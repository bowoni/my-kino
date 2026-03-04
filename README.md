# 🎬 MyKino
> TMDB API를 활용한 OTT 콘텐츠 탐색 및 평가 웹 애플리케이션

![MyKino Demo](src/main/resources/static/images/demo.gif)

<br>

# 목차

[1-프로젝트 소개](#1-프로젝트-소개)

- [1-1 개요](#1-1-개요)
- [1-2 주요목표](#1-2-주요목표)
- [1-3 개발환경](#1-3-개발환경)
- [1-4 구동방법](#1-4-구동방법)

[2-Architecture](#2-architecture)
- [2-1 구조도](#2-1-구조도)
- [2-2 파일 디렉토리](#2-2-파일-디렉토리)

[3-프로젝트 특징](#3-프로젝트-특징)

[4-프로젝트 세부과정](#4-프로젝트-세부과정)

[5-업데이트 및 리팩토링 사항](#5-업데이트-및-리팩토링-사항)


---

## 1-프로젝트 소개

### 1-1 개요
> TMDB API를 활용한 OTT 콘텐츠 탐색 및 평가 웹 애플리케이션
- **개발기간** : 2026.02 ~ (진행중)
- **참여인원** : 1인 (개인 프로젝트)
- **주요특징**
  - 키노라이츠(KinoLights)앱을 모티브로 한 OTT 콘텐츠 통합 탐색 서비스
  - TMDB API 연동을 통한 실시간 영화/드라마/예능 OTT 콘텐츠 정보 제공
  - 신호등 평가 시스템(추천/보통/비추천)과 리뷰 기능을 통한 사용자 참여형 플랫폼

### 1-2 주요목표
- Spring Boot 기반 MVC 아키텍처 설계 및 RESTful API 구현
- TMDB API 연동을 통한 외부 API 데이터 동기화 및 캐싱 전략 이해
- Spring Security를 활용한 인증/인가 처리 및 역할 기반 접근 제어 구현
- JSP + Vanilla JS 환경에서 SPA에 준하는 인터랙티브 UI 구현 (무한스크롤, 자동완성, 동적 필터링)

### 1-3 개발환경
- **활용기술 외 키워드**
  - **Backend** : Java 11, Spring Boot 2.7.18, Spring Security, Spring Data JPA
  - **Frontend** : JSP, JSTL, Vanilla JavaScript (ES5), CSS3
  - **Database** : H2 (File Mode, MySQL 호환)
  - **External API** : TMDB API v3 (영화/TV 정보, 장르, OTT 제공처)

- **라이브러리**
  - Lombok (코드 간소화)
  - BCryptPasswordEncoder (비밀번호 암호화)
  - RestTemplate (외부 API 호출)

### 1-4 구동방법

순서 | 내용 | 비고
---- | ----- | -----
1 | 프로젝트를 클론합니다 | `git clone` 후 IDE에서 Import
2 | `application.properties`에서 TMDB API Key를 확인합니다 | 키 발급: [TMDB 개발자](https://www.themoviedb.org/settings/api)
3 | Gradle 빌드를 실행합니다 | `./gradlew build`
4 | 애플리케이션을 실행합니다 | `./gradlew bootRun` 또는 IDE에서 실행
5 | 브라우저에서 `http://localhost:8080`에 접속합니다 | 최초 실행 시 TMDB 인기 영화 자동 동기화
6 | 테스트 계정으로 로그인하거나 회원가입합니다 | `test@mykino.com` / `test1234`

<br>

## 2-Architecture
### 2-1 구조도

<!-- 아키텍처 다이어그램 (추후 추가) -->
<!-- ![Architecture](아키텍처_다이어그램_URL) -->

<br>

> Spring Boot MVC + JPA
- 전통적인 MVC 패턴을 기반으로, Controller → Service → Repository 계층 구조로 역할 분리
- Spring Data JPA를 통한 ORM 기반 데이터 접근
- Spring Security로 Form 기반 인증 및 역할(USER/ADMIN) 기반 인가 처리

<br>

> TMDB API 연동
- RestTemplate을 활용하여 TMDB API v3와 통신, 영화/TV/장르/OTT 제공처 데이터 수집
- 상세 페이지 접근 시 실시간 동기화(Sync-on-demand) 방식으로 최신 정보 유지
- TMDB 장르 ID → DB 장르, TMDB Provider ID → DB OTT 플랫폼 매핑을 DB 기반으로 관리

<br>

> 프론트엔드 인터랙션
- JSP 서버 사이드 렌더링 + Vanilla JS를 조합한 하이브리드 방식
- 무한스크롤, 자동완성 검색, 동적 필터링 등 SPA에 준하는 사용자 경험 제공
- REST API를 통한 비동기 데이터 통신 (fetch API)

<br>

### 2-2 파일 디렉토리
```
my-kino
 ┣ 📂src/main/java/com/mykino
 ┃ ┣ 📂config
 ┃ ┃ ┣ 📜SecurityConfig.java
 ┃ ┃ ┣ 📜WebConfig.java
 ┃ ┃ ┣ 📜CustomUserDetailsService.java
 ┃ ┃ ┣ 📜CustomUserDetails.java
 ┃ ┃ ┗ 📜DataInitializer.java
 ┃ ┣ 📂controller
 ┃ ┃ ┣ 📜HomeController.java
 ┃ ┃ ┣ 📜AuthController.java
 ┃ ┃ ┣ 📜ContentController.java
 ┃ ┃ ┣ 📜ExploreController.java
 ┃ ┃ ┣ 📜SearchController.java
 ┃ ┃ ┣ 📜MypageController.java
 ┃ ┃ ┣ 📜RatingController.java
 ┃ ┃ ┣ 📜ReviewController.java
 ┃ ┃ ┣ 📜TmdbApiController.java
 ┃ ┃ ┗ 📜AdminController.java
 ┃ ┣ 📂dto
 ┃ ┃ ┣ 📜SignupRequestDto.java
 ┃ ┃ ┣ 📜LoginRequestDto.java
 ┃ ┃ ┗ 📜ContentDetailDto.java
 ┃ ┣ 📂entity
 ┃ ┃ ┣ 📜BaseEntity.java
 ┃ ┃ ┣ 📜User.java
 ┃ ┃ ┣ 📜Content.java
 ┃ ┃ ┣ 📜Genre.java
 ┃ ┃ ┣ 📜GenreTmdbMapping.java
 ┃ ┃ ┣ 📜OttPlatform.java
 ┃ ┃ ┣ 📜CastMember.java
 ┃ ┃ ┣ 📜ContentGenre.java
 ┃ ┃ ┣ 📜ContentOtt.java
 ┃ ┃ ┣ 📜ContentCast.java
 ┃ ┃ ┣ 📜Rating.java
 ┃ ┃ ┣ 📜Review.java
 ┃ ┃ ┣ 📜ReviewLike.java
 ┃ ┃ ┣ 📜Watchlist.java
 ┃ ┃ ┣ 📜WatchlistFolder.java
 ┃ ┃ ┣ 📜ViewHistory.java
 ┃ ┃ ┣ 📜SearchHistory.java
 ┃ ┃ ┣ 📜ExploreCategory.java
 ┃ ┃ ┣ 📜ExploreCategoryGenre.java
 ┃ ┃ ┣ 📜UserOttSubscription.java
 ┃ ┃ ┗ 📜Notification.java
 ┃ ┣ 📂enums
 ┃ ┃ ┣ 📜ContentType.java
 ┃ ┃ ┣ 📜TrafficColor.java
 ┃ ┃ ┣ 📜UserRole.java
 ┃ ┃ ┣ 📜AuthProvider.java
 ┃ ┃ ┣ 📜CastRoleType.java
 ┃ ┃ ┗ 📜NotificationType.java
 ┃ ┣ 📂repository
 ┃ ┃ ┗ 📜(17개 Repository 인터페이스)
 ┃ ┗ 📂service
 ┃   ┣ 📜UserService.java
 ┃   ┣ 📜ContentService.java
 ┃   ┣ 📜TmdbService.java
 ┃   ┣ 📜RatingService.java
 ┃   ┣ 📜ReviewService.java
 ┃   ┗ 📜SearchService.java
 ┣ 📂src/main/webapp/WEB-INF/views
 ┃ ┣ 📂common
 ┃ ┃ ┣ 📜header.jsp
 ┃ ┃ ┗ 📜footer.jsp
 ┃ ┣ 📜home.jsp
 ┃ ┣ 📂auth
 ┃ ┃ ┣ 📜login.jsp
 ┃ ┃ ┗ 📜signup.jsp
 ┃ ┣ 📂content
 ┃ ┃ ┗ 📜detail.jsp
 ┃ ┣ 📂explore
 ┃ ┃ ┗ 📜index.jsp
 ┃ ┣ 📂search
 ┃ ┃ ┗ 📜result.jsp
 ┃ ┣ 📂mypage
 ┃ ┃ ┗ 📜profile.jsp
 ┃ ┗ 📂admin
 ┃   ┗ 📜tmdb.jsp
 ┗ 📂src/main/resources
   ┣ 📜application.properties
   ┣ 📜data.sql
   ┗ 📂static
     ┣ 📂css
     ┃ ┣ 📜common.css
     ┃ ┣ 📜home.css
     ┃ ┣ 📜content.css
     ┃ ┣ 📜explore.css
     ┃ ┣ 📜search.css
     ┃ ┣ 📜auth.css
     ┃ ┣ 📜profile.css
     ┃ ┗ 📜admin.css
     ┗ 📂images/ott
       ┗ 📜(OTT 플랫폼 SVG 로고)
```

<br>

## 3-프로젝트 특징
### 3-1 홈 화면 및 콘텐츠 탐색 (Home / Explore)
- TMDB API 연동을 통해 인기, 현재 상영, 높은 평점의 콘텐츠를 실시간으로 제공
- 카테고리(전체/영화/드라마/애니메이션/예능/시사교양) 탭 필터 + 서브장르 필터로 세분화된 탐색
- OTT 플랫폼별 필터링 (넷플릭스, 티빙, 웨이브, 왓챠, 디즈니+)으로 내가 구독 중인 OTT의 콘텐츠만 조회
- 무한스크롤 기반 페이지네이션으로 끊김 없는 탐색 경험

<!-- 스크린샷 추가 -->
<!--
|![Home](스크린샷_URL)|![Explore](스크린샷_URL)|![OTT Filter](스크린샷_URL)|
|:-:|:-:|:-:|
|`Home`|`Explore`|`OTT Filter`|
-->

<br>

---

### 3-2 콘텐츠 상세 정보 및 OTT 제공처 확인 (Content Detail)
- TMDB 실시간 동기화로 최신 포스터, 배경 이미지, 줄거리, 출연진, 감독 정보 제공
- 해당 콘텐츠를 시청할 수 있는 OTT 플랫폼을 한눈에 확인 (스트리밍/구매/대여 구분)
- 콘텐츠 유형(영화/드라마/예능/다큐멘터리/애니메이션) 자동 분류

<!-- 스크린샷 추가 -->
<!--
|![Detail](스크린샷_URL)|![Cast](스크린샷_URL)|![OTT](스크린샷_URL)|
|:-:|:-:|:-:|
|`Detail`|`Cast`|`OTT Providers`|
-->

<br>

---

### 3-3 신호등 평가 시스템 및 리뷰 (Rating & Review)
- 키노라이츠 스타일의 신호등 평가: 초록(추천) / 노랑(보통) / 빨강(비추천)
- 0~10점 세부 점수 및 한줄 코멘트 작성 가능
- 텍스트 리뷰 작성 시 스포일러 여부 표시 기능
- 리뷰 좋아요 기능으로 유용한 리뷰 노출

<!-- 스크린샷 추가 -->
<!--
|![Rating](스크린샷_URL)|![Review](스크린샷_URL)|![Review Like](스크린샷_URL)|
|:-:|:-:|:-:|
|`Traffic Rating`|`Review`|`Review Like`|
-->

<br>

---

### 3-4 검색 및 자동완성 (Search)
- TMDB 통합 검색으로 영화, 드라마, 인물 등을 한번에 검색
- 키워드 입력 시 실시간 자동완성 추천 (상위 10건)
- 검색 히스토리 저장 및 인기 검색어 제공

<!-- 스크린샷 추가 -->
<!--
|![Search](스크린샷_URL)|![Autocomplete](스크린샷_URL)|![Results](스크린샷_URL)|
|:-:|:-:|:-:|
|`Search`|`Autocomplete`|`Results`|
-->

<br>

---

### 3-5 관리자 기능 (Admin)
- TMDB 인기 영화 일괄 동기화 (페이지 단위)
- 키워드 검색 후 선택적 동기화
- TMDB ID 직접 입력을 통한 단건 동기화

<!-- 스크린샷 추가 -->
<!--
|![Admin](스크린샷_URL)|
|:-:|
|`Admin Panel`|
-->

<br>

## 4-프로젝트 세부과정
### 4-1 [Feature 1] TMDB API 연동 및 데이터 동기화

> RestTemplate 기반 외부 API 통신 및 DB 동기화 전략 구현
- TMDB API v3의 Discover, Search, Movie/TV Details, Credits, Watch Providers 엔드포인트 활용
- Sync-on-demand 방식: 상세 페이지 최초 접근 시 TMDB 데이터를 DB에 동기화하여 이후 빠른 조회 가능
- 장르 매핑(N:1)을 DB 기반으로 관리하여 하드코딩 제거

```java
// TMDB 장르 → DB 장르 매핑 (GenreTmdbMapping 테이블 활용)
genreTmdbMappingRepository.findByTmdbGenreId(tmdbGenreId).ifPresent(mapping -> {
    Genre genre = mapping.getGenre();
    if (addedGenreIds.add(genre.getId())) {
        ContentGenre cg = new ContentGenre(content, genre);
        content.getContentGenres().add(cg);
    }
});
```

<br>

### 4-2 [Feature 2] Spring Security 인증/인가

> Form 기반 인증 + 역할 기반 접근 제어 구현
- BCrypt 암호화를 적용한 비밀번호 저장
- ADMIN/USER 역할에 따른 URL 접근 제어 (`/admin/**`는 ADMIN 전용)
- 공개 API(`/api/public/**`)와 인증 필요 API 분리

```java
http.authorizeRequests()
    .antMatchers("/", "/search/**", "/content/**", "/explore/**").permitAll()
    .antMatchers("/api/public/**").permitAll()
    .antMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated();
```

<br>

### 4-3 [Feature 3] 탐색 페이지 동적 필터링

> DB 기반 카테고리 + OTT 필터 + 장르 필터 + 정렬 조합
- 카테고리/장르/OTT 데이터를 DB에서 관리하여 코드 수정 없이 확장 가능
- 영화/TV를 번갈아 요청하는 페이지 전략으로 `mediaType=all` 지원
- OTT 플랫폼 로고는 TMDB API에서 실시간으로 가져와 표시

```javascript
// DB에서 카테고리 + OTT 데이터 fetch 후 초기화
Promise.all([
    fetch('/api/public/tmdb/categories').then(function(r) { return r.json(); }),
    fetch('/api/public/tmdb/ott-providers').then(function(r) { return r.json(); })
]).then(function(results) {
    CATEGORIES = results[0];
    OTT_PROVIDERS = results[1];
    initFilters();
    loadMore();
});
```

<br>

### 4-4 [Feature 4] 신호등 평가 시스템

> 키노라이츠 스타일의 TrafficColor Enum 기반 평가 로직
- GREEN(추천), YELLOW(보통), RED(비추천) 3단계 신호등 평가
- 사용자 평가 집계를 통한 콘텐츠별 키노스코어 및 대표 색상 산출
- 평가 생성/수정/삭제 시 콘텐츠 점수 자동 재계산

```java
public enum TrafficColor {
    GREEN("추천", "#4CAF50"),
    YELLOW("보통", "#FFC107"),
    RED("비추천", "#F44336");
}
```

<br>

## 5-업데이트 및 리팩토링 사항
### 5-1 우선 순위별 개선항목

1) OAuth 소셜 로그인
- [ ] Google, Naver 등 소셜 로그인 연동 (AuthProvider Enum 확장 준비 완료)

2) 마이페이지 기능 고도화
- [ ] 보고싶어요(Watchlist) 폴더 관리 및 시청기록(ViewHistory) UI 구현
- [ ] OTT 구독 정보 기반 맞춤 콘텐츠 추천

3) 검색 기능 강화
- [ ] 인기 검색어 및 최근 검색어 UI 표시
- [ ] 검색 결과 필터링 (영화/드라마/인물 구분)

4) 알림 시스템
- [ ] 리뷰 좋아요, 새 콘텐츠 추천 등 알림 기능 구현 (Notification 엔티티 준비 완료)

### 5-2 그 외 항목

1) 데이터베이스 전환
- [ ] H2 → MySQL/PostgreSQL로 운영 DB 전환
- [ ] Redis 캐시 도입으로 TMDB API 호출 최적화

2) 배포 환경 구성
- [ ] Docker 컨테이너화 및 AWS/GCP 배포
- [ ] CI/CD 파이프라인 구축

3) 테스트 코드 보강
- [ ] Service 단위 테스트 및 Controller 통합 테스트 작성
- [ ] TMDB API Mock을 활용한 안정적인 테스트 환경 구축

<br>
