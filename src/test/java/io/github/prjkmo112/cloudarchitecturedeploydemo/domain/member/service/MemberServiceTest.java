package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service;

import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.CreateMemberRequestDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.MemberDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.dummy.MemberFixture;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import io.github.prjkmo112.cloudarchitecturedeploydemo.exception.ApiException;
import io.github.prjkmo112.cloudarchitecturedeploydemo.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("getMember 정상 조회")
    void getMember_success_return() {
        // given
        Member member = MemberFixture.createMember();
        given(memberRepository.findById(member.getId())).willReturn(java.util.Optional.of(member));

        // when
        MemberDto memberDto = memberService.getMember(member.getId());

        // then
        assertNotNull(memberDto);
        assertEquals(MemberDto.from(member), memberDto);
    }

    @Test
    @DisplayName("getMember 존재하지 않는 회원 조회 시 예외 발생")
    void getMember_notFound_throwException() {
        // given
        Long userId = 999L;
        given(memberRepository.findById(userId)).willReturn(java.util.Optional.empty());

        // when & then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> memberService.getMember(userId)
        );
        assertEquals("Member not found with id: " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("createMember 정상 생성")
    void createMember_success_return() {
        // given
        CreateMemberRequestDto createMemberRequestDto = MemberFixture.createMemberRequestDto();
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberDto memberDto = memberService.createMember(createMemberRequestDto);

        // then
        assertNotNull(memberDto);
        assertEquals(createMemberRequestDto.name(), memberDto.name());
        assertEquals(createMemberRequestDto.age(), memberDto.age());
        assertEquals(createMemberRequestDto.mbti(), memberDto.mbti());
        verify(memberRepository).save(any(Member.class));
    }

}