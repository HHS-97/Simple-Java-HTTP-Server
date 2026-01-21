package com.HHS_97.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {
	HTTP_1_1("HTTP/1.1", 1, 1);
	
	public final String LITERAL;
	public final int MAJOR;
	public final int MINOR;
	
	HttpVersion(String lITERAL, int mAJOR, int mINOR) {
		this.LITERAL = lITERAL;
		this.MAJOR = mAJOR;
		this.MINOR = mINOR;
	}
	
	private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)");
	
	public static HttpVersion getBestCompatibleVersion(String literalVersion) throws BadHttpVersionException {
		Matcher matcher = httpVersionRegexPattern.matcher(literalVersion);
		if (!matcher.find() || matcher.groupCount() != 2) {
			throw new BadHttpVersionException();
		}
		int major = Integer.parseInt(matcher.group("major"));
		int minor = Integer.parseInt(matcher.group("minor"));
		
		HttpVersion tempBestCompatible = null;
		for (HttpVersion version : HttpVersion.values()) {
			if (version.LITERAL.equals(literalVersion)) {
				return version;
			} else {
				if (version.MAJOR == major) {
					if (version.MINOR < minor) {
						tempBestCompatible = version;
					}
				}
			}
		}
		
		return tempBestCompatible;
	}
}
