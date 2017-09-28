package us.mn.state.health.lims.barcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class LabelMaker {
	
	private static String FILE = "C:/Users/Caleb/Documents/UofW/Projects/OpenELIS/Features/Barcode/sample.pdf";
	
	public LabelMaker() {
		
	}
	
	public File createLabel(String barcode) {
		PdfWriter writer;
		try {

			Document document = new Document();
			writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
	        Rectangle rec = new Rectangle(300, 100);
	        document.setPageSize(rec);
	        document.setMargins(10, 10, 10, 10);
			document.open();
			PdfPTable table = new PdfPTable(1);
	        table.addCell(createBarcode(writer, barcode));
	        document.add(table);
	        document.close();
	        writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new File(FILE);
	}
	
	public PdfPCell createBarcode(PdfWriter writer, String code) throws DocumentException, IOException {
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode(code);
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), null, null), true);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

}
