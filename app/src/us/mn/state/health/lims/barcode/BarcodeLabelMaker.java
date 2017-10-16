package us.mn.state.health.lims.barcode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import us.mn.state.health.lims.barcode.labeltype.OrderLabel;
import us.mn.state.health.lims.barcode.labeltype.BlankLabel;
import us.mn.state.health.lims.barcode.labeltype.SpecimenLabel;
import us.mn.state.health.lims.barcode.labeltype.Label;

public class BarcodeLabelMaker {

	private static String DEFAULT_CODE = "000000000";
	private static int SCALE = 100;
	private static int NUM_COLUMNS = 10;
	
	private Label curLabel;
	private ArrayList<Label> labels = new ArrayList<Label>();
	private float labelWidth;
	private float labelHeight;

	//defaults to making blank label
	public BarcodeLabelMaker() {
		labels.add(new BlankLabel(DEFAULT_CODE));
	}

	//defaults to making blank label
	public BarcodeLabelMaker(String code) {
		labels.add(new BlankLabel(code));
	}
	
	//make specified type of label
	public BarcodeLabelMaker(Label label) {
		labels.add(label);
	}
	
	public BarcodeLabelMaker(ArrayList<Label> labels) {
		this.labels = labels;
	}
	
	public void addLabelToQueue(Label label) {
		labels.add(label);
	}
	
	//create stream for sending pdf to client
	public ByteArrayOutputStream createLabelsAsStream() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (labels.isEmpty()) 
			return stream;
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, stream);
	        document.open();
	        for (Label label : labels) {
	        	for (int i = 0; i < label.getNumLabels(); ++i) {
	        		if (label.checkIfPrintable()) {
	        			curLabel = label;
	        			labelWidth = curLabel.getWidth() * SCALE;
	        			labelHeight = curLabel.getHeight() * SCALE;
	        			drawLabel(writer, document);
	        			curLabel.incrementNumPrinted();
	        		}	        		
	        	}
	        }
			document.close();
	        writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return stream;
	}
	
	//parse label info to draw label and add to document
	private void drawLabel(PdfWriter writer, Document document) 
			throws DocumentException, IOException {
		//set up document and grid
		Rectangle rec = new Rectangle(labelWidth, labelHeight);
        document.setPageSize(rec);
		document.newPage();
		PdfPTable table = new PdfPTable(NUM_COLUMNS);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setTotalWidth(labelWidth - (2 * curLabel.getMargin()));
	    table.setLockedWidth(true);
		//add fields
		Iterable<BarcodeLabelField> fields = curLabel.getAboveFields();
		for (BarcodeLabelField field : fields) {
			if (field.isStartNewline()) 
				table.completeRow();
            table.addCell(createField(field.getName(), field.getValue(), field.getColspan()));
        } 
	    table.completeRow();
	    //add barcode
	    if (curLabel.getScaledBarcodeSpace() != NUM_COLUMNS) {
	    	table.addCell(createSpacerCell((NUM_COLUMNS - curLabel.getScaledBarcodeSpace()) / 2));
		    table.addCell(create128Barcode(writer, curLabel.getScaledBarcodeSpace()));
	    	table.addCell(createSpacerCell((NUM_COLUMNS - curLabel.getScaledBarcodeSpace()) / 2));
	    } else {
	    	table.addCell(create128Barcode(writer, curLabel.getScaledBarcodeSpace()));
	    } 
	    //add fields below barcode
		Iterable<BarcodeLabelField> belowFields = curLabel.getBelowFields();
	    if (belowFields != null) {
	    	for (BarcodeLabelField field : belowFields) {
				if (field.isStartNewline()) 
					table.completeRow();
	            table.addCell(createField(field.getName(), field.getValue(), field.getColspan()));
	        } 
		    table.completeRow();   	
	    }
        //finish document
        document.add(scaleCentreTableAsImage(writer, document, table));
	}
	
	//converts table to centered scaled image (set by absolute position)
	private Image scaleCentreTableAsImage(PdfWriter writer, Document document, PdfPTable table) 
			throws BadElementException {
		PdfContentByte cb = writer.getDirectContent();
        PdfTemplate template = cb.createTemplate(table.getTotalWidth(), table.getTotalHeight());
        table.writeSelectedRows(0, -1, 0, table.getTotalHeight(), template);
        Image labelAsImage = Image.getInstance(template);
        labelAsImage.scaleAbsoluteHeight(labelHeight - (2 * curLabel.getMargin()));
        labelAsImage.setAbsolutePosition(((labelWidth) - labelAsImage.getScaledWidth()) / 2, 
        		((labelHeight) - labelAsImage.getScaledHeight()) / 2);
        return labelAsImage;
	}
	
	//create code 128 barcode with bottom text
	private PdfPCell create128Barcode(PdfWriter writer, int colspan) 
			throws DocumentException, IOException {
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode(curLabel.getCode());
        barcode.setBarHeight((10 - (curLabel.getNumTextRowsBefore() + curLabel.getNumTextRowsAfter())) * 30 / 10);
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), null, null), true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(colspan);
        cell.setPadding(1);
        return cell;
    }
	
	//create code 128 barcode without text Recommended for large fonts
	@SuppressWarnings("unused")
	private PdfPCell create128BarcodeNoText(PdfWriter writer, int colspan) 
			throws DocumentException, IOException {
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode(curLabel.getCode());
        barcode.setFont(null);
        barcode.setBarHeight((10 - (curLabel.getNumTextRowsBefore() + curLabel.getNumTextRowsAfter())) * 30 / 10);
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), null, null), true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(colspan);
        cell.setPadding(1);
        return cell;
    }
	
	//create name value field pair
	private PdfPCell createField(String fieldName, String fieldValue, int colspan) {
		Chunk name = new Chunk(fieldName + ": ");
		Chunk value = new Chunk(fieldValue);
		Chunk underline = new Chunk(new LineSeparator(0.5f, 100, null, 0, -1));
		name.setFont(curLabel.getValueFont());
		value.setFont(curLabel.getValueFont());
		value.setUnderline(0.5f, -1);
		Paragraph field = new Paragraph();
		field.add(name);
		field.add(value);
		field.add(underline);
		PdfPCell cell = new PdfPCell(field);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(colspan);
        cell.setPadding(1);
		return cell;
	}
	
	//returns a blank cell
	private PdfPCell createSpacerCell(int colspan) {
		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(colspan);
		return cell;
	}

}
