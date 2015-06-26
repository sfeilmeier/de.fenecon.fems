/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems.helper;

/**
 * A fixed size array that implements a ring cache.
 * 
 * Part of the Source: http://stackoverflow.com/questions/13157675/looking-for-a-circular-fixed-size-array-based-deque
 * 
 * @author Stefan Feilmeier
 */
public class RingCache {
	private final int size;
	private final Double[] data;
    private int n = 0;

    /**
     * Creates a new RingCache with the defined size
     * 
     * @param size  the size of the cache
     */
    public RingCache(int size) {
    	this.size = size;
        data = new Double[size];
    }

    /**
     * Pushes a value to the end of the array
     * 
     * @param value  the new value
     */
    public void push(Double value) {
        data[n] = value;
        n = (n + 1) % data.length;
    }

    public void shift(Double value) {
        data[n = (n - 1) % data.length] = value;
    }

    public Double get(int index) {
        return data[(n + index) % data.length];
    }
    
    public int getSize() {
		return size;
	}
    
    public boolean isWindowReady() {
    	return get(0) != null;
    }
    
    public final double[] getWindow() {
    	double[] window = new double[size];
    	for(int i=0; i<size; i++) {
    		window[i] = get(i).doubleValue();
    	}
    	return window;
    }
}
