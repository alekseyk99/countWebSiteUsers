package com.alekseyk99;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener{

	static { System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG"); }
	private static final Logger logger = LoggerFactory.getLogger(CountUsersNativeService.class.getCanonicalName());
	   	
    public void contextInitialized(ServletContextEvent sce) {   
	   logger.info("contextInitialized");
	   
	   ServletContext context = sce.getServletContext();
	   context.setAttribute("customService", new CountUsersCustomService());
	   context.setAttribute("nativeService", new CountUsersNativeService());
    }
    
    public void contextDestroyed(ServletContextEvent sce) {
	   logger.info("contextDestroyed");
	   
	   ServletContext context = sce.getServletContext();
	   context.removeAttribute("customService");
	   context.removeAttribute("nativeService");
    }
}