package cz.prochy.metrostation;

import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

public class ProcessedCellInfo {
	private final int cellId;
	private final int type;
	private final int signalLevel;

	public ProcessedCellInfo(CellInfoGsm cellInfo) {
		this.signalLevel = cellInfo.getCellSignalStrength().getLevel();
		this.type = DbContract.CellEntry.CELL_TYPE_GSM;
		this.cellId = cellInfo.getCellIdentity().getCid();
	}

	public ProcessedCellInfo(CellInfoCdma cellInfo) {
		this.signalLevel = cellInfo.getCellSignalStrength().getLevel();
		this.type = DbContract.CellEntry.CELL_TYPE_CDMA;
		this.cellId = cellInfo.getCellIdentity().getBasestationId();		
	}
	
	public ProcessedCellInfo(CellInfoWcdma cellInfo) {
		this.signalLevel = cellInfo.getCellSignalStrength().getLevel();
		this.type = DbContract.CellEntry.CELL_TYPE_WCDMA;
		this.cellId = cellInfo.getCellIdentity().getCid();		
	}
	
	public ProcessedCellInfo(CellInfoLte cellInfo) {
		this.signalLevel = cellInfo.getCellSignalStrength().getLevel();
		this.type = DbContract.CellEntry.CELL_TYPE_LTE;
		this.cellId = cellInfo.getCellIdentity().getCi();		
	}

	public int getCellId() {
		return cellId;
	}

	public int getType() {
		return type;
	}

	public int getSignalLevel() {
		return signalLevel;
	}

	public boolean hasValidId() {
		return cellId != Integer.MAX_VALUE && cellId != -1;
	}
	
}
