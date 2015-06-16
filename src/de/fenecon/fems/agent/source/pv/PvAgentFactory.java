/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source.pv;

import de.fenecon.fems.helper.PredictionAgentFactory;

/**
 * Factory for {@link PvAgent}s.
 * 
 * @author Stefan Feilmeier
 */
public class PvAgentFactory extends PredictionAgentFactory {
	/**
	 * Creates a new, valid {@link PvAgent}
	 * 
	 * @param field
	 *            the field identifier of a photovoltaic installation
	 * @return the new {@link PvAgent}
	 * @throws Exception
	 *             if no valid {@link PvAgent} could be created.
	 */
	public static PvAgent create(PvField field) throws Exception {
		return new PvAgent(field, getPredictors(field));
	}
}
