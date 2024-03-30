package com.ssafy.artchain.member.service;

import com.ssafy.artchain.connectentity.entity.InvestmentLog;
import com.ssafy.artchain.connectentity.repository.InvestmentLogRepository;
import com.ssafy.artchain.funding.repository.FundingRepository;
import com.ssafy.artchain.jwt.JwtUtil;
import com.ssafy.artchain.jwt.entity.RefreshToken;
import com.ssafy.artchain.jwt.repository.RefreshRepository;
import com.ssafy.artchain.market.entity.Market;
import com.ssafy.artchain.market.repository.MarketRepository;
import com.ssafy.artchain.member.dto.CustomUserDetails;
import com.ssafy.artchain.member.dto.request.CompanyMemberRegistRequestDto;
import com.ssafy.artchain.member.dto.request.MemberRegistRequestDto;
import com.ssafy.artchain.member.dto.request.MemberWalletInfoRequestDto;
import com.ssafy.artchain.member.dto.response.*;
import com.ssafy.artchain.member.entity.Member;
import com.ssafy.artchain.member.entity.Permission;
import com.ssafy.artchain.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
// @Transactional 어노 테이션이 없는 메소드는 읽기 전용
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final InvestmentLogRepository investmentLogRepository;
    private final MarketRepository marketRepository;
    private final RefreshRepository refreshRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public String refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        //get refresh token
        String refresh = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return "null";
        }
        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return "expired";
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return "invalid";
        }
        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            return "not in DB";
        }

        String memberId = jwtUtil.getMemberId(refresh);
        String authority = jwtUtil.getAuthority(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", memberId, authority, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", memberId, authority, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(memberId, newRefresh, 86400000L);

        //response
        httpServletResponse.setHeader("Authorization", newAccess);
        httpServletResponse.addCookie(createCookie("refresh", newRefresh));
        return "Authorization";
    }

    @Transactional
    @Override
    public void companyJoin(CompanyMemberRegistRequestDto companyDto) {
        Member member = Member.builder()
                .memberId(companyDto.getMemberId())
                .password(bCryptPasswordEncoder.encode(companyDto.getPassword()))
                .permission(Permission.HOLD)
                .name(companyDto.getName())
                .bankAccount(companyDto.getBankAccount())
                .bankName(companyDto.getBankName())
                .email(companyDto.getEmail())
                .tel(companyDto.getTel())
                .businessRegistrationNumber(companyDto.getBusinessRegistrationNumber())
                .authority("ROLE_COMPANY")
                .isDeleted(false)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void memberJoin(MemberRegistRequestDto memberDto) {
        Member member = Member.builder()
                .memberId(memberDto.getMemberId())
                .password(bCryptPasswordEncoder.encode(memberDto.getPassword()))
                .permission(Permission.Y)
                .name(memberDto.getName())
                .bankAccount(memberDto.getBankAccount())
                .bankName(memberDto.getBankName())
                .email(memberDto.getEmail())
                .authority("ROLE_USER")
                .isDeleted(false)
                .build();

        memberRepository.save(member);

    }

    @Override
    public MemberUserResponseDto getUserInfo(CustomUserDetails customMember) {
        Member member = memberRepository.findByMemberId(customMember.getUsername())
                .orElseThrow(() -> new NoSuchElementException("MEMBER NOT FOUND"));
        return new MemberUserResponseDto(member);
    }

    @Override
    public MemberUserMypageResponseListDto getUserMypage(CustomUserDetails customMember) {
        Member member = memberRepository.findByMemberId(customMember.getUsername())
                .orElseThrow(() -> new NoSuchElementException("MEMBER NOT FOUND"));

        List<MemberUserMypageResponseDto> list = memberRepository.memberMypage(member.getId());
        int count = 0;
        for (MemberUserMypageResponseDto dto : list) {
            count += dto.getProgressCount();
        }

        return new MemberUserMypageResponseListDto(list, count);
    }

    @Override
    public MemberComMypageResponseDto getComMypage(CustomUserDetails customCompany) {
        Member company = memberRepository.findByMemberId(customCompany.getUsername())
                .orElseThrow(() -> new NoSuchElementException("COMPANY NOT FOUND"));
        MemberComMypageDto comDto = MemberComMypageDto.builder()
                .id(company.getId())
                .name(company.getName())
                .build();

        List<FundingComMypageDto> list = memberRepository.comMypage(company.getId());


        return new MemberComMypageResponseDto(comDto, list);
    }

    @Override
    public MemberMainUserInfoResponseDto getMainLoginUserInfo(CustomUserDetails member) {
        Member memberEntity = memberRepository.findById(member.getId())
                .orElseThrow(() -> new NoSuchElementException("MEMBER NOT FOUND"));

        return new MemberMainUserInfoResponseDto(memberEntity);
    }

    @Override
    public boolean isExistsMemberId(String checkId) {
        return memberRepository.existsByMemberId(checkId);
    }

    @Override
    public List<MemberPermissionResponseDto> getComPermissionList() {
        Permission ps = Permission.HOLD;
        String role = "ROLE_COMPANY";
        List<Member> list = memberRepository.findAllByPermissionAndAuthority(ps, role);

        return list.stream().map(MemberPermissionResponseDto::new).toList();
    }

    @Transactional
    @Override
    public void putPermission(Long memberId, String permissionFlag) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("MEMBER NOT FOUND"));
        if (Permission.valueOf(permissionFlag) == Permission.N) {
            memberRepository.delete(member);
        } else {
            member.updatePermission(Permission.valueOf(permissionFlag));
            memberRepository.save(member);
        }

    }

    @Transactional
    @Override
    public void putMemberWalletInfo(CustomUserDetails member, MemberWalletInfoRequestDto requestDto) {
        Member memberEntity = memberRepository.findById(member.getId())
                .orElseThrow(() -> new NoSuchElementException("MEMBER NOT FOUND"));

        memberEntity.updateWalletInfo(requestDto);
        System.out.println(requestDto);
        memberRepository.save(memberEntity);
    }

    @Override
    public List<MemberMyTradeDropDownResponseDto> getMyTradeDropDownList(CustomUserDetails customMember) {
        List<MemberMyTradeDropDownResponseDto> list = fundingRepository.findAllByEntIdOrSellerIdOrBuyerId(customMember.getId());

        return list;
    }

    @Override
    public List<MemberMyTradeResponseDto> getMyTradeList(CustomUserDetails customMember,
                    Long fundingId, String filterFlag, Pageable pageable) {
        Long memberId = customMember.getId();
        Page<InvestmentLog> investmentLogPage;
        Page<Market> marketPage;
        List<MemberMyTradeResponseDto> list = new ArrayList<>();

        System.out.println("filterFlag : " + filterFlag);

        if(filterFlag.equals("ALL")) {
            investmentLogPage = investmentLogRepository.findAllByFundingIdAndMemberIdOrderByCreatedAt(fundingId, memberId, pageable);
            marketPage = marketRepository.findAllMarketHistory(fundingId, memberId,pageable);

            for(InvestmentLog entity: investmentLogPage){
                list.add(new MemberMyTradeResponseDto(entity));
            }
            for(Market entity: marketPage){
                list.add(new MemberMyTradeResponseDto(entity, memberId));
            }
            // createdAt 기준으로 내림차순 정렬 (null 안전 포함)
            Collections.sort(list, Comparator.comparing(MemberMyTradeResponseDto::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        }
        else if(filterFlag.equals("투자")) {
            investmentLogPage = investmentLogRepository.findAllByFundingIdAndMemberIdOrderByCreatedAt(fundingId, memberId, pageable);

            for(InvestmentLog entity: investmentLogPage) {
                list.add(new MemberMyTradeResponseDto(entity));
            }
        }
        else if(filterFlag.equals("거래")) {
            marketPage = marketRepository.findAllMarketHistory(fundingId, memberId, pageable);
            for(Market entity: marketPage){
                list.add(new MemberMyTradeResponseDto(entity, memberId));
            }
        }
        else if(filterFlag.equals("판매중")) {
            marketPage = marketRepository.findAllSelling(fundingId, memberId, pageable);
            for(Market entity: marketPage){
                list.add(new MemberMyTradeResponseDto(entity, memberId));
            }
        }
//        else {
//            System.out.println("예외나버림");
//            throw new Exception("잘못된 filter Flag입니다.");
//        }

        return list;
    }

    @Transactional
    protected void addRefreshEntity(String memberId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshEntity = RefreshToken.builder()
                .memberId(memberId)
                .refresh(refresh)
                .expiration(date.toString())
                .build();
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
//    https에서만 쓰게 할 수 있는 코드, localhost환경에서 개발 중이므로 주석
//    cookie.setSecure(true);
        cookie.setPath("/api");
//    자바스크립트 접근 불가능
        cookie.setHttpOnly(true);
        return cookie;
    }
}
