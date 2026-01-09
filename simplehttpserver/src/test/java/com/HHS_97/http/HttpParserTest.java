package com.HHS_97.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스당 테스트 인스턴스 1개만 생성
class HttpParserTest { // HttpParser 동작을 검증하는 테스트 클래스
	
	// 테스트 대상(HTTP 요청 파서)
	private HttpParser httpParser;
	
	@BeforeAll
	public void beforeClass() { // 전체 테스트 실행 전 1회 수행
		httpParser = new HttpParser(); // 파서 인스턴스 생성
	}

	@Test
	void parseHttpRequest() throws IOException { // "유효한 HTTP 요청"을 파서가 처리 가능한지 확인하는 테스트
		httpParser.parseHttpRequest( // 파서의 요청 파싱 메서드 호출
				generateValidTestCase() // 실제 소켓 입력처럼 보이도록 만든 InputStream 전달
		);
	}
	
	private InputStream generateValidTestCase() { // 테스트용 "정상 HTTP 요청" InputStream 생성기
		String rawData = "GET / HTTP/1.1\r\n" // Request Line(메서드/경로/버전)
				+ "Host: localhost:8080\r\n" // 헤더: Host
				+ "Connection: keep-alive\r\n" // 헤더: 연결 유지
				+ "sec-ch-ua: \"Chromium\";v=\"142\", \"Whale\";v=\"4\", \"Not.A/Brand\";v=\"99\"\r\n" // 헤더들(브라우저가 자동으로 붙임)
				+ "sec-ch-ua-mobile: ?0\r\n"
				+ "sec-ch-ua-platform: \"Windows\"\r\n"
				+ "Upgrade-Insecure-Requests: 1\r\n"
				+ "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Whale/4.35.351.13 Safari/537.36\r\n"
				+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
				+ "Sec-Fetch-Site: none\r\n"
				+ "Sec-Fetch-Mode: navigate\r\n"
				+ "Sec-Fetch-User: ?1\r\n"
				+ "Sec-Fetch-Dest: document\r\n"
				+ "Accept-Encoding: gzip, deflate, br, zstd\r\n"
				+ "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\r\n"
				+ "\r\n"; // 빈 줄: 헤더 종료(HTTP 요청에서 매우 중요)
		
		InputStream inputStream = new ByteArrayInputStream(  // 문자열 데이터를 InputStream처럼 읽히게 감쌈
				rawData.getBytes( // 문자열을 바이트 배열로 변환
						StandardCharsets.US_ASCII // 테스트 환경에 상관없이 동일한 바이트가 되도록 ASCII 지정
				)
		);
		
		return inputStream;
	}
}
