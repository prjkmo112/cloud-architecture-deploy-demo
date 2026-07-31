package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberProfileResponseDto(
        String url
) {
}
