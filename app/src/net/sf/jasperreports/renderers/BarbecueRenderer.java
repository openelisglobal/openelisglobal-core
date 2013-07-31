/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 * JasperSoft Corporation
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */

/*
 * Contributors:
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.renderers;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRAbstractSvgRenderer;
import net.sourceforge.barbecue.Barcode;
import us.mn.state.health.lims.common.util.SystemConfiguration;


/**
 * A wrapper for the Drawable interface in the JCommon library: you will need the
 * JCommon classes in your classpath to compile this class. In particular this can be
 * used to allow JFreeChart objects to be included in the output report in vector form.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class BarbecueRenderer extends JRAbstractSvgRenderer
{

	/**
	 *
	 */	
	private Barcode barcode = null;


	/**
	 *
	 */	
	public BarbecueRenderer(Barcode barcode) 
	{
		this.barcode = barcode;
	}


	/**
	 *
	 */
	public void render(Graphics2D grx, Rectangle2D rectangle) 
	{
		if (barcode != null) 
		{
			String height = SystemConfiguration.getInstance().getBarcodeHeight();
			double h = Double.parseDouble(height);
			String width = SystemConfiguration.getInstance().getBarcodeWidth();
			double w = Double.parseDouble(width);
			//String res = SystemConfiguration.getInstance().getBarcodeResolution();
			//int r = Integer.parseInt(res);
			barcode.setBarHeight(h);
			barcode.setBarWidth(w);
			//barcode.setResolution(r);
		
			
			/*File fd = new File(".");
			String [] fs = fd.list();*/
			/*for (int i = 0; i < fs.length; i++) {
				System.out.println("This is a file or direct " + fs[i]);
			}*/

			/*File file = new File("C:/DEVELOPMENT_OPENREPORTS/lims/fonts/code128.TTF");
			Font small = null;*/
	        /*try
	        {
	            FileInputStream in = new FileInputStream(file);
	            Font c128 = Font.createFont(Font.TRUETYPE_FONT, in);
	            float f = 8;
	            small = c128.deriveFont(f);
	            //barcode.setFont(small);
	            //barcode.setResolution(300);
	        }catch(Exception e) { e.printStackTrace(); }*/

            //Font f = Font.createFont(Font.TRUETYPE_FONT, 4);
            //Font font = new Font("CIA Code 128", Font.TRUETYPE_FONT, 4);
		    //Font font = f.deriveFont(18.0f);

		    //System.out.println("font " + grx.getFont().getName() + " " + grx.getFont().getSize() + " " + grx.getFont().getFamily());
		    //grx.setFont(small);
		    
		   		    
		    //System.out.println("font " + grx.getFont().getName() + " " + grx.getFont().getSize() + " " + grx.getFont().getFamily());
		    //System.out.println("This is max size of barcode " + barcode.getMaximumSize());
		    //System.out.println("This is min size of barcode " + barcode.getMinimumSize());
		    //System.out.println("This is pref size of barcode " + barcode.getPreferredSize());
		    
		    grx.scale(0.5, 0.4);
		   // grx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            //RenderingHints.VALUE_ANTIALIAS_ON);
		    	
		 
			barcode.draw(grx, (int)rectangle.getX(), (int)rectangle.getY());
		}
	}

	
}
