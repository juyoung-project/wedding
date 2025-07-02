package kr.or.wds.project.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import kr.or.wds.project.common.Oauth2Provider;
import kr.or.wds.project.entity.MemberEntity;
import kr.or.wds.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Oauth2CustomService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    	@Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = service.loadUser(userRequest); // OAuth2 정보를 가져옵니다.

		Map<String, Object> originAttributes = oAuth2User.getAttributes(); // OAuth2User의 attribute
		System.out.println("1번 먼저 작동 :: > "+originAttributes);
		System.out.println(originAttributes.get("response"));
//		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
//                .getUserInfoEndpoint().getUserNameAttributeName(); // OAuth 로그인 시 키(pk)가 되는 값 
		
		String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인 원천지 (google, kakao, naver)
		MemberEntity member = Oauth2Provider.getOauthInfo(registrationId, originAttributes);
		this.saveOrUpdate(member);
		
		// OAuth2User attributes에 이메일 정보 추가
		Map<String, Object> customAttributes = new HashMap<>(originAttributes);
		customAttributes.put("email", member.getEmail());
		
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("NORMAL") ),
				customAttributes,
				"email");
    }

    private void saveOrUpdate(MemberEntity memberEntity) {
		Optional<MemberEntity> memberOptional = memberRepository.findByEmail(memberEntity.getEmail());
		
		if ( memberOptional.isPresent() ) {
			// 데이터가 있을경우 업데이트 (필요시)
			MemberEntity existingMember = memberOptional.get();
			// 필요한 경우 사용자 정보 업데이트 로직 추가
		} else {
			// 새로운 사용자 등록
			MemberEntity newMember = MemberEntity.builder()
					.email(memberEntity.getEmail())
					.username(memberEntity.getUsername())
					.password(UUID.randomUUID().toString())
                    .status("ACTIVE")
					.role("NORMAL")
					.build();
			memberRepository.save(newMember);
		}
	}

}
