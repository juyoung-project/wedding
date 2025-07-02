package kr.or.wds.project.dto.response;

import lombok.Data;

@Data
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;

}
