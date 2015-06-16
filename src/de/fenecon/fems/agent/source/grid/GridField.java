/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.source.grid;

import de.fenecon.fems.helper.Field;

/**
 * General definition of a field identifier for power grid connection.
 * 
 * @author Stefan Feilmeier
 */
public class GridField extends Field {

	/**
	 * Creates a specific power grid identifier
	 * 
	 * @param name
	 *            the short name of this field
	 * @param technicalName
	 *            the technical name of this field
	 */
	public GridField(String name, String technicalName) {
		super(name, technicalName);
	}

}
