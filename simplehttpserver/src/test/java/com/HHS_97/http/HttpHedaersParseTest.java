package com.HHS_97.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import javax.management.RuntimeErrorException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHedaersParseTest {

	private HttpParser httpParser;
	private Method parseHeadersMethod;
	
	@BeforeAll
	public void beforeClass() throws NoSuchMethodException { 
		httpParser = new HttpParser();
		Class<HttpParser> cls = HttpParser.class;
		parseHeadersMethod = cls.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
		parseHeadersMethod.setAccessible(true);
	}
	
	@Test
	public void testSimpleSingleHeader() throws IllegalAccessException, InvocationTargetException {
		HttpRequest request = new HttpRequest();
		parseHeadersMethod.invoke(
				httpParser,
				generateSimpleSingleHeaderMessage(),
				request);
		assertEquals(1, request.getHeaderNames().size());
		assertEquals("localhost:8080", request.getHeader("host"));
	}
	
	@Test
	public void testMultipleHeader() throws IllegalAccessException, InvocationTargetException {
		HttpRequest request = new HttpRequest();
		parseHeadersMethod.invoke(
				httpParser,
				generateMultipleHeaderMessage(),
				request);
		assertEquals(14, request.getHeaderNames().size());
		assertEquals("localhost:8080", request.getHeader("host"));
	}
	
	@Test
	public void testErrorSpaceBeforeColonHeader() throws IllegalAccessException, InvocationTargetException {
		HttpRequest request = new HttpRequest();
		try {
			parseHeadersMethod.invoke(
					httpParser,
					generateSpaceBeforeColonErrorHeaderMessage(),
					request);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof HttpParsingException) {
				assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ((HttpParsingException)e.getCause()).getErrorCode());
			}
		}
	}
	
	private InputStreamReader generateSimpleSingleHeaderMessage() {
		String rawData = "Host: localhost:8080\r\n"; // 헤더: Host
//				+ "Connection: keep-alive\r\n" // 헤더: 연결 유지
//				+ "sec-ch-ua: \"Chromium\";v=\"142\", \"Whale\";v=\"4\", \"Not.A/Brand\";v=\"99\"\r\n" // 헤더들(브라우저가 자동으로 붙임)
//				+ "sec-ch-ua-mobile: ?0\r\n"
//				+ "sec-ch-ua-platform: \"Windows\"\r\n"
//				+ "Upgrade-Insecure-Requests: 1\r\n"
//				+ "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Whale/4.35.351.13 Safari/537.36\r\n"
//				+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
//				+ "Sec-Fetch-Site: none\r\n"
//				+ "Sec-Fetch-Mode: navigate\r\n"
//				+ "Sec-Fetch-User: ?1\r\n"
//				+ "Sec-Fetch-Dest: document\r\n"
//				+ "Accept-Encoding: gzip, deflate, br, zstd\r\n"
//				+ "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\r\n"
//				+ "\r\n"; // 빈 줄: 헤더 종료(HTTP 요청에서 매우 중요)
		
		InputStream inputStream = new ByteArrayInputStream(  // 문자열 데이터를 InputStream처럼 읽히게 감쌈
				rawData.getBytes( // 문자열을 바이트 배열로 변환
						StandardCharsets.US_ASCII // 테스트 환경에 상관없이 동일한 바이트가 되도록 ASCII 지정
				)
		);
		
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
		return reader;
	}
	
	private InputStreamReader generateMultipleHeaderMessage() {
		String rawData = "Host: localhost:8080\r\n" // 헤더: Host
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
		
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
		return reader;
	}
	
	private InputStreamReader generateSpaceBeforeColonErrorHeaderMessage() {
		String rawData = "Host: localhost:8080\r\n"; // 헤더: Host
		
		InputStream inputStream = new ByteArrayInputStream(  // 문자열 데이터를 InputStream처럼 읽히게 감쌈
				rawData.getBytes( // 문자열을 바이트 배열로 변환
						StandardCharsets.US_ASCII // 테스트 환경에 상관없이 동일한 바이트가 되도록 ASCII 지정
				)
		);
		
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
		return reader;
	}
}
