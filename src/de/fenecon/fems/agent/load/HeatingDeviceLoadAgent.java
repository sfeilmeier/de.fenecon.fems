/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.agent.load;

import java.util.LinkedList;
import java.util.List;

import de.fenecon.fems.FemsConstants;
import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.source.SourceCategory;

/**
 * Defines a {@link LoadAgent} for a simple heating device, generally used as a
 * simple way to heat water in a central water tank.
 * 
 * @author Stefan Feilmeier
 */
public class HeatingDeviceLoadAgent extends LoadAgent {

	/**
	 * Always requires at least PV priority.
	 */
	@Override
	public float getAddedValue(long timestamp) {
		return SourceCategory.PHOTOVOLTAICS.ordinal();
	}

	/**
	 * This device is connected to phase 1.
	 */
	@Override
	public List<ConsumptionField> getConsumptionFields() {
		LinkedList<ConsumptionField> fields = new LinkedList<ConsumptionField>();
		fields.add(FemsConstants.CONSUMPTION_PHASE1);
		return fields;
	}

	/**
	 * Starts when at least 500 W are available.
	 */
	@Override
	public double getRequiredPower(long timestamp) {
		return 500;
	}

}
