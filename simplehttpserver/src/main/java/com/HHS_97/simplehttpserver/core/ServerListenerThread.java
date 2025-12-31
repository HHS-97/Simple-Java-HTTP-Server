package com.HHS_97.simplehttpserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서버의 메인 리스너 스레드
 * 
 * - 지정된 포트에서 ServerSocket을 열고 클라이언트 연결을 지속적으로 수신한다.
 * - 클라이언트가 연결되면 Socket을 생성하고, 실제 HTTP 통신 처리는 HttpConnectionWorkerThread에 위임한다.
 * - 이 스레드는 "연결 수락(accept)"만 담당하고, 요청/응답 처리는 워커 스레드에서 수행된다.
 */

public class ServerListenerThread extends Thread {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
	
	// 서버가 바인딩될 포트 번호
	private int port;
	
	// 웹 루트 디렉토리
	private String webPort;
	
	// 클라이언트 연결을 수신하는 서버 소켓
	private ServerSocket serverSocket;

	public ServerListenerThread(int port, String webPort) throws IOException {
		this.port = port;
		this.webPort = webPort;
		this.serverSocket = new ServerSocket(this.port);
	}
	
	/**
     * 서버 리스너 스레드의 실행 로직
     * 
     * - ServerSocket이 정상 상태인 동안 클라이언트 연결 요청을 계속 수락한다.
     * - 각 연결은 별도의 워커 스레드에서 처리되어 Multiple Connections 구조를 구현한다.
     */
	@Override
	public void run() {
		try {
			
			// Multiple Connection을 위한 while루프, 서버가 양호한 동안 연결은 계속 수락된다.
			while (serverSocket.isBound() && !serverSocket.isClosed()) {
				// 클라이언트로부터의 연결 요청을 기다림 (blocking 호출)
				// → 연결이 들어오면 해당 클라이언트와 통신하기 위한 Socket 반환
				Socket socket = serverSocket.accept();
				
				LOGGER.info(" * Connection accepted: " + socket.getInetAddress());
				
				// HttpConnection을 다른 쓰레드에서 진행하기 위해 분리
				HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket);
				workerThread.start();
			}
			
		} catch (IOException e) {
			LOGGER.error("Problem with setting socket", e);
		} finally {
			if (serverSocket!=null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// 실제로 예외를 잡지만 아무것도 하지않고 무시함
				}
			}
		}
	}
	
	

}
