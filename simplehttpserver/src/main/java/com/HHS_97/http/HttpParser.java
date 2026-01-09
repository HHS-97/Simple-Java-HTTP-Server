package com.HHS_97.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);
	
	private static final int SP = 0x20; // 32
	private static final int CR = 0x0D; // 13
	private static final int LF = 0x0A; // 10
	
	//InputStream으로 들어오는 Http 요청을 파싱하여 HttpRequest 객체로 변환
	public HttpRequest parseHttpRequest(InputStream inputStream) throws IOException {
		
		//바이트 기반으로 들어오는 InputStream을 문자 단위로 읽기 위해 InputStreamReader로 감쌈 HTTP 헤더는 ASCII 기반 텍스트이므로 US_ASCII 사용
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
		
		//파싱 결과를 담을 HttpRequest 객체 생성
		HttpRequest request = new HttpRequest();
		
		parseRequestLine(reader, request);
		parseHeaders(reader, request);
		parseBody(reader, request);
		
		return request;
	}

	/**
	 * HTTP 요청의 첫 줄(Request Line)을 읽어 처리
	 * 현재는 CRLF를 만나 요청 라인의 끝을 감지하는 단계까지 구현
	 */
	private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException {
		
		//reader에서 읽은 문자(EOF 판별을 위해 int 사용)
		int _byte;
		
		//스트림에서 문자를 하나씩 읽음
		while ((_byte = reader.read()) >= 0) {
			//CR(Carriage Return)을 만나면
			if(_byte == CR) {
				//다음 문자를 읽어 LF인지 확인
				_byte = reader.read();
				//CRLF 조합이면 Request Line의 끝으로 판단
				if (_byte == LF) {
					return;
				}
			}
		}
		
	}

	private void parseHeaders(InputStreamReader reader, HttpRequest request) {
		
	}

	private void parseBody(InputStreamReader reader, HttpRequest request) {
		
	}
}
