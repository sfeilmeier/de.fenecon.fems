/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.scheduler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.FemsTools;
import de.fenecon.fems.agent.consumption.ConsumptionAgent;
import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.load.LoadAction;
import de.fenecon.fems.agent.load.LoadAgent;
import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.Prediction;
import de.fenecon.fems.helper.PredictionAgent;

/**
 * Creates a schedule for {@link LoadAgent}s.
 * 
 * The {@link SchedulerAgent} collects the predictions from all registered
 * {@link SourceAgent}s and {@link ConsumptionAgent}s, creates a plan with the
 * predicted available power per timestamp and uses this informaton to schedule
 * the registered {@link LoadAgent}s.
 * 
 * @author Stefan Feilmeier
 */
public class SchedulerAgent {
	/** The list of registered {@link ConsumptionAgent}s */
	private final ConcurrentSkipListSet<ConsumptionAgent> consumptionAgents = new ConcurrentSkipListSet<ConsumptionAgent>();
	/** The list of registered {@link LoadAgent}s */
	private final ConcurrentSkipListSet<LoadAgent> loadAgents = new ConcurrentSkipListSet<LoadAgent>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/** The list of registered {@link SourceAgent}s per {@link SourceCategory} */
	private final ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<PredictionAgent>> sourceAgents = new ConcurrentSkipListMap<SourceCategory, ConcurrentSkipListSet<PredictionAgent>>();

