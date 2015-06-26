/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

/**
 * Defines a simulator for a FENECON by BYD PRO Hybrid Energy Storage System. It
 * reads the from an internal cache and sends a new event every few seconds, as
 * defined in {@link FemsConstants}.
 * 
 * @author Stefan Feilmeier
 */
public class ProHybridSimulator {
	/** the cached CSV file */
	private final TreeMap<Long, HashMap<Field, Double>> cacheMap;
	/** the list of {@link EssListener}s */
	private final ConcurrentSkipListSet<EssListener> listeners = new ConcurrentSkipListSet<EssListener>();

	private final ScheduledFuture<?> scheduledFuture;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 * Runnable that sends the next event.
	 */
	private Runnable simulator = new Runnable() {
		@Override
		public void run() {
			try {
				Map.Entry<Long, HashMap<Field, Double>> entry = cacheMap.pollFirstEntry();
				long timestamp = entry.getKey();
				// Simulation Timestamp
				FemsConstants.CURRENT_TIMESTAMP = timestamp; 
				for (EssListener listener : listeners) {
					Double value = entry.getValue().get(listener.getField());
					if (value != null) {
						listener.newValue(timestamp, value);
					}
				}
			} catch (NoSuchElementException e) {
				System.out.println("HistoryCache is empty: " + e.getMessage());
				scheduledFuture.cancel(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Creates a new Simulator with a defined cache. Use with
	 * {@link ProHybridSimulatorFactory}.
	 * 
	 * @param cacheMap
	 */
	public ProHybridSimulator(TreeMap<Long, HashMap<Field, Double>> cacheMap) {
		this.cacheMap = cacheMap;
		scheduledFuture = scheduler.scheduleAtFixedRate(simulator, 0, FemsConstants.POLLING_TIME_MILLISECONDS,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * Add a new listener.
	 * 
	 * @param listener
	 *            the listener object
	 */
	public void addListener(EssListener listener) {
		listeners.add(listener);
	}
}
