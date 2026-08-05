# Cloud Architecture Deploy Demo

AWS 상에서 안전하게 중단 없이 운영하기 위한 데모 목적의 프로젝트

<!-- TOC -->
* [Cloud Architecture Deploy Demo](#cloud-architecture-deploy-demo)
  * [Tech Stack](#tech-stack)
  * [Concept](#concept)
    * [API 구조](#api-구조)
  * [Work Flow](#work-flow)
    * [1. AWS Budget 설정 <sup>lv 0</sup>](#1-aws-budget-설정-suplv-0sup)
    * [2. 네트워크 구축 및 핵심 기능 배포 <sup>lv 1</sup>](#2-네트워크-구축-및-핵심-기능-배포-suplv-1sup)
      * [2-1. 인프라 구축](#2-1-인프라-구축)
    * [3. API 프로젝트 개발 <sup>lv 1</sup>](#3-api-프로젝트-개발-suplv-1sup)
      * [3-1. API 구조](#3-1-api-구조)
      * [3-2. 운영 설정](#3-2-운영-설정)
    * [4. 인프라 구축 <sup>lv 2</sup>](#4-인프라-구축-suplv-2sup)
      * [4-1. RDS 생성](#4-1-rds-생성)
      * [4-2. Parameter Store 정보 저장](#4-2-parameter-store-정보-저장)
    * [5. 수동 배포 <sup>lv 1</sup>](#5-수동-배포-suplv-1sup)
      * [5-1. Build](#5-1-build)
    * [6. API 실행 <sup>lv 1</sup>](#6-api-실행-suplv-1sup)
    * [7. 프로필 사진 기능 추가와 권한 관리 <sup>lv 3</sup>](#7-프로필-사진-기능-추가와-권한-관리-suplv-3sup)
      * [7-1. S3 Bucket 생성 및 설정](#7-1-s3-bucket-생성-및-설정)
      * [7-2. API 에 이미지 업로드 (S3) 기능 구현](#7-2-api-에-이미지-업로드-s3-기능-구현)
    * [8. Docker & CI/CD Pipeline 구축 <sup>lv 4</sup>](#8-docker--cicd-pipeline-구축-suplv-4sup)
      * [Idea](#idea)
      * [8-1. Docker 이미지 배포 결과](#8-1-docker-이미지-배포-결과)
      * [8-2. EC2 의 자동 배포 (EC2 자동 실행)](#8-2-ec2-의-자동-배포-ec2-자동-실행)
    * [9. 고가용성 아키텍처와 보안 도메인 연결 (ALB + ASG + HTTPS) <sup>lv 5</sup>](#9-고가용성-아키텍처와-보안-도메인-연결-alb--asg--https-suplv-5sup)
      * [9-1. 대상 그룹 (Target Group) 생성](#9-1-대상-그룹-target-group-생성)
      * [9-2. ALB 생성](#9-2-alb-생성)
      * [9-3. ASG 생성](#9-3-asg-생성)
      * [9-4. 도메인 등록 및 연결](#9-4-도메인-등록-및-연결)
<!-- TOC -->

## Tech Stack

![Static Badge](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Static Badge](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

![Static Badge](https://img.shields.io/badge/H2-09476B?style=for-the-badge&logo=h2database&logoColor=white)
![Static Badge](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

![Static Badge](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

![Amazon EC2](./docs/badges/amazon-ec2.svg)
![Amazon ECR](./docs/badges/amazon-ecr.svg)
![Application Load Balancer](./docs/badges/application-load-balancer.svg)
![EC2 Auto Scaling](./docs/badges/ec2-auto-scaling.svg)
![Amazon RDS](./docs/badges/amazon-rds.svg)
![Amazon Route 53](./docs/badges/amazon-route-53.svg)
![AWS Certificate Manager](./docs/badges/aws-certificate-manager.svg)
![Parameter Store](./docs/badges/parameter-store.svg)
![AWS Systems Manager](./docs/badges/aws-systems-manager.svg)

## Concept

- 팀원들의 정보를 저장하고 불러오는 API
- 프로필 사진을 업로드 하는 API

### API 구조

| Method | URI | 설명 |
|--------|-----|------|
| `GET` | `/api/members/{id}` | ID로 멤버 단건 조회 |
| `POST` | `/api/members` | 멤버 생성 |
| `GET` | `/api/members/{id}/profile-image` | 프로필 이미지 Presigned URL 조회 |
| `POST` | `/api/members/{id}/profile-image` | 프로필 이미지 업로드 |
| `GET` | `/actuator/health` | 애플리케이션 헬스체크 | — |

```mermaid
sequenceDiagram
    participant Client
    participant API as Spring Boot API
    participant DB as MySQL (RDS)
    participant S3 as AWS S3

    Note over Client,S3: 멤버 생성
    Client->>API: POST /api/members
    API->>DB: INSERT Member
    DB-->>API: 저장된 Member
    API-->>Client: ApiResponse<MemberDto>

    Note over Client,S3: 멤버 조회
    Client->>API: GET /api/members/{id}
    API->>DB: SELECT Member
    DB-->>API: Member 데이터
    API-->>Client: ApiResponse<MemberDto>

    Note over Client,S3: 프로필 이미지 업로드
    Client->>API: POST /api/members/{id}/profile-image (multipart)
    API->>S3: 이미지 파일 업로드
    S3-->>API: 저장된 파일 Key
    API->>DB: UPDATE profileImageKey
    DB-->>API: 업데이트 완료
    API-->>Client: ApiResponse<MemberProfileResponseDto>

    Note over Client,S3: 프로필 이미지 조회
    Client->>API: GET /api/members/{id}/profile-image
    API->>DB: SELECT profileImageKey
    DB-->>API: profileImageKey
    API->>S3: Presigned URL 생성
    S3-->>API: Presigned URL (7일 유효)
    API-->>Client: ApiResponse<MemberProfileResponseDto>
```

## Work Flow

### 1. AWS Budget 설정 <sup>lv 0</sup>

실수로 비용이 과다하게 발생하는 것을 방지하기 위해 AWS Budget 설정을 하였습니다.

![](docs/images/img.png)

### 2. 네트워크 구축 및 핵심 기능 배포 <sup>lv 1</sup>

#### 2-1. 인프라 구축

- VPC 생성

![](docs/images/img_1.png)

- Public 서브넷에 EC2 생성

![](docs/images/img_2.png)

### 3. API 프로젝트 개발 <sup>lv 1</sup>

#### 3-1. API 구조

- Endpoint

| Method | URI | 설명 |
|--------|-----|------|
| `GET` | `/api/members/{id}` | ID로 멤버 단건 조회 |
| `POST` | `/api/members` | 멤버 생성 |

공통 응답 형식은 `ApiResponse<T>` 를 사용하며, `status`, `message`, `data` 필드를 포함합니다.

```json
{
  "status": 200,
  "message": "정상 조회되었습니다.",
  "data": { ... }
}
```

- Actuator 설정

`spring-boot-starter-actuator` 의존성을 추가하여 애플리케이션 상태를 외부에서 확인할 수 있도록 헬스체크 엔드포인트를 제공합니다.

| Endpoint | 설명 |
|----------|------|
| `GET /actuator/health` | 애플리케이션 상태 확인 |

- 로깅 Filter

`ApiLoggingFilter` (`OncePerRequestFilter` 구현)를 통해 모든 HTTP 요청의 메서드와 URI를 로그로 기록합니다.

```
[API - LOG] GET /api/members/1
[API - LOG] POST /api/members
```

- Exception Handler 처리

`GlobalExceptionHandler` (`@RestControllerAdvice`)에서 예외를 일관된 형식으로 처리합니다.

| 예외 | HTTP 상태 | 설명 |
|------|-----------|------|
| `ApiException` | 예외에 지정된 상태 | 비즈니스 로직 예외 |
| `DataIntegrityViolationException` | `409 Conflict` | DB 제약 조건 위반 |
| `MethodArgumentNotValidException` | `400 Bad Request` | 요청 값 유효성 검사 실패 |
| `Exception` | `500 Internal Server Error` | 예상치 못한 서버 오류 |

#### 3-2. 운영 설정

- Profile 분리

환경별로 설정을 분리하여 로컬 개발과 프로덕션 환경을 독립적으로 관리합니다.

| Profile | 활성화 방법 | DB | ddl-auto |
|---------|------------|-----|----------|
| `local` | `--spring.profiles.active=local` | H2 (파일 기반) | `create` |
| `prod` | `--spring.profiles.active=prod` | MySQL (AWS RDS) | `validate` |

`prod` 에서는 DB 접속 정보를 환경 변수로 주입받아 코드에 민감 정보가 노출되지 않도록 합니다.
이때 AWS 의 Parameter Store 서비스를 활용하여 환경 변수를 안전하게 관리합니다.

### 4. 인프라 구축 <sup>lv 2</sup>

#### 4-1. RDS 생성

Public Subnet 두 개를 묶어 Subnet Group 을 만들어줍니다.

![](docs/images/img_3.png)

> ⚠️ **RDS 는 반드시 Private Subnet** 에 넣어야 합니다.
>
> 다만, 해당 프로젝트는 데모 목적이므로 Public Subnet 에 생성하였습니다.

RDS 는 아래 정보에 맞게 만들었습니다.

- 프리티어에 맞게 인스턴스 1개 (db.t4g.micro)
- MySQL 8.4.9
- EC2 리소스 연결
- 위에서 만든 public subnet 연결

RDS 를 생성하고 나서 확인해보면 EC2 리소스에 정상적으로 연결된 것을 확인할 수 있습니다.

![](docs/images/img_5.png)

#### 4-2. Parameter Store 정보 저장

RDS 접속 정보를 Parameter Store 에 저장합니다.

![](docs/images/img_6.png)

EC2 에서 Parameter Store 에 접근할 수 있는 권한을 주어야 사용할 수 있습니다.
`AmazonSSMReadOnlyAccess` 정책으로 역할을 하나 만든 뒤에 EC2 에 IAM 역할로 연결해줍니다. 

![](docs/images/img_7.png)

### 5. 수동 배포 <sup>lv 1</sup>

#### 5-1. Build

```bash
./gradlew clean bootJar
```

![](docs/images/img_8.png)

빌드된 jar 파일을 ec2 로 옮기고 spring boot 를 실행합니다.

```bash
java -jar app.jar --spring.profiles.active=prod
```

### 6. API 실행 <sup>lv 1</sup>

아래는 EC2 에 직접 배포한 후 API 실행한 결과입니다.


<img width="45%" class="blog-image" src="docs/images/img_9.png" />   <img width="50%" class="blog-image" src="docs/images/img_10.png" />

<img width="45%" class="blog-image" src="docs/images/img_11.png" />   <img width="50%" class="blog-image" src="docs/images/img_12.png" />

아래 이미지는 전체 API 에 대한 테스트입니다.

<img width="40%" class="blog-image" src="docs/images/img_13.png" />

### 7. 프로필 사진 기능 추가와 권한 관리 <sup>lv 3</sup>

#### 7-1. S3 Bucket 생성 및 설정

ParameterStore 읽을 수 있는 정책과 S3 접근 정책을 포함한 role 을 생성하여 이를 EC2 의 IAM 역할로 붙입니다.

![img_14.png](docs/images/img_14.png)

#### 7-2. API 에 이미지 업로드 (S3) 기능 구현

![img_15.png](docs/images/img_15.png)

![img_16.png](docs/images/img_16.png)

> 예시 Presigned URL
> 
> https://sdd-momo-s3.s3.ap-northeast-2.amazonaws.com/uploads/72ea3acc-6340-4130-9b96-f176a73ad4bd_20240208_151814.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260731T154247Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA37TRHJA5PAL7SPDP%2F20260731%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=759fc98f616d469ccd2d90ec9a9eef68face562fb1b5b9aa6e88e84944bd3467
> 
> ![](https://sdd-momo-s3.s3.ap-northeast-2.amazonaws.com/uploads/72ea3acc-6340-4130-9b96-f176a73ad4bd_20240208_151814.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260731T154247Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA37TRHJA5PAL7SPDP%2F20260731%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=759fc98f616d469ccd2d90ec9a9eef68face562fb1b5b9aa6e88e84944bd3467)

### 8. Docker & CI/CD Pipeline 구축 <sup>lv 4</sup>

#### Idea

![](docs/images/img_25.png)

---

ECR 에 업로드하기 위해 AWS 에서 OIDC 를 생성하고 ECR 관리할 수 있는 역할과 연결합니다.

![](docs/images/img_17.png)
![](docs/images/img_18.png)

Github Actions 에서 사용할 값들을 Github Repo 의 Secrets 에 저장합니다.

![](docs/images/img_19.png)

#### 8-1. Docker 이미지 배포 결과

- Github Actions

    https://github.com/prjkmo112/cloud-architecture-deploy-demo/actions/runs/30775010676

- ECR

    ![](docs/images/img_20.png)

- Docker Hub

    https://hub.docker.com/r/ptjkjm1/cloud-architecture-deploy-demo/tags

#### 8-2. EC2 의 자동 배포 (EC2 자동 실행)

8-2-1. EC2 에 SSM Agent 설치 확인

```bash
sudo systemctl status amazon-ssm-agent
```

![](docs/images/img_21.png)

만약 `amazon-ssm-agent` 가 없다면 설치를 해주어야 합니다.
https://docs.aws.amazon.com/ko_kr/systems-manager/latest/userguide/manually-install-ssm-agent-linux.html

8-2-2. SSM (Systems Manager) 설정

`SSM > Fleet Manager > 관리형 노드`에 접속하면 자동으로 Fleet Manager 가 
SSM 사용이 가능한 EC2 인스턴스를 보여줍니다.

> ⚠️ Fleet Manager 처음 세팅하면, 가능한 EC2 인스턴스 리스트를 가져오는데 오래 걸릴 수 있습니다. (대략 5분 정도)

![](docs/images/img_22.png)

8-2-3. EC2 에 Docker 설치

```bash
set -euxo pipefail

dnf install -y docker

systemctl enable --now docker

docker --version
docker ps
```

> ⚠️ **SSM Fleet Manager 의 명령 실행 주체**
> 
> 위 내용은 SSM 의 명령어 실행 기능을 사용한 기준입니다. Fleet Manager 의 명령 실행 주체는 `root` 입니다.
> 따라서 직접 SSH 를 통해 접속한 경우에는 sudo 를 붙여야 합니다.

8-2-4. Github Action 을 통한 배포 실행

- Github Actions

  https://github.com/prjkmo112/cloud-architecture-deploy-demo/actions/runs/30863014250/job/91848999929

- SSM Fleet Manager 의 명령 기록

  ![](docs/images/img_24.png)

- EC2 에서 Docker 컨테이너 실행 확인

  ![](docs/images/img_23.png)

### 9. 고가용성 아키텍처와 보안 도메인 연결 (ALB + ASG + HTTPS) <sup>lv 5</sup>

#### 9-1. 대상 그룹 (Target Group) 생성

- 대상 유형 : 인스턴스
- 상태 검사 : HTTP, /actuator/health, 200 OK (Spring Actuator)

#### 9-2. ALB 생성
![](docs/images/img_26.png)

#### 9-3. ASG 생성

ASG 에서 사용할 EC2 의 템플릿을 먼저 만들어 주어야 합니다.

이때 주의할 점은 EC2 의 시작 템플릿을 만들어 줄 때 시작할 때 
필요한 명령어를 user data 에 넣어주어야 합니다. Docker 를 사용하므로 **docker 설치**, 이미지를 다운로드 받기 위해 **ECR 로그인**,
이미 작동중인 **컨테이너가 있다면 종료 후 재실행**하는 사전작업들이 필요합니다. 

```bash
#!/bin/bash
set -e

# ── 변수 설정 ──
AWS_REGION="***"
AWS_ACCOUNT_ID="***"
ECR_REPOSITORY="***"
CONTAINER_NAME="***"
IMAGE_TAG="***"

ECR_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}"

# ── Docker 설치 ──
dnf update -y
dnf install -y docker
systemctl enable docker
systemctl start docker

# ── ECR 로그인 ──
aws ecr get-login-password --region ${AWS_REGION} | \
  docker login --username AWS --password-stdin \
  ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

# ── 컨테이너 실행 ──
docker pull ${ECR_URI}:${IMAGE_TAG}

# 혹시 같은 이름의 컨테이너가 있으면 제거
docker rm -f "${CONTAINER_NAME}" 2>/dev/null || true

docker run -d -p 8080:8080 \
  --restart unless-stopped \
  --name ${CONTAINER_NAME} \
  -e SPRING_PROFILES_ACTIVE=prod \
  ${ECR_URI}:${IMAGE_TAG}
```

이제 이 템플릿을 이용한 ALB 를 만들어주면 됩니다.

- Public Subnet 에 위치
- 새 보안 그룹 생성 (HTTP, HTTPS 규칙)
- 대상 그룹 : 위에서 만든 대상 그룹

여기까지 완료한 후 `Auto scaling group > 인스턴스 관리 탭` 또는 `대상 그룹 > 등록된 대상`에 들어가면 작동중인 인스턴스를 확인할 수 있습니다.
상태가 Healthy 라고 되어있으면 정상적으로 작동중인 것입니다.

![](docs/images/img_28.png)

이제 API 가 정상적으로 작동하는지 확인하기 위해 actuator 로 접속해보면 됩니다.
Route 53 연결하기 전이라면 ALB 의 DNS 가 URL 이 됩니다. 

![](docs/images/img_27.png)

#### 9-4. 도메인 등록 및 연결

9-4-1. 도메인 등록

Route 53 에 들어가 도메인 구입 후 ACM 에서 인증서를 발급받습니다.

> ACM 등록할 때 도메인을 "spartamo.click", "*.spartamo.click" 두 개를 등록합니다.

등록된 ACM 인증서 세부 정보에서 `도메인 > Route 53 에서 레코드 생성 > 도메인 전체 클릭 > 레코드 생성` 을 진행합니다.
도메인 상태가 `성공` 으로 변경될 때까지 기다려야 합니다. 

> ⚠️ 도메인 상태가 `성공`으로 변경되는 데 약 30분 정도 소요될 수 있습니다. 

9-4-2. Load Balancer 리스너 수정

Load Balancer 의 리스너를 아래처럼 변경해주었습니다.

- HTTP
  - HTTPS 리다이렉트 (Port: 443, Status: 301)
- HTTPS
  - 대상 그룹으로 전달
  - SSL 인증서 -> ACM & 위에서 만든 인증서

9-4-3. Route 53 레코드 연결

- 레코드 유형: A
- 엔드포인트 : Application/Classic Load Balancer 에 대한 별칭
- 위에서 만든 로드밸런서 선택

---

- 최종 ALB 리소스 맵

![](docs/images/img_29.png)

- 도메인 URL

https://spartamo.click

`GET` https://spartamo.click/actuator/health

`GET` https://spartamo.click/actuator/info

`GET` https://spartamo.click/api/members/:id

`POST` https://spartamo.click/api/members
```
{
  "name": "tester1",
  "age": 22,
  "mbti": "INTP"
}
```

`GET` https://spartamo.click/api/members/:id/profile-image

`POST` https://spartamo.click/api/members/:id/profile-image
```
{
  "file": "<file>"
}
```

- 접속 결과

![](docs/images/img_30.png)

![](docs/images/img_31.png)

---

