package com.ssafy.artchain.settlement.service;

import com.ssafy.artchain.eventLog.entity.EventLog;
import com.ssafy.artchain.eventLog.repository.EventLogRepository;
import com.ssafy.artchain.eventLog.service.EventLogService;
import com.ssafy.artchain.funding.entity.Funding;
import com.ssafy.artchain.funding.entity.FundingProgressStatus;
import com.ssafy.artchain.funding.repository.FundingRepository;
import com.ssafy.artchain.market.entity.Market;
import com.ssafy.artchain.market.repository.MarketRepository;
import com.ssafy.artchain.member.dto.CustomUserDetails;
import com.ssafy.artchain.member.repository.MemberRepository;
import com.ssafy.artchain.pieceowner.entity.PieceOwner;
import com.ssafy.artchain.pieceowner.repository.PieceOwnerRepository;
import com.ssafy.artchain.s3.S3Service;
import com.ssafy.artchain.settlement.dto.SettlementListItemDto;
import com.ssafy.artchain.settlement.dto.SettlementRequestDto;
import com.ssafy.artchain.settlement.dto.SettlementResponseDto;
import com.ssafy.artchain.settlement.entity.Settlement;
import com.ssafy.artchain.settlement.entity.SettlementStatus;
import com.ssafy.artchain.settlement.repository.SettlementRepository;
import com.ssafy.artchain.sse.dto.SseSettlementAllowResultListDto;
import com.ssafy.artchain.sse.dto.SseSettlementAllowResultListItemDto;
import com.ssafy.artchain.sse.repository.SseRepository;
import com.ssafy.artchain.sse.service.SseService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final FundingRepository fundingRepository;
    private final MarketRepository marketRepository;
    private final MemberRepository memberRepository;
    private final PieceOwnerRepository pieceOwnerRepository;
    private final SseRepository sseRepository;
    private final SseService sseService;
    private final EventLogRepository eventLogRepository;
    private final EventLogService eventLogService;
    private final S3Service s3Service;
    private final EntityManager entityManager;
    private final String ROLE_COMPANY = "ROLE_COMPANY";
    private final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    public Long createSettlementRequest(MultipartFile file, SettlementRequestDto dto, CustomUserDetails member) {
        if (member.getAuthorities().stream().noneMatch(au -> au.getAuthority().equals(ROLE_COMPANY))) {
            return -5L;
        }

        Funding funding = fundingRepository.findById(dto.getFundingId())
                .orElse(null);
        if (funding == null) {
            return -4L;
        }
        if (!member.getId().equals(funding.getEntId())) {
            return -3L;
        }
        if (!funding.getProgressStatus().equals(FundingProgressStatus.PENDING_SETTLEMENT)) {
            return -2L;
        }

        try {
            dto.setSettlementFile(s3Service.upload(file, funding.getName()));
        } catch (IOException e) {
            return -1L;
        }

        Settlement settlement = settlementRepository.save(Settlement.builder()
                .entId(member.getId())
                .fundingId(funding.getId())
                .settlementPrice(dto.getSettlementPrice())
                .returnRate(dto.getReturnRate())
                .depositDate(dto.getDepositDate())
                .settlementFile(dto.getSettlementFile())
                .status(SettlementStatus.REQUEST)
                .build());
        if (!entityManager.contains(settlement)) {
            return 0L;
        }

        return settlement.getId();
    }

    @Override
    public List<SettlementListItemDto> getSettlementRequestList(CustomUserDetails member) {
        if (member.getAuthorities().stream().noneMatch(au -> au.getAuthority().equals(ROLE_ADMIN))) {
            return null;
        }

        return settlementRepository.getSettlementList(SettlementStatus.REQUEST);
    }

    @Override
    public SettlementResponseDto getSettlement(Long settlementId, CustomUserDetails member) {
        SettlementResponseDto settlement = settlementRepository.getSettlement(settlementId);
        if (settlement == null ||
                (member.getAuthorities().stream().noneMatch(au -> au.getAuthority().equals(ROLE_ADMIN)) &&
                        !settlement.getEntId().equals(member.getId()))
        ) {
            return null;
        }

        return settlement;
    }

    @Override
    @Transactional
    public int updateSettlementStatus(Long settlementId, String status, CustomUserDetails member) {
        if (member.getAuthorities().stream().noneMatch(au -> au.getAuthority().equals(ROLE_ADMIN))) {
            return -3;
        }

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElse(null);
        if (settlement == null) {
            return -2;
        }

        String upperStatus = status.toUpperCase(Locale.ROOT);
        if (Stream.of(SettlementStatus.values())
                .noneMatch(sts -> sts.name().equals(upperStatus))) {
            return -1;
        }

        settlement.updateStatus(SettlementStatus.valueOf(status.toUpperCase(Locale.ROOT)));

        Funding funding = fundingRepository.findById(settlement.getFundingId())
                .orElse(null);
        if (funding != null && SettlementStatus.ALLOW.name().equals(upperStatus)) {
            List<Market> listedMarketList = marketRepository.findAllByFundingIdAndStatus(funding.getId(), "LISTED");

            listedMarketList.forEach(item -> {
                PieceOwner sellerPieceInfo = pieceOwnerRepository.findPieceOwnerByMemberIdAndFundingId(item.getSellerId(), funding.getId());
                if (sellerPieceInfo != null) {
                    sellerPieceInfo.updatePieceCount(sellerPieceInfo.getPieceCount() + item.getPieceCount());
                }

                item.updateStatus("UNLISTED");
            });

            funding.updateProgressStatus(FundingProgressStatus.SETTLED);

            List<PieceOwner> pieceOwnerList = pieceOwnerRepository.findAllByFundingId(funding.getId());

            List<SseSettlementAllowResultListItemDto> pieceOwnerInfoList = new ArrayList<>();
            pieceOwnerList.forEach(po -> {
                BigDecimal resultCoinCount = new BigDecimal(po.getPieceCount())
                        .multiply(new BigDecimal(100 + settlement.getReturnRate()))
                        .divide(new BigDecimal(100), 0, RoundingMode.FLOOR); // 양수값만 나오기 때문에 RoundingMode.DOWN 사용해도 무방.
                memberRepository.findById(po.getMemberId()).ifPresent(owner ->
                        pieceOwnerInfoList.add(new SseSettlementAllowResultListItemDto(owner.getWalletAddress(), resultCoinCount))
                );
            });

            String eventTypeName = "settlementAllow";
            SseSettlementAllowResultListDto eventContent = new SseSettlementAllowResultListDto(funding.getContractAddress(), funding.getNowCoinCount(), pieceOwnerInfoList);

            eventLogRepository.save(EventLog.builder()
                    .eventDate(LocalDateTime.now())
                    .eventType(eventTypeName)
                    .eventContent(eventLogService.convertDtoToJson(eventContent))
                    .build());

            String eventId = "ADMIN";
            sseRepository.findById(eventId)
                    .ifPresent(sseEmitter -> sseService.send(eventContent, eventId, sseEmitter, eventTypeName));
        }

        return 1;
    }

    @Override
    public int deleteSettlement(Long settlementId, CustomUserDetails member) {
        if (member.getAuthorities().stream().noneMatch(au -> au.getAuthority().equals(ROLE_ADMIN))) {
            return -2;
        }

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElse(null);
        if (settlement == null) {
            return -1;
        }

        settlementRepository.delete(settlement);
        return 1;
    }
}
