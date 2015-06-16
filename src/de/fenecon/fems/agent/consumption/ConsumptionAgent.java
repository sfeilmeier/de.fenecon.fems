/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.consumption;

import de.fenecon.fems.ess.EssListener;
import de.fenecon.fems.helper.Field;
import de.fenecon.fems.helper.PredictionAgent;

/**
 * General agent for a consumption.
 * 
 * @author Stefan Feilmeier
 */
public interface ConsumptionAgent extends PredictionAgent, EssListener {

	/**
	 * Gets the {@link Field} of this consumption.
	 * 
	 * @return the Field of this consumption
	 */
	public ConsumptionField getField();
}
