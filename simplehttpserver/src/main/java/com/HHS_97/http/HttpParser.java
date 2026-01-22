package com.HHS_97.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);
	
	private static final int SP = 0x20; // 32
	private static final int CR = 0x0D; // 13
	private static final int LF = 0x0A; // 10
	
	//InputStream으로 들어오는 Http 요청을 파싱하여 HttpRequest 객체로 변환
	public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
		
		//바이트 기반으로 들어오는 InputStream을 문자 단위로 읽기 위해 InputStreamReader로 감쌈 HTTP 헤더는 ASCII 기반 텍스트이므로 US_ASCII 사용
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
		
		//파싱 결과를 담을 HttpRequest 객체 생성
		HttpRequest request = new HttpRequest();
		
		try {
			parseRequestLine(reader, request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			parseHeaders(reader, request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseBody(reader, request);
		
		return request;
	}

	/**
	 * HTTP 요청의 첫 줄(Request Line)을 읽어 처리
	 * 현재는 CRLF를 만나 요청 라인의 끝을 감지하는 단계까지 구현
	 * @throws HttpParsingException 
	 */
	private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
		StringBuilder processingDataBuffer = new StringBuilder();
		
		boolean methodParsed = false;
		boolean requestTargetParsed = false;
		
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
					// CRLF 직전까지 모인 HTTP 버전을 로그로 확인
					LOGGER.debug("Request Line VERSION to Process : {}", processingDataBuffer.toString());
					if (!methodParsed || !requestTargetParsed) {
						throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
					}
					
					try {
						request.setHttpVersion(processingDataBuffer.toString());
					} catch (BadHttpVersionException e) {
						throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
					}
					
					return;
				} else {
					//CR 다음 byte가 LF가 아닐경우
					throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
				}
			}
			
			// SP을 만나면 method/target 중 하나가 끝났다고 판단
			if (_byte == SP) {
				// 아직 method 토큰을 처리하지 않았다면
				if (!methodParsed) {
					// method을 로그로 확인
					LOGGER.debug("Request Line METHOD to Process : {}", processingDataBuffer.toString());
					request.setMethod(processingDataBuffer.toString());
					// method 처리가 끝났음을 표시
					methodParsed = true;
				} else if (!requestTargetParsed) { // method는 처리했고 target은 아직이면
					// request target을 로그로 확인
					LOGGER.debug("Request Line REQ TARGET to Process : {}", processingDataBuffer.toString());
					request.setRequestTarget(processingDataBuffer.toString());
					// request target 처리가 끝났음을 표시
					requestTargetParsed = true;
				} else {
					throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
				}
				// 다음 토큰을 받기 위해 버퍼 내용을 비움
				processingDataBuffer.delete(0, processingDataBuffer.length());
			} else {// 현재 문자가 공백이 아니라면
				// 현재 문자를 버퍼에 누적해서 토큰 문자열을 만들어감
				processingDataBuffer.append((char)_byte);
				if (!methodParsed) {
					//메서드가 아직 전달되지 않았고, 처리 버퍼의 길이가 이미 메서드의 maxLength보다 큰 경우
					if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
						throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
					}
				}
			}
		}
		
	}

	private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
		StringBuilder processingDataBuffer = new StringBuilder();
		boolean crlfFound = false;
		
		int _byte;
		
		//스트림에서 문자를 하나씩 읽음
		while ((_byte = reader.read()) >= 0) {
			//CR(Carriage Return)을 만나면
			if(_byte == CR) {
				//다음 문자를 읽어 LF인지 확인
				_byte = reader.read();
				//CRLF 조합이면 Request Line의 끝으로 판단
				if (_byte == LF) {
					if (!crlfFound) {
						crlfFound = true;
						
						// Do things like processing
						processSingelHeaderField(processingDataBuffer, request);
						// Clear the buffer
						processingDataBuffer.delete(0, processingDataBuffer.length());
					}
					
					
				} else {
					//CR 다음 byte가 LF가 아닐경우
					throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
				}
			} else {
				crlfFound = false;
				// Append to Buffer
				processingDataBuffer.append((char) _byte);
			}
		}
	}

	private void processSingelHeaderField(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {
		String rawHeaderField = processingDataBuffer.toString();
		Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&'*+\\-./^_`|~\\da-zA-Z]+):\\s*(?<fieldValue>.*)\\s*$");
		
		Matcher matcher = pattern.matcher(rawHeaderField);
		if (matcher.matches()) {
			// We found a proper header
			String fieldName = matcher.group("fieldName");
			String fieldValue = matcher.group("fieldValue").trim();
			request.addHeader(fieldName.toLowerCase(), fieldValue);
		} else {
			throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}
	}

	private void parseBody(InputStreamReader reader, HttpRequest request) {
		
	}
}
