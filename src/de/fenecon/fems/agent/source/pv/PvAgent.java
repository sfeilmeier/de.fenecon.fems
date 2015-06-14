package de.fenecon.fems.agent.source.pv;

import java.util.Set;

import de.fenecon.fems.agent.source.SourceAgent;
import de.fenecon.fems.agent.source.SourceCategory;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.PredictionAgentImpl;
import de.fenecon.fems.helper.Predictor;

public class PvAgent extends PredictionAgentImpl implements SourceAgent {

	private final PvField field;
	
	public PvAgent(PvField field, Set<Predictor> predictors) {
		super(predictors);
		this.field = field;
	}

	@Override
	public SourceCategory getSourceCategory() {
		return SourceCategory.PHOTOVOLTAICS;
	}

	@Override
	public Field getField() {
		return field;
	}
}
