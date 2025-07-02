package kr.or.wds.project.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/test")
@Tag(name = "테스트", description = "테스트용 API")
public class TestController {

    @Operation(summary = "기본 테스트", description = "서버가 정상 작동하는지 확인")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("서버가 정상 작동 중입니다!");
    }

    @Operation(summary = "인증 테스트", description = "JWT 토큰이 유효한지 확인")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/auth")
    public ResponseEntity<String> authTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("인증된 사용자: " + authentication.getName());
        }
        return ResponseEntity.ok("인증되지 않은 사용자");
    }

    @Operation(summary = "OAuth 로그인 링크", description = "네이버 OAuth 로그인 링크")
    @GetMapping("/oauth-link")
    public ResponseEntity<String> getOAuthLink() {
        String oauthUrl = "http://localhost:8090/oauth2/authorization/naver";
        return ResponseEntity.ok("네이버 로그인 링크: " + oauthUrl);
    }
}
