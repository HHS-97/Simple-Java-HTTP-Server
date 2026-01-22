package com.HHS_97.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage{ // HTTP 요청(Request)을 표현하는 도메인 객체

	private HttpMethod method;
	private String requestTarget;
	private String originalHttpVersion; // 요청에서 받은 결과
	private HttpVersion bestCompatibleHttpVersion;
	private HashMap<String, String> headers = new HashMap<>(); 
	
	HttpRequest() {
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getRequestTarget() { // 파싱된 요청 대상 URI 조회
		return requestTarget;
	}

	public HttpVersion getBestCompatibleHttpVersion() {
		return bestCompatibleHttpVersion;
	}

	public String getOriginalHttpVersion() {
		return originalHttpVersion;
	}
	
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}
	
	public String getHeader(String headerName) {
		return headers.get(headerName.toLowerCase());
	}

	void setMethod(String methodName) throws HttpParsingException { // 문자열로 전달된 메서드를 검증 후 설정
		for (HttpMethod method : HttpMethod.values()) { // 서버가 지원하는 모든 HTTP 메서드를 순회
			if (methodName.equals(method.name())) { // 전달된 메서드가 지원 목록에 있으면
				this.method = method; // enum 타입의 HTTP 메서드로 설정
				return; // 메서드 설정 완료 후 종료
			}
		}
		// 지원하지 않는 HTTP 메서드인 경우 501 Not Implemented 오류 발생
		throw new HttpParsingException(
				HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
		);
	}

	public void setRequestTarget(String requestTarget) throws HttpParsingException { // 요청 대상 URI를 검증 후 설정
		if (requestTarget == null || requestTarget.length()==0) { // 요청 대상이 비어 있거나 null인 경우
			// 서버 내부에서 처리할 수 없는 요청으로 판단하여 500 오류 발생
			throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
		}
		this.requestTarget = requestTarget; // 유효한 요청 대상 URI 저장
	}

	public void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
		this.originalHttpVersion = originalHttpVersion;
		this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
		if (this.bestCompatibleHttpVersion == null) { //값이 null이면 호환되는 버전이 없음
			throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
		}
	}
	
	void addHeader(String headerName, String headerField) {
		headers.put(headerName.toLowerCase(), headerField);
	}
}
