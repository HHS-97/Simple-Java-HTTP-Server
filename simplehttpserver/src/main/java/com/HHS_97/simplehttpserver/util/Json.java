package com.HHS_97.simplehttpserver.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {
	
	private static ObjectMapper myObjectMapper = defaultObjectMapper();
	
	public static ObjectMapper defaultObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		// 역직렬화 시 DTO에 정의되지 않은(JSON에만 존재하는) 속성이 있어도
		// 예외를 발생시키지 않고 무시하도록 설정
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return om;
	}
	
	// JSON 문자열을 파싱하여 Jackson의 트리 모델(JsonNode)로 변환
	// → JSON 구조를 동적으로 탐색하거나 부분 추출할 때 사용
	public static JsonNode parse(String jsonSrc) throws IOException {
		return myObjectMapper.readTree(jsonSrc);
	}
	
	// JsonNode 트리 객체를 지정한 Java 클래스 타입으로 역직렬화
	// → JsonNode → DTO/VO 등 POJO 객체로 변환할 때 사용
	public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
		return myObjectMapper.treeToValue(node, clazz);
	}
	
	public static JsonNode toJson(Object obj) {
		return myObjectMapper.valueToTree(obj);
	}
	
	public static String stringify(JsonNode node) throws JsonProcessingException {
		return generateJson(node, false);
	}
	
	public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
		return generateJson(node, true);
	}
	
	// Java 객체를 JSON 문자열로 직렬화하며,
	// pretty 출력 여부를 옵션으로 제어하는 유틸 메서드
	@SuppressWarnings("unused")
	private static String generateJson(Object o, boolean pretty) throws JsonProcessingException {
		// ObjectMapper의 설정을 복사한 쓰기 전용 ObjectWriter 생성
	    // (thread-safe하며 직렬화 옵션을 개별적으로 확장 가능)
		ObjectWriter objectWriter = myObjectMapper.writer();
		
		// pretty 옵션이 true인 경우
	    // JSON을 들여쓰기(INDENT_OUTPUT)된 형태로 직렬화
		if (pretty) {
			objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
		}
		
		// Java 객체 → JSON 문자열 변환 (직렬화)
		return objectWriter.writeValueAsString(o);
	}
}
