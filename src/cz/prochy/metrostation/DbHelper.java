package cz.prochy.metrostation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.CellInfo;

public class DbHelper extends SQLiteOpenHelper {

	    public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "MetroCells.db";
	   
	    public DbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(DbContract.PlaceEntry.SQL_CREATE_ENTRIES);
	        db.execSQL(DbContract.CellEntry.SQL_CREATE_ENTRIES);
	        db.execSQL(DbContract.CellEntry.SQL_CREATE_INDEX);
	    }
	    
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	// nop now
	        onCreate(db);
	    }
	    
	    private long insertPlace(SQLiteDatabase db, String place) {
	    	ContentValues contentValues = new ContentValues();
	    	contentValues.put(DbContract.PlaceEntry.COLUMN_NAME_NAME, place);
	    	return db.insert(DbContract.PlaceEntry.TABLE_NAME, null, contentValues);	    	
	    }
	    
	    public void addPlace(String place, ProcessedCellInfo [] cells) {
	    	SQLiteDatabase db = getWritableDatabase();
	    	db.beginTransaction();
	    	try {
		    	long placeId = insertPlace(db, place);
		    	
		    	for (ProcessedCellInfo cellInfo : cells) {
			    	ContentValues contentValues = new ContentValues();
			    	contentValues.put(DbContract.CellEntry.COLUMN_NAME_PLACE_ID, placeId);
			    	contentValues.put(DbContract.CellEntry.COLUMN_NAME_CELL_ID, cellInfo.getCellId());
			    	contentValues.put(DbContract.CellEntry.COLUMN_NAME_TYPE, cellInfo.getType());
			    	contentValues.put(DbContract.CellEntry.COLUMN_NAME_SIGNAL_LEVEL, cellInfo.getSignalLevel());
			    	db.insert(DbContract.CellEntry.TABLE_NAME, null, contentValues);	    	
		    	}
		    	
		    	db.setTransactionSuccessful();
	    	} finally {
	    		db.endTransaction();
	    	}
	    }
	    	
	    private final static String QUERY_WHERE_COLUMNS =
	    	DbContract.CellEntry.COLUMN_NAME_CELL_ID + " = ? AND " +
	    	DbContract.CellEntry.COLUMN_NAME_TYPE + " = ?";
	    
	    private final Long getPlaceId(SQLiteDatabase db, ProcessedCellInfo cell) {
	    	Cursor c = db.query(
	    			DbContract.CellEntry.TABLE_NAME,
	    			new String [] { DbContract.CellEntry.COLUMN_NAME_PLACE_ID },
	    			QUERY_WHERE_COLUMNS,
	    			new String[] { String.valueOf(cell.getCellId()), String.valueOf(cell.getType()) },
	    			null,
	    			null,
	    			null
	    			);
	    	
	    	if (c == null) {
	    		return null;
	    	}
	    	
	    	try {
		    	if (c.getCount() == 0) {
		    		return null;
		    	}
		    	c.moveToFirst();
		    	return c.getLong(0);
	    	} finally {
	    		c.close();
	    	}
	    	
	    }

	    private String getPlaceName(SQLiteDatabase db, long id) {
	    	Cursor c = db.query(
	    			DbContract.PlaceEntry.TABLE_NAME,
	    			new String [] { DbContract.PlaceEntry.COLUMN_NAME_NAME },
	    			DbContract.PlaceEntry._ID + " = ?",
	    			new String[] { String.valueOf(id) },
	    			null,
	    			null,
	    			null
	    			);
	    	if (c == null) {
	    		return null;
	    	}
	    	
	    	try {
		    	if (c.getCount() == 0) {
		    		return null;
		    	}
		    	c.moveToFirst();
		    	return c.getString(0);
	    	} finally {
	    		c.close();
	    	}
	    	
	    }
	    
	    public String queryPlace(ProcessedCellInfo cell) {
	    	SQLiteDatabase db = getReadableDatabase();
	    	Long placeId = getPlaceId(db, cell);
	    	return placeId != null ? getPlaceName(db, placeId) : null;
	    }
	    
	    public void clearDb() {
	    	SQLiteDatabase db = getWritableDatabase();
	    	db.beginTransaction();
	    	try {
	    		db.delete(DbContract.CellEntry.TABLE_NAME, null, null);
	    		db.delete(DbContract.PlaceEntry.TABLE_NAME, null, null);
	    		db.setTransactionSuccessful();
	    	} finally {
	    		db.endTransaction();
	    	}
	    	
	    }
	
}
