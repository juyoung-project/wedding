package kr.or.wds.project.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.or.wds.project.common.JwtTokenProvider;
import kr.or.wds.project.dto.request.LoginRequest;
import kr.or.wds.project.dto.response.TokenResponseDto;
import kr.or.wds.project.entity.MemberEntity;
import kr.or.wds.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService  {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public TokenResponseDto login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(request.getId(), authentication.getAuthorities().toString());
        return tokenResponse;
    }

    public void signup() {
        MemberEntity member = MemberEntity.builder()
            .email("a")
            .password(passwordEncoder.encode("1234"))
            .username("test")
            .role("USER")
            .status("ACTIVE")
            .build();
        memberRepository.save(member);
    }

}
