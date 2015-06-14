package de.fenecon.fems.agent.scheduler;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;
import de.fenecon.fems.agent.consumption.ConsumptionAgent;
import de.fenecon.fems.agent.consumption.ConsumptionAgentImpl;
import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Prediction;
import de.fenecon.fems.helper.PredictionAgent;

public class SchedulerAgent {
	private final ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<PredictionAgent>> sourceAgents = new ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<PredictionAgent>>();
	private final ConcurrentSkipListSet<ConsumptionAgent> consumptionAgents = new ConcurrentSkipListSet<ConsumptionAgent>(); 
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public SchedulerAgent() {
		scheduler.scheduleAtFixedRate(worker, FemsConstants.POLLING_TIME_SECONDS/2, FemsConstants.POLLING_TIME_SECONDS, TimeUnit.SECONDS);
	}
	
	public void addSourceAgent(SourceAgent agent) {
		SourceCategory sourceCategory = agent.getSourceCategory();
		sourceAgents.putIfAbsent(sourceCategory, new ConcurrentSkipListSet<PredictionAgent>());
		ConcurrentSkipListSet<PredictionAgent> currentSourceAgents = sourceAgents.get(sourceCategory);
		currentSourceAgents.add(agent);
	}
	
	public void addConsumptionAgent(ConsumptionAgent agent) {
		consumptionAgents.add(agent);
	}
	
	private Runnable worker = new Runnable() {
		@Override
		public void run() {
			try {
				TreeMap<SourceCategory, TreeMap<Long, Prediction>> sourcePredictions = getSourcePredictions();
				System.out.println(sourcePredictions);
				TreeMap<ConsumptionField, TreeMap<Long, Prediction>> consumptionPredictions = getConsumptionPredictions();
				System.out.println(consumptionPredictions);
				// TODO: Create Schedule
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Poll all registered {@link SourceAgent}s for their predictions and sum them up per {@link SourceCategory}
		 * 
		 * @return
		 */
		private TreeMap<SourceCategory, TreeMap<Long, Prediction>> getSourcePredictions() {
			final TreeMap<SourceCategory, TreeMap<Long, Prediction>> predictions = new TreeMap<SourceCategory, TreeMap<Long, Prediction>>();
			long currentTimestamp = FemsTools.getCurrentRoundedUtcTimestamp();
			
			for(Map.Entry<SourceCategory, ConcurrentSkipListSet<PredictionAgent>> currentAgents : sourceAgents.entrySet()) {
				predictions.put(currentAgents.getKey(), 
						getPredictionsFromAgents(currentAgents.getValue(), currentTimestamp));
			}
			return predictions;
		}
		
		/**
		 * Poll all registered {@link ConsumptionAgentImpl}s for their predictions and sum them up per phase ({@link ConsumptionField})
		 * 
		 * @return
		 */
		private TreeMap<ConsumptionField, TreeMap<Long, Prediction>> getConsumptionPredictions() {
			final TreeMap<ConsumptionField, TreeMap<Long, Prediction>> predictions = new TreeMap<ConsumptionField, TreeMap<Long, Prediction>>();
			long currentTimestamp = FemsTools.getCurrentRoundedUtcTimestamp();
			
			for(ConsumptionAgent agent : consumptionAgents) {
				ConcurrentSkipListSet<PredictionAgent> oneAgentSet = new ConcurrentSkipListSet<PredictionAgent>();
				oneAgentSet.add(agent);
				predictions.put(agent.getField(), 
						getPredictionsFromAgents(oneAgentSet, currentTimestamp));
			}
			return predictions;
		}
		
		private TreeMap<Long, Prediction> getPredictionsFromAgents(ConcurrentSkipListSet<PredictionAgent> agents, long currentTimestamp) {
			TreeMap<Long, Prediction> currentPredictions = new TreeMap<Long, Prediction>();
			for(long timestamp = currentTimestamp; 
					timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS * FemsConstants.MAX_PREDICTION_WINDOW;
					timestamp += FemsConstants.SLICE_SECONDS) { // all possible prediction windows
				float sumLagWindowSize = 0f;
				double sumValue = 0.;
				for(PredictionAgent agent : agents) {
					Prediction prediction = agent.getBestPredictionAtTimestamp(timestamp);
					//System.out.println(timestamp + ": " + sourceAgent + ": " + prediction);
					if(prediction != null) {
						sumLagWindowSize += prediction.getLagWindowSize(); 
						sumValue += prediction.getValue();
					}
				}
				Prediction sumPrediction = new Prediction(sumValue, sumLagWindowSize / agents.size());
				currentPredictions.put(timestamp, sumPrediction);
			}
			return currentPredictions;
		}
	};
}
