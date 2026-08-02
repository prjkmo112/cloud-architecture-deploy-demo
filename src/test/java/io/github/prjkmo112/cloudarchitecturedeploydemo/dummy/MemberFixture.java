package io.github.prjkmo112.cloudarchitecturedeploydemo.dummy;

import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.CreateMemberRequestDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.MbtiEnum;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;

public class MemberFixture {

    public static Member createMember() {
        return Member.builder()
                .id(1L)
                .name("Test Name")
                .age(25)
                .mbti(MbtiEnum.INTP)
                .build();
    }

    public static CreateMemberRequestDto createMemberRequestDto() {
        return new CreateMemberRequestDto(
                "Test Name",
                25,
                MbtiEnum.INTP
        );
    }

}
