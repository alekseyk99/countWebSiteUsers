package com.alekseyk99;

import static org.junit.Assert.*;

import org.junit.Test;

public class CountUsersNativeServiceTest {

	@Test
	public void testCustomService_() {
			
		    //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		    CountUsersNativeService nativeService = new CountUsersNativeService();
		
		    long time=System.currentTimeMillis();
		    assertEquals("should be empty", 0, nativeService.countUsers(time));
		    nativeService.processUserTime("a", time, 0);
		    nativeService.processUserTime("a", time, time);
		    assertEquals("should be only 1", 1, nativeService.countUsers(time));
		    
		    nativeService.processUserTime("a", time+1000, time);
		    nativeService.processUserTime("b", time+1000, 0);
		    nativeService.processUserTime("c", time+1000, 0);
		    assertEquals("should be 3", 3, nativeService.countUsers(time));
		    
		    nativeService.processUserTime("a", time+2000, time+1000);
		    nativeService.processUserTime("b", time+2000, time+1000);
		    nativeService.processUserTime("c", time+2000, time+1000);
		    assertEquals("should be still 3", 3, nativeService.countUsers(time));
		    
		    nativeService.processUserTime("c", time+302000, time+2000);
		    assertEquals("should be 1", 1, nativeService.countUsers(time+302000));
		    
		    assertEquals("should be 0", 0, nativeService.countUsers(time+602000));
		    
	    }
	}

