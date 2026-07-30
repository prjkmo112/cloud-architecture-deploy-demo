package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto;

import io.github.prjkmo112.cloudarchitecturedeploydemo.common.dto.AuditingDtoIntf;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.MbtiEnum;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberDto(
        Long id,
        String name,
        Integer age,
        MbtiEnum mbti,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) implements AuditingDtoIntf {

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .mbti(member.getMbti())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }

}
