package com.everyting.server.util;

import com.everyting.server.exception.ETException;

public class ETUtils {
	
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new ETException("IllegalArgumentException", "ETUtils throws IllegalArgumentException while safeLongToInt",
	         " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
}
