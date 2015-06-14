package de.fenecon.fems.agent.source.grid;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.Prediction;
import de.fenecon.fems.helper.PredictionAgent;

public class GridAgent implements SourceAgent {
	private final GridField field;
	
	public GridAgent(GridField field) {
		this.field = field;
	}

	@Override
	public Prediction getBestPredictionAtTimestamp(Long timestamp) {
		return new Prediction(Double.MAX_VALUE, 0);
	}

	@Override
	public SourceCategory getSourceCategory() {
		return SourceCategory.POWER_GRID;
	}

	@Override
	public String toString() {
		return field.getName();
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public int compareTo(PredictionAgent o) {
		return this.field.compareTo(o.getField());
	}
}
