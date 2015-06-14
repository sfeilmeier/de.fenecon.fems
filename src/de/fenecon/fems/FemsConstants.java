package de.fenecon.fems;

import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.joda.time.DateTimeZone;

import de.fenecon.fems.types.GridField;
import de.fenecon.fems.types.PvField;

public class FemsConstants {
	public final static DateTimeZone LOCAL_TIMEZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
	
	public final static int SLICE_SECONDS = 5*60; // length of one data slice
	
	public static final int MAX_PREDICTION_WINDOW = 2;
	
	public static final int POLLING_TIME_SECONDS = 1; // simulated polling time for date from storage system in seconds
	
	public final static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT;
	
	public final static String FILESPATH = "D:/fems/files";
	
	public final static PvField PV1 = new PvField("PV1", "PV1_Charger1_Output_Power");
	public final static PvField PV2 = new PvField("PV2", "PV1_Charger1_Output_Power"); // TODO change
	
	public final static GridField GRID_PHASE1 = new GridField("GRID_PHASE1", "");
	public final static GridField GRID_PHASE2 = new GridField("GRID_PHASE2", "");
	public final static GridField GRID_PHASE3 = new GridField("GRID_PHASE3", "");
	
	public static volatile long CURRENT_TIMESTAMP = 0; // required for simulator
}
