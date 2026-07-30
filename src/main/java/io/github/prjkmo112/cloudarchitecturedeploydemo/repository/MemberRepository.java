package io.github.prjkmo112.cloudarchitecturedeploydemo.repository;

import io.github.prjkmo112.cloudarchitecturedeploydemo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}