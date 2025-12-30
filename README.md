# Simple-Java-HTTP-Server

백엔드 학습을 위한 자바로 http 서버 만들기

## 📌 프로젝트 목적

- Java 기반 서버 구조 이해
- HTTP 서버의 초기화 과정 학습
- JSON 설정 파일을 활용한 서버 설정 관리
- Jackson을 이용한 직렬화 / 역직렬화 흐름 이해

---

## ⚙️ 현재까지 구현한 기능

### 1. JSON 기반 서버 설정 파일

서버 설정을 코드에 하드코딩하지 않고, 외부 JSON 파일로 관리합니다.

```json
{
  "port": 8080,
  "webroot": "/tmp"
}
```

port : HTTP 서버가 요청을 수신할 포트 번호

webroot : 정적 리소스를 제공할 웹 루트 디렉토리

### 2. 설정 파일 로딩 기능

지정된 경로의 설정 파일을 읽어 서버 설정으로 로드합니다.

**처리 흐름**

    1. 설정 파일을 문자열로 읽기
    2. JSON 문자열을 JsonNode 트리 구조로 파싱
    3. JsonNode를 Configuration 객체로 역직렬화
    4. 현재 서버 설정으로 저장

### 3. Jackson 기반 JSON 처리

Jackson 라이브러리를 사용하여 다음과 같은 변환 구조를 구현했습니다.

- JSON 문자열 → JsonNode
- JsonNode → Java 객체
- Java 객체 → JSON 문자열
- Pretty 출력 옵션 지원

### 4. 서버 시작 시 설정 적용

서버 실행 시 설정 파일을 로드하고, 적용된 설정 값을 로그로 출력합니다.

```
Server starting.....
Using Port: 8080
Using WebRoot: /tmp
```

이를 통해 서버가 어떤 설정으로 실행되었는지 즉시 확인할 수 있습니다.

### 🧠 학습 포인트

- 설정 파일을 외부로 분리하여 확장성과 유지보수성 확보
- JSON → 트리 → 객체 구조를 통한 유연한 파싱 전략
- ObjectMapper / ObjectWriter를 활용한 안전한 직렬화 설계
- 서버 초기화 로직과 설정 로딩 로직 분리

### 🔧 사용 기술

- Java
- Jackson (JSON Parsing)
- File I/O
