//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;


/** class to map {@link Field}s to {@link SwingField}s and handle events.
 * This also performs the default mapping of Forms to panels.
 * @author spb
 *
 */
public class SwingFormComponentListener implements ComponentListener {
	private JPanel panel = null;
	private static final String J_FILE_CHOOSER = "JFileChooser";
    private final Map<String,SwingField> field_map;
    private final AppContext conn;
    private final Logger log;
   
	public SwingFormComponentListener(AppContext c) {
		conn=c;
		field_map=new LinkedHashMap<>();
		log = conn.getService(LoggerService.class).getLogger(getClass());
	}

	public <I> SwingField<I> getSwingField(Field<I> f){
		String key = f.getKey();
		@SuppressWarnings("unchecked")
		SwingField<I> result = field_map.get(key);
		if( result != null ){
			return result;
		}
		result = new SwingField<>(conn, f);
		field_map.put(key, result);
		return result;
	}
	@Override
	public void componentHidden(ComponentEvent e) {

		log.debug("hidden " + e.paramString());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent e) {
		log.debug("resize " + e.paramString());

	}

	@Override
	public void componentShown(ComponentEvent e) {

		log.debug("shown " + e.paramString());
	}

	/**
	 * Get a JPanel containing the filled editing controls for this form. Note
	 * that this code assumes that only a single Panel is in existance at a time
	 * and while the panel is in use no other edits are made to the Form other
	 * than through the Panel
	 * @param form 
	 * 
	 * @param validate
	 *            boolean should the form be validated to show errors in initial
	 *            state
	 * 
	 * @return JPanel
	 * @throws Exception 
	 */
	public JPanel getPanel(Iterable<Field>  form,boolean validate) throws Exception {
		panel = new JPanel();

		panel.setLayout(new SpringLayout());
		int nrow = 0;
		// build form contents from fields
		for (Field field : form) {
			try{
				SwingField f = getSwingField(field);
				f.addLabel(panel);
				f.addInput(panel,null);
				nrow++;
				if(validate){
					// pre validate the inputs
					f.validate();
				}
			}catch(Exception e){
				log.error("Error registering field", e);
			}
		}
		makeCompactGrid(panel, nrow, 2, 5, 5, 5, 5);
		panel.addComponentListener(this);
		return panel;
	}
	/** Validate a form (Including updating SwingField errors) 
	 * 
	 * @param form
	 * @return boolean false if SwingFields fail to validate
	 * @throws ValidateException 
	 */
	public boolean validate(Form  form) throws ValidateException{
		boolean ok = true;
		for(Field f : form){
			SwingField sf = getSwingField(f);
			if( ! sf.validate()){
				ok = false;
			}
		}
		if( ok ){
			for(FormValidator val : form.getValidators()){
				val.validate(form);
			}
			return true;
		}
		return false;
	}
	

	

	public static JFileChooser getChooser(AppContext conn){
		JFileChooser chooser = (JFileChooser) conn.getAttribute(J_FILE_CHOOSER);
		if( chooser == null ){
			chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			conn.setAttribute(J_FILE_CHOOSER, chooser);
		}
		return chooser;
	}


	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(int row,
			int col, Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component in a column is as wide as
	 * the maximum preferred width of the components in that column; height is
	 * similarly determined for each row. The parent is made just big enough to
	 * fit them all.
	 * 
	 * @param parent
	 * 
	 * @param rows
	 *            number of rows
	 * @param cols
	 *            number of columns
	 * @param initialX
	 *            x location to start the grid at
	 * @param initialY
	 *            y location to start the grid at
	 * @param xPad
	 *            x padding between cells
	 * @param yPad
	 *            y padding between cells
	 */
	public static void makeCompactGrid(Container parent, int rows, int cols,
			int initialX, int initialY, int xPad, int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err
					.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width, getConstraintsForCell(r, c, parent,
						cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r,
						c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height, getConstraintsForCell(r, c, parent,
						cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r,
						c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}

	public void setComponentValues(){
		for(SwingField field : field_map.values()){
			try {
				field.setComponentValue();
			} catch (Exception e) {
				log.error("Error setting compoment: "+field.toString(),e);
			}
		}
	}
}