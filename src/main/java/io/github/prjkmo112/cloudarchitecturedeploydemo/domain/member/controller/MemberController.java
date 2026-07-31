package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.controller;

import io.github.prjkmo112.cloudarchitecturedeploydemo.common.response.ApiResponse;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.CreateMemberRequestDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.MemberDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@PathVariable Long id) {
        MemberDto dto = memberService.getMember(id);
        return ApiResponse.ok("정상 조회되었습니다.", dto);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberDto>> createMember(
            @Valid @RequestBody CreateMemberRequestDto createMemberRequestDto
    ) {
        MemberDto dto = memberService.createMember(createMemberRequestDto);
        return ApiResponse.created("정상 저장되었습니다.", dto);
    }

}
