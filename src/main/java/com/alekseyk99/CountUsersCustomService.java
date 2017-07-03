package com.alekseyk99;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Counts users on a site, using custom implementation session tracking mechanism 
 * Use 2 maps to keep current and previous periods so old data can be easily deleted 
 *
 */
public class CountUsersCustomService {
	
    private static final Logger logger = LoggerFactory.getLogger(CountUsersCustomService.class.getCanonicalName());
    // how precisely keep information
	static final long INTERVAL_SIZE = 1000L ; // 1 second
	// how long keep information
	static final int CAPACITY = 300; // 5 minutes
  
	final private Counter counter = new Counter();
	
	AtomicReference<TimeMap> atomicCurrentPeriod = new AtomicReference<TimeMap>(new TimeMap(0));
	AtomicReference<TimeMap> atomicPrevPeriod = new AtomicReference<TimeMap>(new TimeMap(0));
	
	/**
	 * Keeps Map and corresponding timePeriod
	 */
	static class TimeMap {
		final private long timePeriod;
		final private ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
		
		TimeMap(long timePeriod) {
			this.timePeriod = timePeriod;
		}
		
		long getTimePeriod() {
			return timePeriod;
		}
		
		Map<String, Long> getMap() {
			return map;
		}
	}
	
	/**
	 * Add information about current request
	 * 
	 * @param name Corresponding name of request 
	 * @param requestTime Time of request
	 */
	public void processUserTime(String name, long requestTime) {
		
		// normalized version
		long normalRequestTime = requestTime/INTERVAL_SIZE;
		//Beginning of the period of a multiple of CAPACITY
		long periodBegin = normalRequestTime/CAPACITY;

		TimeMap currentPeriod = atomicCurrentPeriod.get();
		logger.debug("Session={}, time={}, currentPeriod={}, periodBegin={}",name,requestTime, currentPeriod.getTimePeriod(),periodBegin);

		if (currentPeriod.getTimePeriod() < periodBegin) { // current period is out of time
			// previous became current, old previous can be deleted
			TimeMap prevPeriod = atomicPrevPeriod.get();
			logger.debug("Current period is out of time. Prev={}",prevPeriod.getTimePeriod());
			
			if (prevPeriod!=currentPeriod) {
				logger.debug("Prev != Current");
				atomicPrevPeriod.compareAndSet(prevPeriod, currentPeriod); 
				// if unsuccessfully another thread already did it  
			} // if (prevPeriod == currentPeriod) another thread already did it
			
			// replace current with new one
			TimeMap newPeriod = new TimeMap(periodBegin);
			atomicCurrentPeriod.compareAndSet(currentPeriod, newPeriod);
			// if unsuccessfully another thread already did it
			currentPeriod = atomicCurrentPeriod.get();
		}

		// get time of last request
		Long normalLastRequestTimeWraper = currentPeriod.getMap().put(name, normalRequestTime);
		if (normalLastRequestTimeWraper==null) {
			normalLastRequestTimeWraper = atomicPrevPeriod.get().getMap().put(name, normalRequestTime);
		}
		
		counter.register(normalRequestTime);
		if (normalLastRequestTimeWraper !=null) {
			// have information about previous request
			counter.deregister(normalRequestTime, normalLastRequestTimeWraper.longValue());
		}
	}
	
	
	// calculate number of current users
	public int countUsers(long time) {
		return counter.count(time/INTERVAL_SIZE);
	}			
	
}