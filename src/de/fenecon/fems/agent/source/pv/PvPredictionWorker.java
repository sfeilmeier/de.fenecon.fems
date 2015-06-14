package de.fenecon.fems.agent.source.pv;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.types.Prediction;

public class PvPredictionWorker implements Runnable {
	private final PvPredictor predictor;
	private final long currentTimestamp;
	private final double value;
	private final PvAgent callback;
	
	public PvPredictionWorker(PvPredictor predictor, long currentTimestamp, double value, PvAgent callback) {
		this.predictor = predictor;
		this.currentTimestamp = currentTimestamp;
		this.value = value;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			Prediction prediction = predictor.addValueAndPredict(value);
			long newTimestamp = currentTimestamp + predictor.getLagWindowSize() * FemsConstants.SLICE_SECONDS;
			callback.addPrediction(newTimestamp, prediction);
		} catch (Exception e) {
			System.out.println("Exception. Worker not successful: " + e.getMessage());
		}
		
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

	}
}
