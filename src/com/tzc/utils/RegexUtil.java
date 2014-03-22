package com.tzc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;



public class RegexUtil {
	private static Logger logger = LogUtil.getLogger();
	
	public static String getStringByRegex(String content,String regex){
		if(regex == null)return null;
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE |Pattern.DOTALL );
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			try {
				return matcher.group(1);
			} catch (Exception e) {
				logger.error("#正则匹配出错");
				return null;
			}
			
		}
		return null;
	}
}
