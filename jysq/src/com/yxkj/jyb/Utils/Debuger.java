package com.yxkj.jyb.Utils;

public final class Debuger {
	
	/*
	 USE_MSG_MODE = 1 【不使用维纳斯】【本地】
	 USE_MSG_MODE = 2 【不使用维纳斯】【外网】
	 USE_MSG_MODE = 3 【使用维纳斯】【本地】
	 USE_MSG_MODE = 4 【使用维纳斯】【外网】
	*/
    private static int  USE_MSG_MODE = 2;
	public static boolean USE_LOCAL_PHPSERVER = true;
    public static boolean USE_WNS = false;
    public static boolean USE_WNS_DEBUG = false;
    static {
    	switch(USE_MSG_MODE){
    	case 1:
    		break;
	    case 2:
			USE_LOCAL_PHPSERVER = false;
	        USE_WNS = false;
	        USE_WNS_DEBUG = false;
			break;
		case 3:
			USE_LOCAL_PHPSERVER = false;
		    USE_WNS = true;
		    USE_WNS_DEBUG = true;
			break;
		case 4:
			USE_LOCAL_PHPSERVER = false;
		    USE_WNS = true;
		    USE_WNS_DEBUG = false;
			break;
		}
    };
    
    public static String  WNS_DEBUG_IP = "";//维纳斯调试ip地址
    public static int  WNS_DEBUG_IP_PORT = 8080;//维纳斯调试端口号
    public static boolean USE_WNS_ASYNCTASK = true;//使用asynctask 还是 runnable
}