package de.fenecon.fems.agent.consumption;

import java.util.Set;

import de.fenecon.fems.helper.PredictionAgentImpl;
import de.fenecon.fems.helper.Predictor;

public class ConsumptionAgentImpl extends PredictionAgentImpl implements ConsumptionAgent {

	protected final ConsumptionField field;
	
	public ConsumptionAgentImpl(ConsumptionField field, Set<Predictor> predictors) {
		super(predictors);
		this.field = field;
	}

	@Override
	public ConsumptionField getField() {
		return field;
	}
	
}
