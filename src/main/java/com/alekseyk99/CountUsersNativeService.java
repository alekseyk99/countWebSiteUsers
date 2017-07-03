package com.alekseyk99;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Counts users on a site, using build-in implementation of session tracking mechanism 
 *
 */
public class CountUsersNativeService {
	
    //private static final Logger logger = LoggerFactory.getLogger(CountUsersNativeService.class.getCanonicalName());
  
    // how precisely keep information
	static final long INTERVAL_SIZE = 1000L ; // 1 second

	Counter counter= new Counter();
    
	public void processUserTime(String name, long requestTime, long lastRequestTime) {
		long normalRequestTime = requestTime/INTERVAL_SIZE;
		counter.register(normalRequestTime);
		if (lastRequestTime>0) { // have information about previous request
			long normalLastRequestTime = lastRequestTime/INTERVAL_SIZE;
			counter.deregister(normalRequestTime, normalLastRequestTime);
		}
	}
	
	// calculate number of current users
	public int countUsers(long time) {
		return counter.count(time/INTERVAL_SIZE);
	}			
	
}