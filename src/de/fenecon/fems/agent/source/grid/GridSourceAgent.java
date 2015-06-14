package de.fenecon.fems.agent.source.grid;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.types.GridField;
import de.fenecon.fems.types.Prediction;
import de.fenecon.fems.types.SourceCategory;

public class GridSourceAgent implements SourceAgent {
	private final GridField field;
	
	public GridSourceAgent(GridField field) {
		this.field = field;
	}
	
	@Override
	public int compareTo(SourceAgent o) {
		if(o instanceof GridSourceAgent) {
			return field.toString().compareTo(((GridSourceAgent)o).field.toString());
		} else {
			return getSourceCategory().compareTo(o.getSourceCategory());
		}
	}

	@Override
	public Prediction getBestPredictionAtTimestamp(Long timestamp) {
		return new Prediction(Double.MAX_VALUE, 0);
	}

	@Override
	public SourceCategory getSourceCategory() {
		return SourceCategory.POWER_GRID;
	}

}
