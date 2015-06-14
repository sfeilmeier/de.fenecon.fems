package de.fenecon.fems.agent.source.pv;

import de.fenecon.fems.helper.PredictionAgentFactory;

public class PvAgentFactory extends PredictionAgentFactory {
	/**
	 * Creates a new, valid {@link PvAgent}
	 * 
	 * @param field identifier
	 * @return a new, valid {@link PvAgent}
	 * @throws Exception 
	 */
	public static PvAgent create(PvField field)  throws Exception {
		return new PvAgent(field, getPredictors(field));
	}
}
