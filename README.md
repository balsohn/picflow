# Picflow

사진이 흐르는 소셜 미디어 플랫폼 - Spring Boot 기반 백엔드 API

## 📋 프로젝트 개요

Picflow는 사용자들이 이미지와 텍스트를 포함한 게시물을 공유하고, 다른 사용자와 소통할 수 있는 소셜 미디어 플랫폼입니다. Instagram과 유사한 기능을 제공하며, RESTful API로 설계되었습니다.

<details>
<summary>🛠 기술 스택</summary>

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Database**: MySQL 8.0+ / MariaDB
- **Build Tool**: Gradle 8.14
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Password Encryption**: BCrypt
- **Validation**: Bean Validation (Hibernate Validator)

</details>

<details>
<summary>🚀 주요 기능</summary>

### 👤 사용자 관리 (User Management)
- 회원가입 / 로그인 / 로그아웃
- JWT 기반 인증 시스템
- 프로필 관리 (사용자명, 프로필 이미지, 자기소개)
- 비밀번호 변경
- 회원 탈퇴 (소프트 삭제)
- 사용자 검색 (사용자명, 이메일)

### 📝 게시물 관리 (Post Management)
- 게시물 CRUD (생성, 조회, 수정, 삭제)
- 이미지 업로드 지원
- 게시물 검색 (제목, 내용, 날짜)
- 페이지네이션 지원
- 뉴스피드 (팔로우한 사용자들의 게시물)

### 💬 댓글 시스템 (Comment System)
- 댓글 CRUD
- 댓글 페이지네이션
- 작성자 및 게시물 소유자만 댓글 수정/삭제 가능

### ❤️ 좋아요 시스템 (Like System)
- 게시물 좋아요/좋아요 취소
- 좋아요 수 실시간 업데이트
- 중복 좋아요 방지
- 자신의 게시물 좋아요 방지

### 👥 팔로우 시스템 (Follow System)
- 사용자 팔로우/언팔로우
- 팔로워/팔로잉 목록 조회
- 팔로우 수 통계
- 중복 팔로우 방지

### 📁 파일 관리 (File Management)
- 이미지 파일 업로드
- 파일 형식 검증 (jpg, jpeg, png, gif)
- 파일 크기 제한
- 고유한 파일명 생성 (UUID)

</details>

<details>
<summary>📁 프로젝트 구조</summary>

```
src/main/java/com/example/picflow/
├── auth/                    # 인증 관련
│   ├── controller/         # 인증 컨트롤러
│   ├── dto/               # 인증 DTO
│   └── service/           # 인증 서비스
├── user/                   # 사용자 관리
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── post/                   # 게시물 관리
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── comment/                # 댓글 관리
│   └── ...
├── like/                   # 좋아요 관리
│   └── ...
├── follow/                 # 팔로우 관리
│   └── ...
├── file/                   # 파일 관리
│   └── ...
└── global/                 # 공통 설정
    ├── config/            # 설정 클래스
    ├── exception/         # 예외 처리
    ├── filter/            # 필터
    └── util/              # 유틸리티
```

</details>

<details>
<summary>🗄 데이터베이스 스키마</summary>

### 주요 테이블
- **users**: 사용자 정보
- **posts**: 게시물 정보
- **comments**: 댓글 정보
- **likes**: 좋아요 정보
- **follows**: 팔로우 관계

### ERD 특징
- 외래키 제약조건 설정
- 복합 유니크 키로 중복 방지
- 트리거를 통한 자동 카운트 업데이트
- 인덱스 최적화

</details>

<details>
<summary>⚙️ 설치 및 실행</summary>

### 1. 사전 요구사항
- Java 17 이상
- MySQL 8.0+ 또는 MariaDB
- Gradle 8.0+ (또는 Gradle Wrapper 사용)

