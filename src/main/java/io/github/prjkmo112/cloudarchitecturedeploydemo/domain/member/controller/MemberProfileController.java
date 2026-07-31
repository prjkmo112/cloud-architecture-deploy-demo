package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.controller;

import io.github.prjkmo112.cloudarchitecturedeploydemo.common.response.ApiResponse;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.dto.MemberProfileResponseDto;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service.MemberProfileService;
import io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/members/{id}")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    @GetMapping("/profile-image")
    public ResponseEntity<ApiResponse<MemberProfileResponseDto>> getProfileImage(@PathVariable Long id) {
        String url = memberProfileService.getProfileImage(id);
        MemberProfileResponseDto dto = MemberProfileResponseDto.builder()
                .url(url)
                .build();

        return ApiResponse.ok("프로필 이미지가 조회되었습니다.", dto);
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<MemberProfileResponseDto>> updateProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String url = memberProfileService.uploadImage(id, file);
        MemberProfileResponseDto dto = MemberProfileResponseDto.builder()
                .url(url)
                .build();

        return ApiResponse.ok("프로필 이미지가 업데이트되었습니다.", dto);
    }

}
