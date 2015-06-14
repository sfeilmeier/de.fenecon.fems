package de.fenecon.fems.ess;

import de.fenecon.fems.helper.Field;

public abstract interface EssListener {	
	Field getField();
	
	public void newValue(long timestamp, double value);
}
