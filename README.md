# 👤 couponmoa-user

## 📌 개요

이 서버는 Couponmoa 프로젝트의 **사용자 도메인 전담 서비스**로,  
**회원가입, 로그인, 사용자 정보 관리 등 사용자 관련 기능**을 처리합니다.

---

## 🧩 주요 기능

- 회원가입 / 로그인 (JWT 기반 인증)
- 사용자 프로필 조회 / 수정 / 탈퇴
- 비밀번호 변경
- 인증된 사용자 식별 및 관리
- redis를 사용한 refreshToken 발급 및 재발급

---

## 🔧 기술 스택

- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- MySQL, Spring Data JPA
- Gradle

---

## 📘 API 명세 요약
### 🔐 인증 (AuthController)
| 메서드    | URI                    | 설명                               |
| ------ | ---------------------- | -------------------------------- |
| `POST` | `/api/v1/auth/signup`  | 회원가입 (ROLE\_USER, ROLE\_ADMIN)   |
| `POST` | `/api/v1/auth/signin`  | 로그인                              |
| `POST` | `/api/v1/auth/refresh` | Refresh Token으로 Access Token 재발급 |


### 👤 사용자 관리 (UserControllerV1)
| 메서드      | URI                      | 설명                 |
| -------- | ------------------------ | ------------------ |
| `GET`    | `/api/v1/users`          | 내 정보 조회            |
| `PATCH`  | `/api/v1/users`          | 내 정보 수정 (비밀번호 제외)  |
| `PUT`    | `/api/v1/users/password` | 비밀번호 변경            |
| `DELETE` | `/api/v1/users`          | 회원 탈퇴 (비밀번호 확인 필요) |


### 🖼️ 프로필 이미지 (UserProfileController)
| 메서드      | URI                   | 설명         |
| -------- | --------------------- | ---------- |
| `POST`   | `/api/v1/users/image` | 프로필 이미지 등록 |
| `DELETE` | `/api/v1/users/image` | 프로필 이미지 삭제 |


---

## 🔐 인증

- JWT 기반 인증 방식을 사용하며, 로그인 시 발급된 토큰을 `Authorization` 헤더에 포함하여 요청
- 토큰 검증은 Gateway 서버에서 수행하며, 사용자 ID는 헤더(`X-User-Id`)로 전달됨


