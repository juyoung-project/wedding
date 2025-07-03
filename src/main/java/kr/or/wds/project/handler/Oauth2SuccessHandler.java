package kr.or.wds.project.handler;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.wds.project.common.JwtTokenProvider;
import kr.or.wds.project.dto.response.TokenResponseDto;
import kr.or.wds.project.entity.MemberEntity;
import kr.or.wds.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");

        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "회원 정보를 찾을 수 없습니다.");
            return;
        }

        MemberEntity member = optionalMember.get();

        TokenResponseDto tokenResponseDto = jwtTokenProvider.generateToken(member.getEmail(), member.getRole());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("{\"accessToken\": \"" + tokenResponseDto.getAccessToken() + "\", \"refreshToken\": \"" + tokenResponseDto.getRefreshToken() + "\"}");
    }

}
