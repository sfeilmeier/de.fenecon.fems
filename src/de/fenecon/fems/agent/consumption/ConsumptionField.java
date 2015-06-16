/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.consumption;

import de.fenecon.fems.helper.Field;

/**
 * General definition of a field identifier for consumptions.
 * 
 * @author Stefan Feilmeier
 */
public class ConsumptionField extends Field {

	/**
	 * Creates a specific consumption identifier
	 * 
	 * @param name
	 *            the short name of this field
	 * @param technicalName
	 *            the technical name of this field
	 */
	public ConsumptionField(String name, String technicalName) {
		super(name, technicalName);
	}

}
