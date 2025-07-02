package kr.or.wds.project.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.or.wds.project.common.JwtTokenProvider;
import kr.or.wds.project.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "OAuth2 소셜 로그인 API")
public class OAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "OAuth 로그인 성공", description = "네이버 OAuth 로그인 성공 후 JWT 토큰 발급")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환"),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @GetMapping("/success")
    public ResponseEntity<TokenResponseDto> oauthSuccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            
            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                // 카카오나 네이버의 경우 다른 방식으로 이메일 추출
                Map<String, Object> attributes = oAuth2User.getAttributes();
                if (attributes.containsKey("response")) {
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    email = (String) response.get("email");
                }
            }
            
            if (email != null) {
                // JWT 토큰 생성
                TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(email, "NORMAL");
                return ResponseEntity.ok(tokenResponse);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "OAuth 로그인 실패", description = "네이버 OAuth 로그인 실패")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @GetMapping("/failure")
    public ResponseEntity<String> oauthFailure() {
        return ResponseEntity.badRequest().body("OAuth 로그인에 실패했습니다.");
    }

    @Operation(summary = "OAuth 정보 확인", description = "현재 OAuth 사용자 정보 확인")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getOAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return ResponseEntity.ok(oAuth2User.getAttributes());
        }
        
        return ResponseEntity.ok(Map.of("message", "OAuth 사용자가 아닙니다."));
    }
} 