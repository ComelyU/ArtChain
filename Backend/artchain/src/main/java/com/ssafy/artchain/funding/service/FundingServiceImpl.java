package com.ssafy.artchain.funding.service;


import com.ssafy.artchain.funding.dto.FundingCreateRequestDto;
import com.ssafy.artchain.funding.entity.Funding;
import com.ssafy.artchain.funding.repository.FundingRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundingServiceImpl implements FundingService {

    private final FundingRepository fundingRepository;
    private final EntityManager em;

    @Override
    public int createFunding(FundingCreateRequestDto data) {
        Funding funding = fundingRepository.save(Funding.builder()
                .entId(data.getEntId())
                .name(data.getName())
                .poster(data.getPoster())
                .description(data.getDescription())
                .investmentStructure(data.getInvestmentStructure())
                .estimatedReturn(data.getEstimatedReturn())
                .riskNotice(data.getRiskNotice())
                .progressStatus("모집중")
                .goalCoinCount(data.getGoalCoinCount())
                .nowCoinCount(0L)
                .isFinished(false)
                .contractAddress(data.getContractAddress())
                .attachment(data.getAttachment())
                .isAllow(false)
                .category(data.getCategory())
                .investmentLogs(new ArrayList<>())
                .build());

        // -1 리턴 시, 저장이 되지 않은 것
        // 1 리턴 시, 저장이 된 것

        // 저장 여부는 영속화 여부에 따라 확인한다.
        // 정상적으로 저장되었다면 영속화된 상태일 것!
        if(!em.contains(funding)){
            return -1;
        }
        return 1;
    }

}