### 2. 데이터베이스 설정
```sql
-- MySQL/MariaDB에서 실행
CREATE DATABASE picflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

프로젝트 루트의 `picflow.sql` 파일을 실행하여 테이블과 샘플 데이터를 생성합니다.

### 3. 환경 설정
`src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 수정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/picflow?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 4. 애플리케이션 실행
```bash
# Gradle Wrapper 사용 (권장)
./gradlew bootRun

# 또는 직접 Gradle 사용
gradle bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

</details>

<details>
<summary>🔐 보안 설정</summary>

### JWT 인증
- Bearer Token 방식
- 토큰 만료 시간: 24시간
- 자동 갱신 미지원 (수동 재로그인 필요)

### 비밀번호 정책
- 최소 8자 이상
- 대문자, 소문자, 숫자, 특수문자 각각 최소 1개 포함

### 파일 업로드 제한
- 허용 형식: jpg, jpeg, png, gif
- 최대 크기: 10MB
- 저장 위치: `./uploads` (설정 가능)

</details>

<details>
<summary>🧪 테스트</summary>

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "클래스명"

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

</details>

<details>
<summary>📝 설정 정보</summary>

### 프로파일 설정
- **local**: 개발 환경 (기본값)
- **prod**: 운영 환경

### 주요 설정값
```yaml
jwt:
  secret: "your-secret-key"
  expiration: 86400000  # 24시간

file:
  upload-dir: "./uploads"

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 15MB
```

</details>

<details>
<summary>🔧 주요 의존성</summary>

```gradle
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    
    // Database
    runtimeOnly 'com.mysql:mysql-connector-j'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client:3.1.4'
    
    // Password Encryption
    implementation 'at.favre.lib:bcrypt:0.10.2'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

</details>

---

# 📚 API 명세서

<details>
<summary>📋 기본 정보</summary>

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **인증 방식**: Bearer Token (JWT)
- **날짜 형식**: ISO 8601 (`yyyy-MM-ddTHH:mm:ss`)

## 🔐 인증 헤더

대부분의 API는 JWT 토큰이 필요합니다.

```
Authorization: Bearer {token}
```

## 📝 공통 응답 형식

```json
{
  "message": "응답 메시지",
  "data": "실제 데이터",
  "errorCode": "에러 코드 (에러 시에만)"
}
```

</details>

<details>
<summary>👤 인증 API (Authentication)</summary>

## 1. 회원가입

**POST** `/auth/signup`

