package com.HHS_97.simplehttpserver;

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
    }
}
