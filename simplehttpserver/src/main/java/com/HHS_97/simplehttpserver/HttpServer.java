package com.HHS_97.simplehttpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.HHS_97.simplehttpserver.config.Configuration;
import com.HHS_97.simplehttpserver.config.ConfigurationManager;

/*
 * 
 * Driver Class for the Http Server
 * 
 */
public class HttpServer 
{
    public static void main( String[] args )
    {
    	// 서버 시작 로그 출력
        System.out.println( "Server starting....." );
        
        // ConfigurationManager를 통해
        // 지정된 경로의 http.json을 로드
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/java/resources/http.json");
        
        // 로드된 Configuration 객체을 가져옴
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        
        // 설정 파일에 정의된 서버 포트 출력
        System.out.println("Using Port: " + conf.getPort());
        // 설정 파일에 정의된 웹 루트 디렉토리 출력
        System.out.println("Using WebRoot: " + conf.getWebroot());
        
        
        try {
        	// 설정 파일에서 로드한 포트 번호로 ServerSocket 생성
        	// → 해당 포트에서 클라이언트의 연결 요청을 수신 대기
			ServerSocket serverSocket = new ServerSocket(conf.getPort());
			
			// 클라이언트로부터의 연결 요청을 기다림 (blocking 호출)
			// → 연결이 들어오면 해당 클라이언트와 통신하기 위한 Socket 반환
			Socket socket = serverSocket.accept();
			
			// 클라이언트(브라우저)가 보낸 요청 데이터를 읽기 위한 입력 스트림
			// → HTTP 요청 메시지(Request Line + Headers + Body)를 수신
			InputStream inputStream = socket.getInputStream();
			
			// 클라이언트(브라우저)에게 응답 데이터를 전송하기 위한 출력 스트림
			// → HTTP 응답 메시지(Status Line + Headers + Body)를 송신
			OutputStream outputStream = socket.getOutputStream();
			
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
			
			outputStream.write(response.getBytes());
					
			inputStream.close();
			outputStream.close();
			socket.close();
			serverSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
