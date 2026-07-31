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

<img src="docs/images/img.png" />

### 2. 네트워크 구축 및 핵심 기능 배포

#### 2-1. 인프라 구축

- VPC 생성

<img src="docs/images/img_1.png" />

- Public 서브넷에 EC2 생성

<img src="docs/images/img_2.png" />

### 3. API 프로젝트 개발

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

### 4. 인프라 구축

#### 4-1. RDS 생성

Public Subnet 두 개를 묶어 Subnet Group 을 만들어줍니다.

<img src="docs/images/img_3.png" />

> ⚠️ **RDS 는 반드시 Private Subnet** 에 넣어야 합니다.
>
> 다만, 해당 프로젝트는 데모 목적이므로 Public Subnet 에 생성하였습니다.

RDS 는 아래 정보에 맞게 만들었습니다.

- 프리티어에 맞게 인스턴스 1개 (db.t4g.micro)
- MySQL 8.4.9
- EC2 리소스 연결
- 위에서 만든 public subnet 연결

RDS 를 생성하고 나서 확인해보면 EC2 리소스에 정상적으로 연결된 것을 확인할 수 있습니다.

<img src="docs/images/img_5.png" />

#### 4-2. Parameter Store 정보 저장

RDS 접속 정보를 Parameter Store 에 저장합니다.

<img src="docs/images/img_6.png" />

EC2 에서 Parameter Store 에 접근할 수 있는 권한을 주어야 사용할 수 있습니다.
`AmazonSSMReadOnlyAccess` 정책으로 역할을 하나 만든 뒤에 EC2 에 IAM 역할로 연결해줍니다. 

<img src="docs/images/img_7.png" />

### 5. 수동 배포

#### 5-1. Build

```bash
./gradlew clean bootJar
```

<img src="docs/images/img_8.png" />

빌드된 jar 파일을 ec2 로 옮기고 spring boot 를 실행합니다.

```bash
java -jar app.jar --spring.profiles.active=prod
```

### 6. API 실행

아래는 EC2 에 직접 배포한 후 API 실행한 결과입니다.


<img width="45%" class="blog-image" src="docs/images/img_9.png" />   <img width="50%" class="blog-image" src="docs/images/img_10.png" />

<img width="45%" class="blog-image" src="docs/images/img_11.png" />   <img width="50%" class="blog-image" src="docs/images/img_12.png" />

아래 이미지는 전체 API 에 대한 테스트입니다.

<img width="40%" class="blog-image" src="docs/images/img_13.png" />

### 7. 프로필 사진 기능 추가와 권한 관리

#### 7-1. S3 Bucket 생성 및 설정

ParameterStore 읽을 수 있는 정책과 S3 접근 정책을 포함한 role 을 생성하여 이를 EC2 의 IAM 역할로 붙입니다.

![img_14.png](docs/images/img_14.png)

#### 7-2. API 에 이미지 업로드 (S3) 기능 구현

![img_15.png](docs/images/img_15.png)

![img_16.png](docs/images/img_16.png)

> 예시 Presigned URL
> 
> https://sdd-momo-s3.s3.ap-northeast-2.amazonaws.com/uploads/f179d082-3606-4506-8879-840b29339795_20240209_121854.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260731T152707Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA37TRHJA5PAL7SPDP%2F20260731%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=2ff2a5fd5a5e18fe5ea4dd6c9c8c594f163d0d7025302efc12cb3a19fdee7be9
> 
> ![](https://sdd-momo-s3.s3.ap-northeast-2.amazonaws.com/uploads/f179d082-3606-4506-8879-840b29339795_20240209_121854.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260731T152707Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIA37TRHJA5PAL7SPDP%2F20260731%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=2ff2a5fd5a5e18fe5ea4dd6c9c8c594f163d0d7025302efc12cb3a19fdee7be9)

