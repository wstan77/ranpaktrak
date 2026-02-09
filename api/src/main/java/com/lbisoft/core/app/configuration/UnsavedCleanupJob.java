package com.lbisoft.core.app.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public abstract class UnsavedCleanupJob {
	public abstract void cleanup(List<Integer> ids);
	public abstract List<Integer> getAssetIdsToDelete();
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	/**
	 * Run the cleanup job every day at 4am. 
	 * Get a list of asset ids to delete and
	 * then perform the cleanup
	 */
	@Scheduled(cron = "0 0 4 * * *")
	public void cleanupJob() {
		try {
			List<Integer> ids = getAssetIdsToDelete();	
			cleanup(ids);
		} catch (Exception e) {
			LOGGER.error("Caught exception in unsaved cleanup job", e);
		}
	}
}
