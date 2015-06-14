package de.fenecon.fems;

import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.joda.time.DateTimeZone;

import de.fenecon.fems.agent.consumption.ConsumptionField;
import de.fenecon.fems.agent.source.grid.GridField;
import de.fenecon.fems.agent.source.pv.PvField;

public class FemsConstants {
	public final static DateTimeZone LOCAL_TIMEZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
	
	public final static int SLICE_SECONDS = 5*60; // length of one data slice
	
	public static final int MAX_PREDICTION_WINDOW = 2;
	
	public static final int POLLING_TIME_SECONDS = 1; // simulated polling time for date from storage system in seconds
	
	public final static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT;
	
	public final static String FILESPATH = "D:/fems/files";
	
	public final static PvField PV1 = new PvField("PV1", "PV1_Charger1_Output_Power");
	public final static PvField PV2 = new PvField("PV2", "PV2_Charger2_Output_Power"); // TODO change
	
	public final static GridField GRID_PHASE1 = new GridField("GRID_PHASE1", "PCS1_Grid_Phase1_Active_Power");
	public final static GridField GRID_PHASE2 = new GridField("GRID_PHASE2", "PCS2_Grid_Phase2_Active_Power");
	public final static GridField GRID_PHASE3 = new GridField("GRID_PHASE3", "PCS3_Grid_Phase3_Active_Power");
	
	public final static ConsumptionField CONSUMPTION_PHASE1 = new ConsumptionField("Ph1", "PCS1_Phase1_Load_Active_Power");
	public final static ConsumptionField CONSUMPTION_PHASE2 = new ConsumptionField("Ph2", "PCS2_Phase2_Load_Active_Power");
	public final static ConsumptionField CONSUMPTION_PHASE3 = new ConsumptionField("Ph3", "PCS3_Phase3_Load_Active_Power");
	
	public static volatile long CURRENT_TIMESTAMP = 0; // required for simulator
}
