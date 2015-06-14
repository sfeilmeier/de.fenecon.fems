package de.fenecon.fems.tools;

/**
 * Message with new Listener
 */
public class AddListenerMessage extends Message {
	private final Listener listener;
	
	public AddListenerMessage(Listener listener) {
		this.listener = listener;
	}
	
	public Listener getListener() {
		return listener;
	}
}
