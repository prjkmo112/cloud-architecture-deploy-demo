package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto;

import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.MbtiEnum;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMemberRequestDto(
        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @NotNull
        @Min(value = 1)
        @Max(value = 200)
        Integer age,

        @NotNull
        @Enumerated(EnumType.STRING)
        MbtiEnum mbti
) {

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .age(age)
                .mbti(mbti)
                .build();
    }

}
