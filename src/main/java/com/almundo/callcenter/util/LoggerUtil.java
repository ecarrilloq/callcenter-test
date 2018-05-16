package com.almundo.callcenter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	
	private LoggerUtil() {
		//Private constructor to hide the default public
	}
	
	public static Logger getLogger(Class c) {
		return LoggerFactory.getLogger(c);
	}
	
}
