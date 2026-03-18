# 📚 만화책 대여점 관리 시스템 (Comic Rental Management)

Java CLI 환경에서 MySQL과 JDBC를 연동하여 만화책 대여를 관리하는 팀 프로젝트입니다.

---

## 🛠 Tech Stack
- **Language**: Java 17 (또는 팀 공통 버전)
- **Database**: MySQL 8.0+
- **Library**: JDBC (MySQL Connector/J)
- **Tool**: IntelliJ/Eclipese, Git/GitHub

---

## 🧭 협업 컨벤션 (Conventions)

### 🌿 브랜치 전략
- **형식**: `타입/#이슈번호-기능명`
- `feature/`: 신규 기능 (`feature/#1-comic-add`)
- `fix/`: 오류 수정 (`fix/#5-db-close`)
- `docs/`: 문서 수정 (`docs/#2-readme`)

### 🚫 main 브랜치 정책 (중요)
main 브랜치에는 직접 push하지 않습니다.
모든 작업은 기능 브랜치에서 작업 후 Pull Request(PR)를 통해 병합합니다.
PR을 통해 팀원과 코드 내용을 공유하고 리뷰 후 병합합니다.

### 💬 커밋 메시지
- **형식**: `[타입] #이슈번호 - 내용`
- 예: `[Feat] #1 - 만화책 등록 기능 및 JDBC 연동 구현`

### 💻 코드 컨벤션
1. **명명 규칙**
   - 클래스: `PascalCase` (예: `ComicRepository`)
   - 메서드/변수: `camelCase` (예: `rentComic`, `isRented`)
2. **자원 관리**: 
3. **트랜잭션**: 

### ⚙️ 설정 가이드 (Setup)
JDK 17: 프로젝트는 Java 17을 기준으로 빌드됩니다. 반드시 버전을 맞춰주세요.

Database: src/main/resources/config.properties 파일에 본인의 MySQL 접속 정보를 설정하세요.

Gradle: 프로젝트를 연 후 build.gradle의 변경사항을 반드시 Reload 하세요.

---

## 📋 주요 기능 및 명령어

| 기능 분류 | 명령어 | 설명 |
| :--- | :--- | :--- |
| **만화책** | `comic-add` | 제목, 권수, 작가 입력 및 등록 |
| | `comic-list` | 전체 만화책 목록 출력 |
| | `comic-detail [id]` | 특정 만화책 상세 정보 조회 |
| | `comic-update [id]` | 제목, 권수, 작가 정보 수정 |
| | `comic-delete [id]` | 만화책 데이터 삭제 |
| **회원** | `member-add` | 신규 회원 등록 |
| | `member-list` | 회원 목록 조회 |
| **대여/반납** | `rent [cId] [mId]` | 만화책 대여 (중복 대여 불가) |
| | `return [rId]` | 만화책 반납 처리 |
| | `rental-list` | 대여 내역 출력 |
| **기타** | `exit` | 프로그램 종료 |

---
## 📁 명령어 구현 확인

### member 
<img width="338" height="200" alt="Image" src="https://github.com/user-attachments/assets/22b06593-3264-46e1-944e-8613aead75a1" />

### comic
<img width="466" height="155" alt="Image" src="https://github.com/user-attachments/assets/17300ec9-e7f9-44a8-bfd9-5efed747949a" />

<img width="468" height="115" alt="Image" src="https://github.com/user-attachments/assets/335be06a-0551-4472-affc-7bab27c0c8aa" />

<img width="479" height="186" alt="Image" src="https://github.com/user-attachments/assets/39efb932-c6f9-4693-a162-39099a7523b6" />

### rent
<img width="568" height="226" alt="Image" src="https://github.com/user-attachments/assets/948d254c-3c45-403a-9089-77df7357d4a0" />

---
## 📁 ERD
<img width="2358" height="1034" alt="image" src="https://github.com/user-attachments/assets/b6d1fa54-bffa-4aa2-a53b-6245a7e8eec1" />




---
## 📁 프로젝트 구조 (Project Structure)
```text
src/main/java/CLI/project/
├─ Main.java                 ← 프로그램 진입점 (App 실행)
├─ App.java                  ← 메인 루프 및 명령어 라우팅
├─ Rq.java                   ← 커맨드 파싱 및 데이터 추출 유틸
├─ util/                     
│  └─ DBUtil.java            ← JDBC 연결 및 자원 해제 전담
├─ domain/                   ← 순수 데이터 클래스 (DTO) 모음
│  ├─ Comic.java            
│  ├─ Member.java           
│  └─ Rental.java           
└─ repository/               ← 데이터베이스 처리 로직 모음 (따로 분리)
   ├─ ComicRepository.java  
   ├─ MemberRepository.java 
   └─ RentalRepository.java
└── resources/                
    └── config.properties     (DB 접속 정보)