### Request
```json
{
  "email": "user@example.com",
  "username": "username",
  "password": "Password123!"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | ✅ | 이메일 (이메일 형식) |
| username | String | ✅ | 사용자명 (2-10자) |
| password | String | ✅ | 비밀번호 (8자 이상, 대소문자+숫자+특수문자) |

### Response
```json
{
  "message": "회원가입이 완료되었습니다."
}
```

### Error Response
```json
{
  "message": "이미 존재하는 이메일입니다.",
  "errorCode": "DUPLICATE_EMAIL"
}
```

---

## 2. 로그인

**POST** `/auth/login`

### Request
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

### Response
```json
{
  "message": "로그인이 완료되었습니다.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "username"
  }
}
```

### Error Response
```json
{
  "message": "비밀번호가 일치하지 않습니다.",
  "errorCode": "INVALID_PASSWORD"
}
```

---

## 3. 회원탈퇴

**DELETE** `/auth/withdraw`

🔒 **인증 필요**

### Request
```json
{
  "password": "Password123!"
}
```

### Response
```json
{
  "message": "회원탈퇴가 완료되었습니다."
}
```

</details>

<details>
<summary>👥 사용자 API (Users)</summary>

## 1. 내 프로필 조회

**GET** `/users/profile`

🔒 **인증 필요**

### Response
```json
{
  "data": {
    "userId": 1,
    "username": "username",
    "email": "user@example.com",
    "profileImage": "http://localhost:8080/api/files/view/uuid-filename.jpg",
    "bio": "안녕하세요! 개발자입니다.",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

## 2. 다른 사용자 프로필 조회

**GET** `/users/{userId}`

### Response
```json
{
  "data": {
    "userId": 2,
    "username": "otheruser",
    "email": null,
    "profileImage": "http://localhost:8080/api/files/view/uuid-filename.jpg",
    "bio": "다른 사용자입니다.",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**Note**: 다른 사용자의 이메일은 노출되지 않습니다.

---

## 3. 프로필 수정

**PUT** `/users/profile`

🔒 **인증 필요**

### Request
```json
{
  "username": "newusername",
  "profileImage": "http://localhost:8080/api/files/view/new-uuid-filename.jpg",
  "bio": "수정된 자기소개입니다."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| username | String | ❌ | 사용자명 (2-10자) |
| profileImage | String | ❌ | 프로필 이미지 URL |
| bio | String | ❌ | 자기소개 (500자 이하) |

### Response
```json
{
  "message": "프로필이 수정되었습니다.",
  "data": {
    "userId": 1,
    "username": "newusername",
    "email": "user@example.com",
    "profileImage": "http://localhost:8080/api/files/view/new-uuid-filename.jpg",
    "bio": "수정된 자기소개입니다.",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

## 4. 비밀번호 변경

**PUT** `/users/password`

🔒 **인증 필요**

### Request
```json
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```

### Response
```json
{
  "message": "비밀번호가 변경되었습니다."
}
```

---

## 5. 사용자명으로 검색

**GET** `/users/search/username?username={query}&page=0&size=10`

### Query Parameters
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| username | String | ✅ | - | 검색할 사용자명 |
| page | Integer | ❌ | 0 | 페이지 번호 |
| size | Integer | ❌ | 10 | 페이지 크기 |

### Response
```json
{
  "data": {
    "users": [
      {
        "userId": 2,
        "username": "searchuser",
        "email": null,
        "profileImage": "http://localhost:8080/api/files/view/uuid-filename.jpg",
        "bio": "검색된 사용자입니다.",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

</details>

<details>
<summary>📝 게시물 API (Posts)</summary>

## 1. 게시물 작성

**POST** `/posts`

🔒 **인증 필요**

### Request (multipart/form-data)
```
Content-Type: multipart/form-data

post: {
  "title": "게시물 제목",
  "content": "게시물 내용입니다."
}
image: [이미지 파일] (선택사항)
```

### Request (JSON - 이미지 없는 경우)
```json
{
  "title": "게시물 제목",
  "content": "게시물 내용입니다.",
  "imageUrl": "http://localhost:8080/api/files/view/uuid-filename.jpg"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | String | ✅ | 게시물 제목 (100자 이하) |
| content | String | ✅ | 게시물 내용 |
| imageUrl | String | ❌ | 이미지 URL |

### Response
```json
{
  "message": "게시물이 작성되었습니다.",
  "data": {
    "postId": 1,
    "title": "게시물 제목",
    "content": "게시물 내용입니다.",
    "imageUrl": "http://localhost:8080/api/files/view/uuid-filename.jpg",
    "author": "username",
    "authorId": 1,
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

## 2. 전체 게시물 조회

**GET** `/posts?page=0&size=10`

### Query Parameters
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | Integer | ❌ | 0 | 페이지 번호 |
| size | Integer | ❌ | 10 | 페이지 크기 |

### Response
```json
{
  "data": {
    "posts": [
      {
        "postId": 1,
        "title": "게시물 제목",
        "content": "게시물 내용입니다.",
        "imageUrl": "http://localhost:8080/api/files/view/uuid-filename.jpg",
        "author": "username",
        "authorId": 1,
        "likeCount": 5,
        "commentCount": 3,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

---

## 3. 특정 게시물 조회

**GET** `/posts/{postId}`

### Response
```json
{
  "data": {
    "postId": 1,
    "title": "게시물 제목",
    "content": "게시물 내용입니다.",
    "imageUrl": "http://localhost:8080/api/files/view/uuid-filename.jpg",
    "author": "username",
    "authorId": 1,
    "likeCount": 5,
    "commentCount": 3,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

## 4. 뉴스피드 조회 (팔로우한 사용자들의 게시물)

**GET** `/posts/newsfeed?page=0&size=10`

🔒 **인증 필요**

### Response
```json
{
  "data": {
    "posts": [
      {
        "postId": 2,
        "title": "팔로우한 사용자의 게시물",
        "content": "내용입니다.",
        "imageUrl": null,
        "author": "followeduser",
        "authorId": 2,
        "likeCount": 2,
        "commentCount": 1,
        "createdAt": "2024-01-01T11:00:00",
        "updatedAt": "2024-01-01T11:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

---

## 5. 게시물 수정

**PUT** `/posts/{postId}`

🔒 **인증 필요** (작성자만)

### Request
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용입니다.",
  "imageUrl": "http://localhost:8080/api/files/view/new-uuid-filename.jpg"
}
```

### Response
```json
{
  "message": "게시물이 수정되었습니다.",
  "data": {
    "postId": 1,
    "title": "수정된 제목",
    "content": "수정된 내용입니다.",
    "imageUrl": "http://localhost:8080/api/files/view/new-uuid-filename.jpg",
    "author": "username",
    "authorId": 1,
    "likeCount": 5,
    "commentCount": 3,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T12:00:00"
  }
}
```

---

## 6. 게시물 삭제

**DELETE** `/posts/{postId}`

🔒 **인증 필요** (작성자만)

### Response
```json
{
  "message": "게시물이 삭제되었습니다."
}
```

---

## 7. 제목으로 게시물 검색

**GET** `/posts/search/title?title={query}&page=0&size=10`

### Response
```json
{
  "data": {
    "posts": [
      {
        "postId": 1,
        "title": "검색된 게시물 제목",
        "content": "내용입니다.",
        "imageUrl": null,
        "author": "username",
        "authorId": 1,
        "likeCount": 5,
        "commentCount": 3,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

</details>

<details>
<summary>💬 댓글 API (Comments)</summary>

## 1. 댓글 작성

**POST** `/posts/{postId}/comments`

🔒 **인증 필요**

### Request
```json
{
  "content": "댓글 내용입니다."
}
```

### Response
```json
{
  "message": "댓글이 작성되었습니다.",
  "data": {
    "commentId": 1,
    "content": "댓글 내용입니다.",
    "author": "username",
    "authorId": 1,
    "postId": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

## 2. 게시물의 댓글 목록 조회

**GET** `/posts/{postId}/comments?page=0&size=10`

### Response
```json
{
  "data": {
    "comments": [
      {
        "commentId": 1,
        "content": "댓글 내용입니다.",
        "author": "username",
        "authorId": 1,
        "postId": 1,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

---

## 3. 댓글 수정

**PUT** `/comments/{commentId}`

🔒 **인증 필요** (작성자 또는 게시물 작성자)

### Request
```json
{
  "content": "수정된 댓글 내용입니다."
}
```

### Response
```json
{
  "message": "댓글이 수정되었습니다.",
  "data": {
    "commentId": 1,
    "content": "수정된 댓글 내용입니다.",
    "author": "username",
    "authorId": 1,
    "postId": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

---

## 4. 댓글 삭제

**DELETE** `/comments/{commentId}`

🔒 **인증 필요** (작성자 또는 게시물 작성자)

### Response
```json
{
  "message": "댓글이 삭제되었습니다."
}
```

---

## 5. 댓글 수 조회

**GET** `/posts/{postId}/comments/count`

### Response
```json
{
  "data": 5
}
```

</details>

<details>
<summary>❤️ 좋아요 API (Likes)</summary>

## 1. 게시물 좋아요

**POST** `/posts/{postId}/likes`

🔒 **인증 필요**

### Response
```json
{
  "message": "좋아요가 등록되었습니다.",
  "data": {
    "likeId": 1,
    "postId": 1,
    "postTitle": "게시물 제목",
    "userId": 1,
    "username": "username",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

### Error Response (자신의 게시물)
```json
{
  "message": "자신의 게시물에는 좋아요를 누를 수 없습니다."
}
```

---

## 2. 게시물 좋아요 취소

**DELETE** `/posts/{postId}/likes`

🔒 **인증 필요**

### Response
```json
{
  "message": "좋아요가 취소되었습니다."
}
```

---

## 3. 게시물의 좋아요 목록 조회

**GET** `/posts/{postId}/likes`

### Response
```json
{
  "data": [
    {
      "likeId": 1,
      "postId": 1,
      "postTitle": "게시물 제목",
      "userId": 2,
      "username": "otheruser",
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

## 4. 좋아요 상태 확인

**GET** `/posts/{postId}/likes/status`

🔒 **인증 필요**

### Response
```json
{
  "data": true
}
```

**Note**: `true`이면 좋아요 누름, `false`이면 좋아요 누르지 않음

---

## 5. 좋아요 수 조회

**GET** `/posts/{postId}/likes/count`

### Response
```json
{
  "data": 5
}
```

</details>

<details>
<summary>👥 팔로우 API (Follows)</summary>

## 1. 팔로우

**POST** `/follows/{userId}`

🔒 **인증 필요**

### Response
```json
{
  "message": "팔로우가 완료되었습니다."
}
```

### Error Response (자기 자신)
```json
{
  "message": "자기 자신을 팔로우할 수 없습니다."
}
```

### Error Response (이미 팔로우)
```json
{
  "message": "이미 팔로우한 사용자입니다."
}
```

---

## 2. 언팔로우

**DELETE** `/follows/{userId}`

🔒 **인증 필요**

### Response
```json
{
  "message": "언팔로우가 완료되었습니다."
}
```

---

## 3. 팔로잉 목록 조회 (내가 팔로우하는 사람들)

**GET** `/follows/{userId}/following?page=0&size=10`

### Response
```json
{
  "data": {
    "follows": [
      {
        "userId": 2,
        "username": "followeduser",
        "profileImage": "http://localhost:8080/api/files/view/uuid-filename.jpg",
        "followAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

---

## 4. 팔로워 목록 조회 (나를 팔로우하는 사람들)

**GET** `/follows/{userId}/followers?page=0&size=10`

### Response
```json
{
  "data": {
    "follows": [
      {
        "userId": 3,
        "username": "followeruser",
        "profileImage": "http://localhost:8080/api/files/view/uuid-filename.jpg",
        "followAt": "2024-01-01T10:00:00"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "first": true,
    "last": true
  }
}
```

---

## 5. 팔로잉 수 조회

**GET** `/follows/following/count`

🔒 **인증 필요**

### Response
```json
{
  "data": 10
}
```

---

## 6. 팔로워 수 조회

**GET** `/follows/followers/count`

🔒 **인증 필요**

### Response
```json
{
  "data": 15
}
```

---

## 7. 팔로우 상태 확인

**GET** `/follows/status/{userId}`

🔒 **인증 필요**

### Response
```json
{
  "data": true
}
```

**Note**: `true`이면 팔로우 중, `false`이면 팔로우 안함

</details>

<details>
<summary>📁 파일 API (Files)</summary>

## 1. 파일 업로드

**POST** `/files/upload`

### Request (multipart/form-data)
```
Content-Type: multipart/form-data

file: [이미지 파일]
```

**허용 형식**: jpg, jpeg, png, gif  
**최대 크기**: 10MB

### Response
```json
{
  "message": "파일이 업로드되었습니다.",
  "data": {
    "fileName": "uuid-generated-filename.jpg",
    "fileDownloadUri": "http://localhost:8080/api/files/download/uuid-generated-filename.jpg",
    "fileViewUri": "http://localhost:8080/api/files/view/uuid-generated-filename.jpg",
    "fileType": "image/jpeg",
    "size": 1024000
  }
}
```

### Error Response (잘못된 형식)
```json
{
  "message": "지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 허용)"
}
```

### Error Response (크기 초과)
```json
{
  "message": "파일 크기는 10MB 이하여야 합니다."
}
```

---

## 2. 파일 조회

**GET** `/files/view/{fileName}`

### Response
실제 파일 데이터 (이미지)를 반환합니다.

```
Content-Type: image/jpeg
Content-Length: 1024000

[이미지 바이너리 데이터]
```

---

## 3. 파일 다운로드

**GET** `/files/download/{fileName}`

### Response
파일 다운로드를 위한 응답을 반환합니다.

```
Content-Type: image/jpeg
Content-Disposition: attachment; filename="original-filename.jpg"
Content-Length: 1024000

[이미지 바이너리 데이터]
```

</details>

<details>
<summary>🚨 에러 코드 및 페이지네이션</summary>

## 에러 코드

| HTTP 상태 | 에러 코드 | 설명 |
|-----------|-----------|------|
| 400 | VALIDATION_ERROR | 입력값 검증 실패 |
| 401 | UNAUTHORIZED | 인증 토큰 없음 또는 만료 |
| 403 | FORBIDDEN | 권한 없음 |
| 404 | USER_NOT_FOUND | 사용자를 찾을 수 없음 |
| 404 | POST_NOT_FOUND | 게시물을 찾을 수 없음 |
| 404 | COMMENT_NOT_FOUND | 댓글을 찾을 수 없음 |
| 409 | DUPLICATE_EMAIL | 이미 존재하는 이메일 |
| 409 | DUPLICATE_LIKE | 이미 좋아요한 게시물 |
| 409 | DUPLICATE_FOLLOW | 이미 팔로우한 사용자 |
| 500 | INTERNAL_ERROR | 서버 내부 오류 |

---

## 페이지네이션

모든 목록 조회 API는 페이지네이션을 지원합니다.

### Query Parameters
- `page`: 페이지 번호 (0부터 시작, 기본값: 0)
- `size`: 페이지 크기 (기본값: 10)

### Response 형식
```json
{
  "currentPage": 0,
  "totalPages": 5,
  "totalElements": 47,
  "pageSize": 10,
  "first": true,
  "last": false
}
```

---

## 검색 기능

### 게시물 검색
- **제목 검색**: `/posts/search/title?title={query}`
- **내용 검색**: `/posts/search/content?content={query}`
- **날짜 검색**: `/posts/search/date?startDate={date}&endDate={date}`

### 사용자 검색
- **사용자명 검색**: `/users/search/username?username={query}`
- **이메일 검색**: `/users/search/email?email={query}`

모든 검색 API는 부분 일치 검색을 지원하며, 페이지네이션이 적용됩니다.

</details>

---

<details>
<summary>🐛 알려진 문제 및 제한사항</summary>

1. **이미지 리사이징**: 현재 원본 이미지가 그대로 저장됨
2. **토큰 갱신**: JWT 토큰 자동 갱신 기능 미구현
3. **실시간 알림**: WebSocket 기반 실시간 알림 미구현
4. **캐싱**: Redis 등을 이용한 캐싱 시스템 미구현

</details>

<details>
<summary>🔮 향후 개발 계획</summary>

- [ ] 실시간 알림 시스템 (WebSocket)
- [ ] 이미지 리사이징 및 최적화
- [ ] Redis 캐싱 시스템
- [ ] JWT 토큰 갱신 기능
- [ ] 게시물 해시태그 기능
- [ ] 스토리 기능
- [ ] 다중 이미지 업로드

</details>

<details>
<summary>👥 기여하기</summary>

1. 이 저장소를 Fork 합니다
2. 새로운 기능 브랜치를 생성합니다 (`git checkout -b feature/새기능`)
3. 변경사항을 커밋합니다 (`git commit -am '새 기능 추가'`)
4. 브랜치에 푸시합니다 (`git push origin feature/새기능`)
5. Pull Request를 생성합니다

</details>

## 📄 라이선스

이 프로젝트는 개인 학습 목적으로 개발되었습니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해 주세요.

---

**Picflow** - 사진이 흐르는 소셜 미디어 플랫폼 🌊📸
