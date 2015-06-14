package de.fenecon.fems.agent.source;

import de.fenecon.fems.types.Prediction;
import de.fenecon.fems.types.SourceCategory;

public interface SourceAgent extends Comparable<SourceAgent> {
	Prediction getBestPredictionAtTimestamp(Long timestamp);
	
	SourceCategory getSourceCategory();
}
