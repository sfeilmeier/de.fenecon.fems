package de.fenecon.fems.agent.consumption;

import de.fenecon.fems.helper.PredictionAgentFactory;

public class ConsumptionAgentFactory extends PredictionAgentFactory {
	/**
	 * Creates a new, valid {@link ConsumptionAgentImpl}
	 * 
	 * @param field identifier
	 * @return a new, valid {@link ConsumptionAgentImpl}
	 * @throws Exception 
	 */
	public static ConsumptionAgentImpl create(ConsumptionField field)  throws Exception {
		return new ConsumptionAgentImpl(field, getPredictors(field));
	}
}
