package cz.prochy.metrostation;

import android.provider.BaseColumns;

public final class DbContract {

	public static abstract class PlaceEntry implements BaseColumns {
		public static final String TABLE_NAME = "places";
		public static final String COLUMN_NAME_NAME = "name";

	    public static final String SQL_CREATE_ENTRIES =
	    		"CREATE TABLE " + PlaceEntry.TABLE_NAME +
	    		"(" + PlaceEntry._ID + " INTEGER PRIMARY KEY," +
	    		PlaceEntry.COLUMN_NAME_NAME + " TEXT NOT NULL)";
	}
	
	public static abstract class CellEntry implements BaseColumns {
		public static final String TABLE_NAME = "cells";
		public static final String COLUMN_NAME_PLACE_ID = "place_id";
		public static final String COLUMN_NAME_CELL_ID = "cell_id";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_SIGNAL_LEVEL = "signal_level";
		
		public static final int CELL_TYPE_GSM = 0;
		public static final int CELL_TYPE_CDMA = 1;
		public static final int CELL_TYPE_WCDMA = 2;
		public static final int CELL_TYPE_LTE = 3;
		
		public static final String SQL_CREATE_ENTRIES =
	    		"CREATE TABLE " + TABLE_NAME +
	    		"(" + _ID + " INTEGER PRIMARY KEY," +
	    		COLUMN_NAME_PLACE_ID + " INTEGER NOT NULL," +
	    		COLUMN_NAME_CELL_ID + " INTEGER NOT NULL, " +
	    		COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
	    		COLUMN_NAME_SIGNAL_LEVEL + " INTEGER NOT NULL, " +
	    		"FOREIGN KEY (" + COLUMN_NAME_PLACE_ID + ") REFERENCES " +
	    		PlaceEntry.TABLE_NAME + "(" + PlaceEntry._ID + "))";
				
		public static final String SQL_CREATE_INDEX = 
				"CREATE INDEX cells_idx ON " + TABLE_NAME + "(" + COLUMN_NAME_CELL_ID + "," +
				COLUMN_NAME_TYPE + ")";
		
	}
	
    		

	
}
