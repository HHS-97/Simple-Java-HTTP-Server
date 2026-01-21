package com.HHS_97.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class HttpVersionTest {
	
	@Test
	void getBestCompatibleVersionExactMatch() {
		HttpVersion version = null;
		try {
			version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
		} catch (BadHttpVersionException e) {
			fail();
		}
		
		assertNotNull(version);
		assertEquals(version, HttpVersion.HTTP_1_1);
	}
	
	@Test
	void getBestCompatibleVersionBadFormat() { //잘못된 요청을 보내면 어떤 일이 일어나는지 테스트
		HttpVersion version = null;
		try {
			version = HttpVersion.getBestCompatibleVersion("http/1.1");
			fail();
		} catch (BadHttpVersionException e) {
			
		}
	}
	
	@Test
	void getBestCompatibleVersionHigherVersion() { //잘못된 버전을 보내면 어떤 일이 일어나는지 테스트
		HttpVersion version = null;
		try {
			version = HttpVersion.getBestCompatibleVersion("HTTP/1.2");
			assertNotNull(version);
			assertEquals(version, HttpVersion.HTTP_1_1);
		} catch (BadHttpVersionException e) {
			fail();
		}
	}

}
