package de.fenecon.fems.ess;

import de.fenecon.fems.tools.Listener;
import de.fenecon.fems.types.PvField;

public abstract interface PvListener extends Listener {	
	PvField getField();
	
	public void pvNotification(long timestamp, double value);
}
