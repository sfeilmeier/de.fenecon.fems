package de.fenecon.fems.ess.prohybrid;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.ess.PvListener;

public class ProHybridSimulator {	
	private final ConcurrentSkipListSet<PvListener> PvListeners = new ConcurrentSkipListSet<PvListener>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final LinkedList<String[]> historyCache;
	private final ScheduledFuture<?> scheduledFuture;
	
	public ProHybridSimulator(LinkedList<String[]> historyCache) {
		this.historyCache = historyCache;
		scheduledFuture = scheduler.scheduleAtFixedRate(simulator, 0, FemsConstants.POLLING_TIME_SECONDS, TimeUnit.SECONDS);
	}
	
	public void addPvListener(PvListener listener) {
		PvListeners.add(listener);
	}
	
	private Runnable simulator = new Runnable() {
		@Override
		public void run() {
			try {
				String[] line = historyCache.pop();
				long timestamp = Long.parseLong(line[0]);
				double value = Double.parseDouble(line[1]);
				FemsConstants.CURRENT_TIMESTAMP = timestamp; // only for simulator
				for(PvListener listener : PvListeners) {
					if(listener.getField() == FemsConstants.PV1) {
						listener.pvNotification(timestamp, value);
					} else if(listener.getField() == FemsConstants.PV2) {
						listener.pvNotification(timestamp, value);
					} 
				}
			} catch(NoSuchElementException e) {
				System.out.println("HistoryCache is empty: " + e.getMessage());
				scheduledFuture.cancel(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
