package kr.or.wds.project.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.or.wds.project.dto.request.LoginRequest;
import kr.or.wds.project.dto.response.LoginResponse;
import kr.or.wds.project.dto.response.TokenResponseDto;
import kr.or.wds.project.service.LoginService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "로그인", description = "로그인 요청")
    @PostMapping("/api/auth-login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        TokenResponseDto tokenResponse = loginService.login(request);
        return ResponseEntity.ok(new LoginResponse(tokenResponse));
    }
    
    @Operation(summary = "회원가입", description = "회원가입 요청")
    @PostMapping("/api/auth-signup")
    public ResponseEntity<Void> signup() {
        loginService.signup();
        return ResponseEntity.ok().build();
    }
}