	/**
	 * Worker runnable for SchedulerAgent
	 */
	private Runnable worker = new Runnable() {
		/**
		 * Creates the schedule for {@link LoadAgent}s.
		 * 
		 * @param powerPredictions
		 *            the powerPredictions (Use
		 *            {@link SchedulerAgent#getPowerPredictions}
		 * @param currentTimestamp
		 *            the current timestamp
		 * @return the schedule per timestamp and {@link LoadAgent}
		 */
		private TreeMap<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>> createSchedule(
				TreeMap<Long, TreeMap<SourceCategory, Double[]>> powerPredictions, long currentTimestamp) {
			TreeMap<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>> schedule = new TreeMap<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>>();
			for (long timestamp = currentTimestamp; timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS
					* FemsConstants.MAX_PREDICTION_WINDOW; timestamp += FemsConstants.SLICE_SECONDS) {
				schedule.putIfAbsent(timestamp, new ConcurrentSkipListMap<LoadAgent, LoadAction>());
				ConcurrentSkipListMap<LoadAgent, LoadAction> schedulePerTimestamp = schedule.get(timestamp);
				TreeMap<SourceCategory, Double[]> powerPerTimestamp = powerPredictions.get(timestamp);
				LoadAction loadAction = LoadAction.STOP;

				for (LoadAgent loadAgent : loadAgents) {
					double remainingPower = 0.;
					float addedValue = loadAgent.getAddedValue(timestamp);
					for (Map.Entry<SourceCategory, Double[]> powerPerCategory : powerPerTimestamp.entrySet()) {
						if (addedValue >= powerPerCategory.getKey().ordinal()) {
							remainingPower = powerPerCategory.getValue()[1];
						} else {
							break;
						}
					}
					double requiredPower = loadAgent.getRequiredPower(timestamp);
					if (remainingPower > requiredPower) {
						// very basic scheduling!
						loadAction = LoadAction.START;
						remainingPower -= requiredPower;
					}
					schedulePerTimestamp.put(loadAgent, loadAction);
				}

			}

			return schedule;
		}

		/**
		 * Poll all registered {@link ConsumptionAgentl}s for their predictions
		 * and sum them up per {@link ConsumptionField}.
		 * 
		 * @param currentTimestamp
		 *            the current timestamp
		 * @return the list of consumptionPredictions
		 */
		private TreeMap<Long, TreeMap<ConsumptionField, Prediction>> getConsumptionPredictions(long currentTimestamp) {
			final TreeMap<Long, TreeMap<ConsumptionField, Prediction>> predictions = new TreeMap<Long, TreeMap<ConsumptionField, Prediction>>();
			for (long timestamp = currentTimestamp; timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS
					* FemsConstants.MAX_PREDICTION_WINDOW; timestamp += FemsConstants.SLICE_SECONDS) {
				TreeMap<ConsumptionField, Prediction> predictionsPerTimestamp = new TreeMap<ConsumptionField, Prediction>();

				for (PredictionAgent agent : consumptionAgents) {
					Prediction prediction = agent.getBestPredictionAtTimestamp(timestamp); // Get
					Field field = agent.getField();
					if (prediction != null && field instanceof ConsumptionField) {
						predictionsPerTimestamp.put((ConsumptionField) field, prediction);
					}
				}

				predictions.put(timestamp, predictionsPerTimestamp);
			}

			return predictions;
		}

		/**
		 * Creates a plan with the predicted available power per timestamp.
		 * 
		 * @param sourcePredictions
		 *            the list of sourcePredictions
		 * @param consumptionPredictions
		 *            the list of consumptionPredictions
		 * @param currentTimestamp
		 *            the current timestamp
		 * @return the plan with predicted available power per timestamp and
		 *         {@link SourceCategory}
		 */
		private TreeMap<Long, TreeMap<SourceCategory, Double[]>> getPowerPredictions(
				TreeMap<Long, TreeMap<SourceCategory, Prediction>> sourcePredictions,
				TreeMap<Long, TreeMap<ConsumptionField, Prediction>> consumptionPredictions, long currentTimestamp) {
			TreeMap<Long, TreeMap<SourceCategory, Double[]>> powerPredictions = new TreeMap<Long, TreeMap<SourceCategory, Double[]>>();
			for (long timestamp = currentTimestamp; timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS
					* FemsConstants.MAX_PREDICTION_WINDOW; timestamp += FemsConstants.SLICE_SECONDS) {
				double totalConsumptionPerTimestamp = 0.;
				TreeMap<ConsumptionField, Prediction> consumptionPredictionsPerTimestamp = consumptionPredictions
						.get(timestamp);

				for (Map.Entry<ConsumptionField, Prediction> consumptionPredictionPerCategory : consumptionPredictionsPerTimestamp
						.entrySet()) {
					totalConsumptionPerTimestamp += consumptionPredictionPerCategory.getValue().getValue();
				}

				double remainingConsumptionPerTimestamp = totalConsumptionPerTimestamp;
				TreeMap<SourceCategory, Prediction> sourcePredictionsPerTimestamp = sourcePredictions.get(timestamp);
				TreeMap<SourceCategory, Double[]> powerPerTimestamp = new TreeMap<SourceCategory, Double[]>();

				for (Map.Entry<SourceCategory, Prediction> sourcePredictionPerCategory : sourcePredictionsPerTimestamp
						.entrySet()) {
					final Double[] powerPerCategory = new Double[2];
					// [0]: total power; [1]: available power
					powerPerCategory[0] = sourcePredictionPerCategory.getValue().getValue();
					if (remainingConsumptionPerTimestamp > powerPerCategory[0]) {
						powerPerCategory[1] = 0.;
						remainingConsumptionPerTimestamp -= powerPerCategory[0];
					} else if (remainingConsumptionPerTimestamp <= powerPerCategory[0]) {
						remainingConsumptionPerTimestamp = 0;
						powerPerCategory[1] = powerPerCategory[0] - remainingConsumptionPerTimestamp;
					}
					powerPerTimestamp.put(sourcePredictionPerCategory.getKey(), powerPerCategory);
				}

				powerPredictions.put(timestamp, powerPerTimestamp);
			}

			return powerPredictions;
		}

		/**
		 * Poll all registered {@link SourceAgent}s for their predictions and
		 * sum them up per {@link SourceCategory}.
		 * 
		 * @param currentTimestamp
		 *            the current timestamp
		 * @return the list of sourcePredictions
		 */
		private TreeMap<Long, TreeMap<SourceCategory, Prediction>> getSourcePredictions(long currentTimestamp) {
			final TreeMap<Long, TreeMap<SourceCategory, Prediction>> predictions = new TreeMap<Long, TreeMap<SourceCategory, Prediction>>();
			for (long timestamp = currentTimestamp; timestamp <= currentTimestamp + FemsConstants.SLICE_SECONDS
					* FemsConstants.MAX_PREDICTION_WINDOW; timestamp += FemsConstants.SLICE_SECONDS) {
				TreeMap<SourceCategory, Prediction> predictionsPerTimestamp = new TreeMap<SourceCategory, Prediction>();

				for (Map.Entry<SourceCategory, ConcurrentSkipListSet<PredictionAgent>> categoryAgents : sourceAgents
						.entrySet()) {
					float sumLagWindowSize = 0f;
					double sumValue = 0.;
					for (PredictionAgent agent : categoryAgents.getValue()) {
						Prediction prediction = agent.getBestPredictionAtTimestamp(timestamp);
						if (prediction != null) {
							sumLagWindowSize += prediction.getLagWindowSize();
							sumValue += prediction.getValue();
						}
					}
					predictionsPerTimestamp.put(categoryAgents.getKey(), new Prediction(sumValue, sumLagWindowSize
							/ categoryAgents.getValue().size()));
				}

				predictions.put(timestamp, predictionsPerTimestamp);
			}

			return predictions;
		}

		/**
		 * Pretty prints the consumptionPredictions to standard output.
		 * 
		 * @param consumptionPredictions
		 *            the consumptionPredictions
		 * @param currentTimestamp
		 *            the current timestamp
		 */
		private void printConsumptionPredictions(
				TreeMap<Long, TreeMap<ConsumptionField, Prediction>> consumptionPredictions, long currentTimestamp) {
			System.out.println("ConsumptionPredictions at " + FemsTools.timestampToString(currentTimestamp));
			for (Entry<Long, TreeMap<ConsumptionField, Prediction>> consumptionPerTimestamp : consumptionPredictions
					.entrySet()) {
				StringBuilder line = new StringBuilder();
				line.append("@");
				line.append(FemsTools.timestampToString(consumptionPerTimestamp.getKey()));
				line.append(": ");
				String split = "";
				for (Entry<ConsumptionField, Prediction> consumptionPerCategory : consumptionPerTimestamp.getValue()
						.entrySet()) {
					line.append(split);
					line.append("{");
					line.append(consumptionPerCategory.getKey());
					line.append(": ");
					line.append(consumptionPerCategory.getValue());
					line.append("}");
					split = "; ";
				}
				System.out.println(line.toString());
			}
		}

		/**
		 * Pretty prints the powerPredictions to standard output.
		 * 
		 * @param powerPredictions
		 *            the powerPredictions
		 * @param currentTimestamp
		 *            the current timestamp
		 */
		private void printPowerPredictions(TreeMap<Long, TreeMap<SourceCategory, Double[]>> powerPredictions,
				long currentTimestamp) {
			System.out.println("PowerPredictions at " + FemsTools.timestampToString(currentTimestamp));
			for (Entry<Long, TreeMap<SourceCategory, Double[]>> powerPerTimestamp : powerPredictions.entrySet()) {
				StringBuilder line = new StringBuilder();
				line.append("@");
				line.append(FemsTools.timestampToString(powerPerTimestamp.getKey()));
				line.append(": ");
				String split = "";
				for (Entry<SourceCategory, Double[]> powerPerCategory : powerPerTimestamp.getValue().entrySet()) {
					line.append(split);
					line.append("{");
					line.append(powerPerCategory.getKey());
					line.append(": ");
					line.append(String.format("Total: %.2f, ", powerPerCategory.getValue()[0]));
					line.append(String.format("Available: %.2f", powerPerCategory.getValue()[1]));
					line.append("}");
					split = "; ";
				}
				System.out.println(line.toString());
			}
		}

		/**
		 * Pretty prints the schedule to standard output.
		 * 
		 * @param schedule
		 *            the schedule
		 * @param currentTimestamp
		 *            the current timestamp
		 */
		private void printSchedule(TreeMap<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>> schedule,
				long currentTimestamp) {
			System.out.println("Schedule at " + FemsTools.timestampToString(currentTimestamp));
			for (Map.Entry<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>> schedulePerTimestamp : schedule
					.entrySet()) {
				StringBuilder line = new StringBuilder();
				line.append("@");
				line.append(FemsTools.timestampToString(schedulePerTimestamp.getKey()));
				line.append(": ");
				String split = "";
				for (Entry<LoadAgent, LoadAction> schedulePerLoadAgent : schedulePerTimestamp.getValue().entrySet()) {
					line.append(split);
					line.append("{");
					line.append(schedulePerLoadAgent.getKey());
					line.append(": ");
					line.append(schedulePerLoadAgent.getValue());
					line.append("}");
					split = "; ";
				}
				System.out.println(line.toString());
			}
		}

		/**
		 * Pretty prints the sourcePredictions to standard output.
		 * 
		 * @param sourcePredictions
		 *            the sourcePredictions
		 * @param currentTimestamp
		 *            the currentTimestamp
		 */
		private void printSourcePredictions(TreeMap<Long, TreeMap<SourceCategory, Prediction>> sourcePredictions,
				long currentTimestamp) {
			System.out.println("SourcePredictions at " + FemsTools.timestampToString(currentTimestamp));
			for (Entry<Long, TreeMap<SourceCategory, Prediction>> sourcePerTimestamp : sourcePredictions.entrySet()) {
				StringBuilder line = new StringBuilder();
				line.append("@");
				line.append(FemsTools.timestampToString(sourcePerTimestamp.getKey()));
				line.append(": ");
				String split = "";
				for (Entry<SourceCategory, Prediction> sourcePerCategory : sourcePerTimestamp.getValue().entrySet()) {
					line.append(split);
					line.append("{");
					line.append(sourcePerCategory.getKey());
					line.append(": ");
					line.append(sourcePerCategory.getValue());
					line.append("}");
					split = "; ";
				}
				System.out.println(line.toString());
			}
		}

		@Override
		public void run() {
			try {
				// Set the current timestamp for simulation purposes
				long currentTimestamp = FemsTools.getCurrentRoundedUtcTimestamp();

				// Get the sourcePredictions from registered SourceAgents
				TreeMap<Long, TreeMap<SourceCategory, Prediction>> sourcePredictions = getSourcePredictions(currentTimestamp);
				/* printSourcePredictions(sourcePredictions, currentTimestamp); */

				// Get the consumptionPredictions from registered
				// ConsumptionAgents
				TreeMap<Long, TreeMap<ConsumptionField, Prediction>> consumptionPredictions = getConsumptionPredictions(currentTimestamp);
				// printConsumptionPredictions(consumptionPredictions,
				// currentTimestamp);

				// Create a plan with the predicted available power per
				// timestamp
				TreeMap<Long, TreeMap<SourceCategory, Double[]>> powerPredictions = getPowerPredictions(
						sourcePredictions, consumptionPredictions, currentTimestamp);
				// printPowerPredictions(powerPredictions, currentTimestamp);

				// Create the load schedule
				TreeMap<Long, ConcurrentSkipListMap<LoadAgent, LoadAction>> schedule = createSchedule(powerPredictions,
						currentTimestamp);
				// printSchedule(schedule, currentTimestamp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Creates a SchedulerAgent and starts its worker runnable.
	 */
	public SchedulerAgent() {
		scheduler.scheduleAtFixedRate(worker, FemsConstants.POLLING_TIME_SECONDS / 2,
				FemsConstants.POLLING_TIME_SECONDS, TimeUnit.SECONDS);
	}

	/**
	 * Adds a new {@link ConsumptionAgent}.
	 * 
	 * @param agent
	 *            the ConsumptionAgent to add
	 */
	public void addConsumptionAgent(ConsumptionAgent agent) {
		if (agent == null)
			return;
		consumptionAgents.add(agent);
	}

	/**
	 * Adds a new {@link LoadAgent}.
	 * 
	 * @param agent
	 *            the LoadAgent to add
	 */
	public void addLoadAgent(LoadAgent agent) {
		loadAgents.add(agent);
	}

	/**
	 * Adds a new {@link SourceAgent}.
	 * 
	 * @param agent
	 *            the SourceAgent to add
	 */
	public void addSourceAgent(SourceAgent agent) {
		if (agent == null)
			return;
		SourceCategory sourceCategory = agent.getSourceCategory();
		sourceAgents.putIfAbsent(sourceCategory, new ConcurrentSkipListSet<PredictionAgent>());
		ConcurrentSkipListSet<PredictionAgent> currentSourceAgents = sourceAgents.get(sourceCategory);
		currentSourceAgents.add(agent);
	}
}
