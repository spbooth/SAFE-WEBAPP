// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.swing;

import javax.swing.JLabel;
/** A panel containing a html fragment.
 * This provides a single point were we can add style-sheet style customisation.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: HtmlPanel.java,v 1.2 2014/09/15 14:30:21 spb Exp $")

public class HtmlPanel extends JLabel {
   public HtmlPanel(String html){
	   super("<html>"+html+"</html>");
   }
   public HtmlPanel(String html,int align){
	   super("<html>"+html+"</html>",align);
   }
}