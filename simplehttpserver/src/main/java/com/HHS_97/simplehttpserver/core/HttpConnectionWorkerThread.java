package com.HHS_97.simplehttpserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 클라이언트 1명과의 HTTP 통신을 처리하는 워커 스레드
 * 
 * - ServerSocket으로부터 전달받은 Socket을 기반으로 HTTP 요청을 수신하고 응답을 전송한다.
 * - 각 클라이언트 연결마다 하나의 스레드가 생성되어 동시에 여러 클라이언트 요청을 처리할 수 있도록 한다.
 */

public class HttpConnectionWorkerThread extends Thread {
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
	private Socket socket;
	
	public HttpConnectionWorkerThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		try {
			// 클라이언트(브라우저)가 보낸 요청 데이터를 읽기 위한 입력 스트림
			// → HTTP 요청 메시지(Request Line + Headers + Body)를 수신
			inputStream = socket.getInputStream();
			
			// 클라이언트(브라우저)에게 응답 데이터를 전송하기 위한 출력 스트림
			// → HTTP 응답 메시지(Status Line + Headers + Body)를 송신
			outputStream = socket.getOutputStream();
			
			// 브라우저로 보낼 html
			String html = "<html lang=\"ko\"><head><meta charset=\"UTF-8\"></head><title>Simple Java HTTP Server</title><body><h1>이 서버는 제가 만든 simple java http 서버를 사용하고 있습니다.</h1></body></html>";
			
			final String CRLF = "\n\r"; // 아스키코드 13, 10 줄바꿈 문자
			
			// html을 그냥 보내면 브라우저가 어떻게 처리해야할지 모르니까 http 응답으로 감싸기, http 1.1 표준을 준수하는 응답
			String response = 
					"HTTP/1.1 200 OK" + CRLF + // 상태 표시줄 : HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
					"Content-Length: " + html.getBytes().length + CRLF + // HEADER
					CRLF +
					html +
					CRLF + CRLF
					;
			
			// http응답을 클라이언트로 전송
			outputStream.write(response.getBytes());
			
			LOGGER.info("Connection Processing Finished.");
		} catch (IOException e) {
			LOGGER.error("Problem with communication", e);
		} finally {
			// 자원 정리
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
	
	

}
