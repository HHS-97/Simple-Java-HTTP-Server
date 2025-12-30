package com.HHS_97.simplehttpserver.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.HHS_97.simplehttpserver.util.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class ConfigurationManager {
	
	private static ConfigurationManager myConfigurationManager;
	private static Configuration myCurrentConfiguration;

	public ConfigurationManager() {
	}

	public static ConfigurationManager getInstance() {
		if (myConfigurationManager==null) {
			myConfigurationManager = new ConfigurationManager();
		}
		
		return myConfigurationManager;
	}
	
	/*
	 * 제공된 파일 경로의 설정(JSON) 파일을 읽어
	 * 현재 애플리케이션 설정(Configuration 객체)으로 로드한다.
	 */
	@SuppressWarnings("resource")
	public void loadConfigurationFile(String filePath) {
		
		// 설정 파일을 문자 스트림으로 읽기 위한 FileReader 생성
		FileReader fileReader;
		try {
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			throw new HttpConfigurationException(e);
		}
		
		// 파일 전체 내용을 문자열로 누적하기 위한 버퍼
		StringBuffer sb = new StringBuffer();
		int i;
		
		// 파일 끝(-1)에 도달할 때까지 한 문자씩 읽어 StringBuffer에 추가
		try {
			while ( (i = fileReader.read()) != -1) {
				sb.append((char)i);
			}
		} catch (IOException e) {
			throw new HttpConfigurationException(e);
		}
		
		// JSON 문자열 → JsonNode 트리 구조로 파싱
		JsonNode conf;
		try {
			conf = Json.parse(sb.toString());
		} catch (IOException e) {
			throw new HttpConfigurationException("Error parsing the Configuration File", e);
		}
		
		// JsonNode → Configuration 객체로 역직렬화
		try {
			myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
		} catch (JsonProcessingException e) {
			throw new HttpConfigurationException("Error parsing the Configuration file, internal", e);
		}
	}
	
	/*
	 * 현재 로드된 구성을 반환
	 */
	public Configuration getCurrentConfiguration() {
		if ( myCurrentConfiguration == null) {
			throw new HttpConfigurationException("No Current Configuration Set.");
		}
		return myCurrentConfiguration;
	}
}

