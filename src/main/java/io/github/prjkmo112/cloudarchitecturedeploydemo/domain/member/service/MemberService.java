package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service;

import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.CreateMemberRequestDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.MemberDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import io.github.prjkmo112.cloudarchitecturedeploydemo.exception.ApiException;
import io.github.prjkmo112.cloudarchitecturedeploydemo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException("Member not found with id: " + id, HttpStatus.NOT_FOUND));

        return MemberDto.from(member);
    }

    public MemberDto createMember(CreateMemberRequestDto createMemberRequestDto) {
        Member member = createMemberRequestDto.toEntity();
        Member savedMember = memberRepository.save(member);

        return MemberDto.from(savedMember);
    }

}
