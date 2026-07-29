<style>
.blog-image {
  margin: 0 auto;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}
</style>

# Cloud Architecture Deploy Demo

AWS 상에서 안전하게 중단 없이 운영하기 위한 데모 목적의 프로젝트

## Tech Stack

![Static Badge](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Static Badge](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

![Static Badge](https://img.shields.io/badge/H2-09476B?style=for-the-badge&logo=h2database&logoColor=white)
![Static Badge](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

## Concept

- 팀원들의 정보를 저장하고 불러오는 API
- 프로필 사진을 업로드 하는 API

## Work Flow

### 1. AWS Budget 설정

실수로 비용이 과다하게 발생하는 것을 방지하기 위해 AWS Budget 설정을 하였습니다.

<img width="70%" class="blog-image" src="img.png" />

### 2. 네트워크 구축 및 핵심 기능 배포

#### 2-1. 인프라 구축

- VPC 생성

<img width="70%" class="blog-image" src="img_1.png" />

- Public 서브넷에 EC2 생성

<img width="70%" class="blog-image" src="img_2.png" />