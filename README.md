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

### 5. 기본 HTTP 서버 소켓 통신 구현

Java의 `ServerSocket`과 `Socket`을 사용하여 **브라우저와 직접 TCP 통신하는 HTTP 서버의 최소 동작 흐름**을 구현했습니다.

#### 구현 내용

- 설정 파일에서 로드한 포트 번호로 서버 소켓 생성
- 클라이언트(브라우저)의 연결 요청을 대기 (`accept`)
- 연결된 클라이언트와 통신하기 위한 소켓 획득
- 입력 스트림으로 HTTP 요청 수신
- 출력 스트림으로 HTTP 응답 전송
- HTML 응답을 HTTP/1.1 형식에 맞게 직접 구성하여 전송

#### 처리 흐름

1. `ServerSocket`을 생성하여 지정된 포트에서 연결 대기
2. 브라우저의 요청이 들어오면 `accept()`를 통해 `Socket` 생성
3. `InputStream`으로 HTTP 요청 메시지 수신
4. `OutputStream`으로 HTTP 응답 메시지 전송
5. 응답 전송 후 소켓 및 스트림 자원 정리

#### HTTP 응답 구성 방식

- 상태 라인: `HTTP/1.1 200 OK`
- 헤더: `Content-Length`
- 빈 줄(CRLF) 이후 HTML Body 전송
- HTTP/1.1 표준 형식을 직접 문자열로 구성

이를 통해 **브라우저가 서버 응답을 정상적인 HTTP 응답으로 인식**하도록 했습니다.

#### 결과

브라우저에서 서버에 접속하면 다음과 같은 HTML 응답이 반환됩니다.

- 직접 작성한 HTML 문자열이 브라우저에 렌더링됨
- Java 소켓 기반으로 HTTP 통신이 실제로 동작함을 확인

### 6. Multiple Connections 처리 (Thread 기반 서버 구조)

여러 클라이언트의 동시 접속을 처리하기 위해 **Thread 기반의 멀티 커넥션 서버 구조**를 구현했습니다.  
서버는 연결 수락과 실제 HTTP 통신을 분리하여 처리합니다.

#### 구조 개요

- `ServerListenerThread`

  - `ServerSocket`을 통해 클라이언트 연결을 지속적으로 수락
  - 연결이 발생하면 클라이언트 전용 `Socket`을 생성
  - 실제 통신 처리를 워커 스레드에 위임

- `HttpConnectionWorkerThread`
  - 클라이언트 1명당 1개의 스레드
  - HTTP 요청 수신 및 HTTP 응답 전송 담당
  - 통신 종료 후 스트림 및 소켓 자원 정리

#### 처리 흐름

1. `ServerListenerThread`가 지정된 포트에서 연결 요청을 대기
2. 클라이언트가 접속하면 `accept()`를 통해 `Socket` 생성
3. 생성된 `Socket`을 `HttpConnectionWorkerThread`에 전달
4. 워커 스레드에서 HTTP 요청/응답 처리
5. 각 클라이언트는 독립적인 스레드에서 처리됨

### 7. HTTP 요청 파싱 준비 (Part 5: Parsing Requests 기반)

브라우저가 서버로 보내는 HTTP 요청을 “읽고 해석(파싱)”하기 위한 준비를 진행했습니다.

#### 핵심 아이디어

- HTTP 요청은 소켓의 `InputStream`으로 들어오는 **바이트/문자 스트림**이다.
- 따라서 네트워크 환경 없이도, 동일한 입력을 **테스트 코드에서 재현**할 수 있다.

#### 구현 내용

- `HttpParserTest`에서 브라우저 요청 형태의 Raw HTTP 문자열을 작성
- `ByteArrayInputStream`으로 감싸 **InputStream을 가짜로 생성**
- `HttpParser.parseHttpRequest(InputStream)`에 주입하여 파싱 로직을 테스트 기반으로 개발할 준비 완료
- CRLF(`\r\n`) 및 헤더 종료 빈 줄(`\r\n\r\n`) 구조를 테스트 데이터에 반영

### 8. HTTP 요청 파서(HttpParser) 뼈대 구현

HTTP 요청(InputStream)을 파싱하여 `HttpRequest` 객체로 변환하기 위한 파서 구조를 구현했습니다.

#### 구현 내용

- `HttpParser.parseHttpRequest(InputStream)`에서 입력 스트림을 `InputStreamReader(US-ASCII)`로 감싸 텍스트 기반 파싱 준비
- 파싱 단계를 `Request Line → Headers → Body`로 분리하여 확장 가능한 구조로 설계
- `HttpMessage`, `HttpRequest`, `HttpMethod` 등 HTTP 도메인 모델 뼈대 구성
- 파싱 오류 처리를 위해 `HttpParsingException` 및 `HttpStatusCode` enum 뼈대 구성

#### 현재 진행 상황

- `parseRequestLine()`에서 CRLF까지 읽어 요청 라인의 끝을 감지하는 단계까지 구현
- 이후 단계에서 공백(SP) 기준으로 Method/Target/Version을 분해하여 `HttpRequest`에 저장할 예정

### 🧠 학습 포인트

- 설정 파일을 외부로 분리하여 확장성과 유지보수성 확보
- JSON → 트리 → 객체 구조를 통한 유연한 파싱 전략
- ObjectMapper / ObjectWriter를 활용한 안전한 직렬화 설계
- 서버 초기화 로직과 설정 로딩 로직 분리
- `ServerSocket`과 `Socket`의 역할 차이 이해
- `accept()`의 blocking 동작 방식 이해
- HTTP 요청/응답이 단순 문자열 기반 프로토콜임을 체감
- HTTP 응답 형식(Status Line / Header / Body)의 구조 이해
- 고수준 프레임워크 없이 HTTP 서버의 본질적인 동작 원리 학습

### 🔧 사용 기술

- Java
- Jackson (JSON Parsing)
- File I/O
