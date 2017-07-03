package com.alekseyk99;

import static org.junit.Assert.*;

import org.junit.Test;

public class CountUsersCustomServiceTest {

	@Test
	public void testCustomService() {

		    //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		    CountUsersCustomService customService = new CountUsersCustomService();
		
		    long time=System.currentTimeMillis();
		    assertEquals("should be 0", 0, customService.countUsers(time));
		    customService.processUserTime("a", time);
		    customService.processUserTime("a", time);
		    assertEquals("should be 1", 1, customService.countUsers(time));
		    
		    customService.processUserTime("a", time+1000);
		    customService.processUserTime("b", time+1000);
		    customService.processUserTime("c", time+1000);
		    assertEquals("should be 3", 3, customService.countUsers(time));
		    
		    customService.processUserTime("a", time+2000);
		    customService.processUserTime("b", time+2000);
		    customService.processUserTime("c", time+2000);
		    assertEquals("should be still 3", 3, customService.countUsers(time));
		    
		    customService.processUserTime("c", time+302000);
		    assertEquals("should be 1", 1, customService.countUsers(time+302000));
		    
		    assertEquals("should be 0", 0, customService.countUsers(time+602000));
		    
	    }
	}

