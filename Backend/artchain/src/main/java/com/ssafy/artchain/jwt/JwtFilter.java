package com.ssafy.artchain.jwt;

import com.ssafy.artchain.jwt.response.StatusCode;
import com.ssafy.artchain.member.dto.CustomUserDetails;
import com.ssafy.artchain.member.entity.Member;
import com.ssafy.artchain.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JwtFilter active");
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            log.error("access없대");
            filterChain.doFilter(request, response);

            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(StatusCode.FAIL_EXPIRED_ACCESS_TOKEN.getStatus());
            return;
        }

// 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            log.error("access아니래");

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(StatusCode.FAIL_INVAILD_ACCESS_TOKEN.getStatus());
            return;
        }
        String memberId = jwtUtil.getMemberId(accessToken);
        String authority = jwtUtil.getAuthority(accessToken);

        log.info("memberId : " + memberId);
        log.info("authority : " + authority);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("NOT FOUND MEMBER"));

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

//        OAuth2 JWTFilter
        //userDTO를 생성하여 값 set
//        MemberDto memberDto = new MemberDto();
//        memberDto.setId(memberId);
//        memberDto.setAuthority(authority);
//
//        //UserDetails에 회원 정보 객체 담기
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(memberDto);
//
//        //스프링 시큐리티 인증 토큰 생성
//        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
//        //세션에 사용자 등록
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        log.info("JwtFilter 끝");

        filterChain.doFilter(request, response);
    }
}
