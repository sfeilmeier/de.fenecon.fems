package de.fenecon.fems.types;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.csv.CSVPrinter;

import de.fenecon.fems.FemsConstants;

public class Predictions {
	private final ConcurrentSkipListSet<Prediction> predictions;
	
	public Predictions() {
		predictions = new ConcurrentSkipListSet<Prediction>();
	}
	
	public Predictions(Prediction prediction) {
		this();
		addPrediction(prediction);
	}
	
	public ConcurrentSkipListSet<Prediction> getPredictions() {
		return predictions;			
	}
	
	public void addPrediction(Prediction newPrediction) {
		predictions.add(newPrediction);
	}
	
	public Prediction getBestPrediction() {
		return predictions.first(); // use internal sorting of ConcurrentSkipListSet to return the most accurate prediction
	}
	
	@Override
	public synchronized String toString() {
		StringBuilder builder = new StringBuilder("Predictions [");
		String split = "";
		for(Prediction prediction : predictions) {
			builder.append(split);
			builder.append(prediction.getLagWindowSize() * FemsConstants.SLICE_SECONDS + ":");
			builder.append(String.format("%.2f", prediction.getValue()));
			split = ", ";
		}	
		builder.append("]");
		return builder.toString();
	}
	
	public synchronized String toStringWithRatio(double ideal) {
		StringBuilder builder = new StringBuilder("Predictions [");
		String split = "";
		for(Prediction prediction : predictions) {
			builder.append(split);
			builder.append(prediction.getLagWindowSize() * FemsConstants.SLICE_SECONDS + ":");
			builder.append(String.format("%.2f", prediction.getValue()/ideal));
			split = ", ";
		}	
		builder.append("]");
		return builder.toString();
	}
	
	public void writeToCsv(Double ideal) throws IOException {
		try(CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(Paths.get(FemsConstants.FILESPATH, "predictions.csv").toFile()), FemsConstants.CSV_FORMAT)) {
			csvFilePrinter.print(ideal);
			for(Prediction prediction : predictions) {
				csvFilePrinter.print(prediction.getValue());
			}	
			csvFilePrinter.println();
		}
			 
	}
}
