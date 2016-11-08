package com.workflow.engine.core.common.utils;

public class OSUtil {
	public static boolean isLinux(){
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
		 return false;  
		}  
		return true;
	}
	public static boolean isWindows(){
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
		 return true;  
		}  
		return false;
	}
}
