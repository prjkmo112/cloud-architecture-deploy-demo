package io.github.prjkmo112.cloudarchitecturedeploydemo.domain.member.service;

import io.github.prjkmo112.cloudarchitecturedeploydemo.common.service.S3Service;
import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import io.github.prjkmo112.cloudarchitecturedeploydemo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;

    private final S3Service s3Service;

    public String getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        String imgKey = member.getImageUrl();
        return s3Service.getDownloadUrl(imgKey).toString();
    }

    public String uploadImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        String imgKey = s3Service.upload(file);
        member.setImageUrl(imgKey);
        memberRepository.save(member);
        return imgKey;
    }

}
