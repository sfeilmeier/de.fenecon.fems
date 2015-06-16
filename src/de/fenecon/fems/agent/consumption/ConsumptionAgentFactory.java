/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.consumption;

import de.fenecon.fems.helper.PredictionAgentFactory;

/**
 * Factory for {@link ConsumptionAgent}s.
 * 
 * @author Stefan Feilmeier
 */
public class ConsumptionAgentFactory extends PredictionAgentFactory {

	/**
	 * Creates a new, valid {@link ConsumptionAgent}.
	 * 
	 * @param field
	 *            the field identifier of a consumption
	 * @return the new {@link ConsumptionAgent}
	 * @throws Exception
	 *             if no valid {@link ConsumptionAgent} could be created.
	 */
	public static ConsumptionAgent create(ConsumptionField field) throws Exception {
		return new ConsumptionAgentImpl(field, getPredictors(field));
	}
}
