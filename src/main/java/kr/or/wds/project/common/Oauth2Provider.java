package kr.or.wds.project.common;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import kr.or.wds.project.entity.MemberEntity;

public enum Oauth2Provider {
    NAVER("naver", (attributes) -> {
		Map<String, Object> jsonMap = (Map<String, Object>) attributes.get("response");
		return MemberEntity.builder()
				.username(String.valueOf(jsonMap.get("name")))
				.email(String.valueOf(jsonMap.get("email"))).build();
	});

	private final String registrationId;
	private final Function<Map<String, Object>, MemberEntity> of;

	Oauth2Provider(String registrationId, Function<Map<String, Object>, MemberEntity> of) {
		this.registrationId = registrationId;
		this.of = of;
	}
	
	public static MemberEntity getOauthInfo(String registrationId, Map<String, Object> attributes) {
		return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
           .of.apply(attributes);
		
	}
}
