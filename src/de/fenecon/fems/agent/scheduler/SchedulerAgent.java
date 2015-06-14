package de.fenecon.fems.agent.scheduler;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;
import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.types.Prediction;
import de.fenecon.fems.types.SourceCategory;

public class SchedulerAgent {
	private final ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<SourceAgent>> sourceAgents = new ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<SourceAgent>>(); 
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final ScheduledFuture<?> scheduledFuture;
	
	public SchedulerAgent() {
		scheduledFuture = scheduler.scheduleAtFixedRate(worker, FemsConstants.POLLING_TIME_SECONDS/2, FemsConstants.POLLING_TIME_SECONDS, TimeUnit.SECONDS);
	}
	
	public void addSourceAgent(SourceAgent sourceAgent) {
		SourceCategory sourceCategory = sourceAgent.getSourceCategory();
		sourceAgents.putIfAbsent(sourceCategory, new ConcurrentSkipListSet<SourceAgent>());
		ConcurrentSkipListSet<SourceAgent> currentSourceAgents = sourceAgents.get(sourceCategory);
		currentSourceAgents.add(sourceAgent);
	}
	
	/**
	 * Poll all registered {@link SourceAgent}s for their predictions and forward the result to a SchedulerAgent
	 */
	private Runnable worker = new Runnable() {
		@Override
		public void run() {
			try {
				TreeMap<SourceCategory, TreeMap<Long, Prediction>> sourcePredictions = getSourcePredictions();
				System.out.println(sourcePredictions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private TreeMap<SourceCategory, TreeMap<Long, Prediction>> getSourcePredictions() {
			final TreeMap<SourceCategory, TreeMap<Long, Prediction>> sourcePredictions = new TreeMap<SourceCategory, TreeMap<Long, Prediction>>();
			long currentTimestamp = FemsTools.getCurrentRoundedUtcTimestamp();
			
			for(Map.Entry<SourceCategory, ConcurrentSkipListSet<SourceAgent>> currentSourceAgents : sourceAgents.entrySet()) {
				TreeMap<Long, Prediction> thisSourcePriorityPredictions = new TreeMap<Long, Prediction>();
				for(long timestamp = currentTimestamp; 
						timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS * FemsConstants.MAX_PREDICTION_WINDOW;
						timestamp += FemsConstants.SLICE_SECONDS) { // all possible prediction windows
					float sumLagWindowSize = 0f;
					double sumValue = 0.;
					for(SourceAgent sourceAgent : currentSourceAgents.getValue()) {
						Prediction prediction = sourceAgent.getBestPredictionAtTimestamp(timestamp);
						//System.out.println(timestamp + ": " + sourceAgent + ": " + prediction);
						if(prediction != null) {
							sumLagWindowSize += prediction.getLagWindowSize(); 
							sumValue += prediction.getValue();
						}
					}
					Prediction sumPrediction = new Prediction(sumValue, sumLagWindowSize / currentSourceAgents.getValue().size());
					thisSourcePriorityPredictions.put(timestamp, sumPrediction);
				}
				sourcePredictions.put(currentSourceAgents.getKey(), thisSourcePriorityPredictions);
			}
			return sourcePredictions;
		}
	};
}
