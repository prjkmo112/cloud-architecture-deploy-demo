package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service;

import io.github.prjkmo112.cloudarchitecturedeploydemo.common.service.S3Service;
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
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @InjectMocks
    private MemberProfileService memberProfileService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("getProfileImage 프로필 이미지 정상 조회")
    void getProfileImage_success_return() throws MalformedURLException {
        // given
        Member member = MemberFixture.createMember();
        URL downloadUrl = new URL("http://example.com/image.jpg");

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(s3Service.getDownloadUrl(member.getImageUrl())).willReturn(downloadUrl);

        // when
        String result = memberProfileService.getProfileImage(member.getId());

        // then
        assertNotNull(result);
        assertEquals(downloadUrl.toString(), result);
    }

    @Test
    @DisplayName("getProfileImage 예외 발생")
    void getProfileImage_exception() {
        // given
        Member member = MemberFixture.createMember();
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> memberProfileService.getProfileImage(member.getId()));
        assertEquals("Member not found", exception.getMessage());
    }

    @Test
    @DisplayName("uploadImage 프로필 이미지 정상 업로드")
    void uploadImage_success() {
        // given
        Member member = MemberFixture.createMember();
        MultipartFile file = mock(MultipartFile.class);
        String imgKey = "imageKey";

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(s3Service.upload(file)).willReturn(imgKey);

        // when
        String result = memberProfileService.uploadImage(member.getId(), file);

        // then
        assertNotNull(result);
        assertEquals(imgKey, result);
    }
}