package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import com.lowagie.text.Font;

import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.barcode.dao.BarcodeLabelInfoDAO;
import us.mn.state.health.lims.barcode.daoimpl.BarcodeLabelInfoDAOImpl;
import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelInfo;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;

public abstract class Label {
	
	static int SMALL_BARCODE = 3;
	static int MED_BARCODE = 4;
	static int LARGE_BARCODE = 5;
	
	private Font valueFont =  new Font(Font.HELVETICA, 10, Font.NORMAL);
	private Font nameFont =  new Font(Font.HELVETICA, 10, Font.BOLD);
	protected float height = 1;
	protected float width = 3;
	private int margin = 5;

	//min 1 max 5
	private int barcodeSpace = MED_BARCODE;

	protected ArrayList<BarcodeLabelField> aboveFields;
	protected ArrayList<BarcodeLabelField> belowFields;
	private String code;
	private String sysUserId;
	
	//default number of copies to print
	private int numLabels = 1;
	
	public BarcodeLabelInfo barcodeLabelInfo;
	boolean newInfo;


	public abstract int getNumTextRowsBefore();
	
	public abstract int getNumTextRowsAfter();
	
	public abstract int getMaxNumLabels();

	public int getScaledBarcodeSpace() {
		return barcodeSpace * 2;
	}
	
	public Font getValueFont() {
		return valueFont;
	}

	public void setValueFont(Font valueFont) {
		this.valueFont = valueFont;
	}

	public Font getNameFont() {
		return nameFont;
	}

	public void setNameFont(Font nameFont) {
		this.nameFont = nameFont;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public int getBarcodeSpace() {
		return barcodeSpace;
	}

	public void setBarcodeSpace(int barcodeSpace) {
		this.barcodeSpace = barcodeSpace;
	}

	public Iterable<BarcodeLabelField> getAboveFields() {
		return aboveFields;
	}

	public void setAboveFields(ArrayList<BarcodeLabelField> aboveFields) {
		this.aboveFields = aboveFields;
	}

	public Iterable<BarcodeLabelField> getBelowFields() {
		return belowFields;
	}

	public void setBelowFields(ArrayList<BarcodeLabelField> belowFields) {
		this.belowFields = belowFields;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getNumLabels() {
		return numLabels;
	}

	public void setNumLabels(int numLabels) {
		this.numLabels = numLabels;
	}
	
	public String getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(String sysUserId) {
		this.sysUserId = sysUserId;
	}
	
	protected int getNumRows(Iterable<BarcodeLabelField> fields) {
		int numRows = 0;
		int curColumns = 0;
		boolean completeRow = true;
		for (BarcodeLabelField field : fields) {
			//add to num row if start on newline
			if (field.isStartNewline() && !completeRow) {
				++numRows;
				curColumns = 0;
			}
			curColumns += field.getColspan();
			if (curColumns > 10) {
				//throw error
				//row is completed, add to num row
			} else if (curColumns == 10) {
				completeRow = true;
				curColumns = 0;
				++numRows;
			} else {
				completeRow = false;
			}
		}
		//add to num row if last row was incomplete
		if (!completeRow) {
			++numRows;
		}
		return numRows;
	}
	
	public boolean checkIfPrintable() {
		boolean printable = true;
		if (barcodeLabelInfo.getNumPrinted() >= getMaxNumLabels())
			printable = false;
		return printable;
	}
	
	public void linkBarcodeLabelInfo() {
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			BarcodeLabelInfoDAO barcodeLabelDAO = new BarcodeLabelInfoDAOImpl();
			barcodeLabelInfo = barcodeLabelDAO.getDataByCode(code);
			tx.commit();
			newInfo = false;
			if (barcodeLabelInfo == null) {
				newInfo = true;
				barcodeLabelInfo = new BarcodeLabelInfo(code);
			}
		} catch (LIMSRuntimeException lre) {
			LogEvent.logError("Label","linkBarcodeLabelInfo()",lre.toString());
			tx.rollback();
		}  finally {
            HibernateUtil.closeSession();
        }
	}
	
	//increment the amount in the database
	public void incrementNumPrinted() {
		barcodeLabelInfo.incrementNumPrinted();
		barcodeLabelInfo.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			BarcodeLabelInfoDAO barcodeLabelDAO = new BarcodeLabelInfoDAOImpl();
			if (newInfo) {
				barcodeLabelDAO.insertData(barcodeLabelInfo);
				newInfo = false;
			} else {
				barcodeLabelDAO.updateData(barcodeLabelInfo);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			LogEvent.logError("Label","linkBarcodeLabelInfo()",lre.toString());
			tx.rollback();
		}  finally {
            HibernateUtil.closeSession();
        }
	}
	
}
