/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.ess;

import de.fenecon.fems.helper.Field;

/**
 * Defines a Listener for Energy Storage System (ESS) events.
 * 
 * @author Stefan Feilmeier
 */
public abstract interface EssListener {
	/**
	 * Gets the {@link Field} descriptor.
	 * 
	 * @return the field descriptor
	 */
	Field getField();

	/**
	 * Notifies this listener of a new value. Implement this method to react on
	 * notifications fom Energy Storage System (ESS).
	 * 
	 * @param timestamp
	 *            the timestamp of the value
	 * @param value
	 *            the value
	 */
	public void newValue(long timestamp, double value);
}
