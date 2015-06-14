package de.fenecon.fems.ess.prohybrid;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.ess.EssListener;
import de.fenecon.fems.helper.Field;

public class ProHybridSimulator {	
	private final ConcurrentSkipListSet<EssListener> listeners = new ConcurrentSkipListSet<EssListener>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final TreeMap<Long, HashMap<Field, Double>> cacheMap;
	private final ScheduledFuture<?> scheduledFuture;
	
	public ProHybridSimulator(TreeMap<Long, HashMap<Field, Double>> cacheMap) {
		this.cacheMap = cacheMap;
		scheduledFuture = scheduler.scheduleAtFixedRate(simulator, 0, FemsConstants.POLLING_TIME_SECONDS, TimeUnit.SECONDS);
	}
	
	public void addListener(EssListener listener) {
		listeners.add(listener);
	}
	
	private Runnable simulator = new Runnable() {
		@Override
		public void run() {
			try {
				Map.Entry<Long, HashMap<Field, Double>> entry = cacheMap.pollFirstEntry();
				long timestamp = entry.getKey();
				FemsConstants.CURRENT_TIMESTAMP = timestamp; // Simulation Timestamp
				for(EssListener listener : listeners) {
					Double value = entry.getValue().get(listener.getField());
					if(value != null) {
						listener.newValue(timestamp, value);
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
