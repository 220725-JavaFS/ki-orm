package com.revature.orm.utils;

public class StringUtil {
	
	public static String sqlStringHelper(int num, String str) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<num; i++) {
			sb.append(str);
			if (i<(num-1)) sb.append(", "); 
		}
		return new String(sb);
	}

}
