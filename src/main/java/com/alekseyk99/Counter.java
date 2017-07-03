package com.alekseyk99;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process information based on request time and previous request time
 *
 */
public class Counter {
	
	private static final Logger logger = LoggerFactory.getLogger(Counter.class.getCanonicalName());
	
	static final int CAPACITY = 300; // 5 minutes
	final private TimeSlot[] times = new TimeSlot[CAPACITY];
	
	// constructor
	Counter() {
		for (int i=0;i< times.length; i++) {
			times[i] = new TimeSlot();
		}
	}
	
	/** 
	 * Works with timeslots
	 * 
	 *
	 *
	 */
	static class TimeSlot {

		private long timestamp;
		private long count;

		/**
		 * Registers information about time.
		 * Increases counter with corresponded time.
		 * 
		 * @param time
		 */
		synchronized void register(long time)  {
			// add counting to time slot
			if (timestamp == time ) {
				logger.debug("Add to existed timeSlot. Was {}",count);
				count++;
			} else if (timestamp < time) {
				logger.debug("Create new timeSlot");
				// old data, replace with new one
				timestamp=time;
				count = 1;
			} else {
				// cann't be (usually)
				logger.debug("Error: timeslot has time in the future");
			};
		}
		
		/**
		 * Deregisters information about time.
		 * Decreases counter with corresponded time.
		 * 
		 * @param time
		 */
		synchronized void deregister(long time)  {
			if (timestamp == time) {
				logger.debug("Deregister from existed timeSlot. Was {}",count);
				count--;
			} else {	
				// cann't be
				logger.debug("Error: timeSlot hasn't information about previous request");
			}
		}

		synchronized long getCounter(long time)  {
			// count only data within period and skip old
			return (timestamp > time)?count:0L; 
		}

	}
	
	/**
	 * Registers information about request.
	 * Increases counter of corresponded slot.
	 *   
	 * @param requestTime Normalized request time
	 */
	void register(long requestTime) {
		int index = (int)(requestTime % CAPACITY);
		logger.debug("currentIndex={}",index);
		TimeSlot timeSlot = times[index];
		timeSlot.register(requestTime);
	}
	
	/**
	 * Deregisters information about request.
	 * Decreases counter of corresponded slot.
	 * 
	 * @param requestTime Normalized request time
	 * @param lastRequestTime Normalized time of previous request
	 */
	void deregister(long requestTime, long lastRequestTime) {
		// begin of relevant interval
		long intervalBegin = requestTime - CAPACITY;
		if (lastRequestTime > intervalBegin) {
			// delete from previous and still relevant time slot
			// to not count same session several times
			int indexLastRequest = (int)(lastRequestTime % CAPACITY);
			logger.debug("Previos lastRequestTime={} indexLastRequest={}",lastRequestTime,indexLastRequest);
			TimeSlot timeSlotLatestRequest = times[indexLastRequest];
			timeSlotLatestRequest.deregister(lastRequestTime);
		}	
	}
	
	/**
	 * Count all relevant counter 
	 * @param time Normalized current time
	 * @return
	 */
	int count(long time) {
		long startPeriod = time - CAPACITY;
		int count=0;
		for (int i=0;i< times.length; i++) {
			count += times[i].getCounter(startPeriod);
		}
		return count;
	}
}
