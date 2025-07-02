package kr.or.wds.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "member",
       indexes = {
           @Index(name = "idx_member_email", columnList = "email"),
           @Index(name = "idx_member_role", columnList = "role"),
           @Index(name = "idx_member_status", columnList = "status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "member_email_key", columnNames = "email")
       }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "회원 아이디")
    @Column(name = "member_id")
    private Integer memberId;

    @Column(length = 50, nullable = false)
    @Schema(description = "회원 이름")
    private String username;

    @Column(length = 100, nullable = false, unique = true)
    @Schema(description = "회원 이메일")
    private String email;

    @Column(length = 255, nullable = false)
    @Schema(description = "회원 비밀번호")
    private String password;

    @Column(length = 15)
    @Schema(description = "회원 연락처")
    private String contactNumber;

    @Column(length = 255, nullable = false)
    @Schema(description = "회원 권한")
    private String role;

    @Column(length = 20, nullable = false)
    @Schema(description = "회원 상태")
    private String status = "ACTIVE";

    @Column(name = "create_time", updatable = false)
    @Schema(description = "회원 생성일")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Schema(description = "회원 수정일")
    private LocalDateTime updateTime;

    @Column(name = "create_member_id")
    @Schema(description = "회원 생성자")
    private Long createMemberId;

    @Column(name = "update_member_id")
    @Schema(description = "회원 수정자")
    private Long updateMemberId;

    @Column(name = "del_yn", length = 1)
    @Schema(description = "회원 삭제 여부")
    private String delYn = "N";

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}