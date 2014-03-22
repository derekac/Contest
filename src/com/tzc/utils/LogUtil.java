package com.tzc.utils;

import org.apache.log4j.Logger;

public class LogUtil {
    private static Logger logger = createLogger();

    public static Logger getLogger() {
            return logger;
    }

    private static Logger createLogger() {
            return Logger.getLogger(Thread.currentThread().getClass().getName());
    }
    
}
