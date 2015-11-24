// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.apps;

import java.awt.Color;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class Heatmap {

	/**
	 * 
	 */
	public Heatmap() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String argv[]){
		System.out.println("Hello world\n");
		for(int i=0 ; i<=10 ;i++){
			Color c = Color.getHSBColor((float)(0.0333333f*i), 0.9f, 0.93f);
			System.out.println("#"+Integer.toHexString(c.getRed())+Integer.toHexString(c.getGreen())+Integer.toHexString(c.getBlue()));
		}
	}
}
