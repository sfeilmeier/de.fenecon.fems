/**
 * Copyright (c) 2015 Stefan Feilmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fenecon.fems;

import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.joda.time.DateTimeZone;

import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.source.grid.GridField;
import de.fenecon.fems.agent.source.pv.PvField;
import de.fenecon.fems.ess.prohybrid.ProHybridSimulator;

/**
 * Holds important Constants that are used throughout the application.
 * 
 * @author Stefan Feilmeier
 */
public class FemsConstants {
	/** The local timezone of the device. */
	public final static DateTimeZone LOCAL_TIMEZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

	/** The length of one data slice (= prediction timespan). */
	public final static int SLICE_SECONDS = 5 * 60;

	/**
	 * The maximum number of predictions. (This value multiplied with the
	 * SLICE_SECONDS is the total prediction timeframe in seconds.)
	 */
	public static final int MAX_PREDICTION_WINDOW = 12;

	/**
	 * The simulated polling time for data from the storage system in seconds.
	 * In a real application this is equal to SLICE_SECONDS
	 */
	public static final int POLLING_TIME_MILLISECONDS = 100; //

	/** The format for all CSV-files */
	public final static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT;
	
	/** The name of the FEMS device to use */
	public final static String FEMS_NAME = "fems20";
	
	/** The URL of the InfluxDB api endpoint */
	public final static String INFLUXDB_URL = "http://fenecon.de:8086";

	/** The base path for external files */
	public final static String FILESPATH = "D:/fems/files";

	/** The photovoltaic installation on first MPP tracker */
	public final static PvField PV1 = new PvField("PV1", "PV1_Charger1_Output_Power");
	/** The photovoltaic installation on second MPP tracker */
	public final static PvField PV2 = new PvField("PV2", "PV2_Charger2_Output_Power");

	/** The power grid connection on first phase */
	public final static GridField GRID_PHASE1 = new GridField("GRID_PHASE1", "PCS1_Grid_Phase1_Active_Power");
	/** The power grid connection on second phase */
	public final static GridField GRID_PHASE2 = new GridField("GRID_PHASE2", "PCS2_Grid_Phase2_Active_Power");
	/** The power grid connection on third phase */
	public final static GridField GRID_PHASE3 = new GridField("GRID_PHASE3", "PCS3_Grid_Phase3_Active_Power");

	/** The power consumption on first phase */
	public final static ConsumptionField CONSUMPTION_PHASE1 = new ConsumptionField("Ph1",
			"PCS1_Phase1_Load_Active_Power");
	/** The power consumption on second phase */
	public final static ConsumptionField CONSUMPTION_PHASE2 = new ConsumptionField("Ph2",
			"PCS2_Phase2_Load_Active_Power");
	/** The power consumption on third phase */
	public final static ConsumptionField CONSUMPTION_PHASE3 = new ConsumptionField("Ph3",
			"PCS3_Phase3_Load_Active_Power");

	/**
	 * The current timestamp: required for simulation and set by
	 * {@link ProHybridSimulator}
	 */
	public static volatile long CURRENT_TIMESTAMP = 0;
	
	//TODO: public final static SIMULATION_START_TIMESTAMP = 0;
}
