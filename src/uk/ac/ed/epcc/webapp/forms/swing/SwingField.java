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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.ListUI;
import javax.swing.text.JTextComponent;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;
import uk.ac.ed.epcc.webapp.model.data.stream.FileStreamData;



public class SwingField<I>  {
	public static final String LIST_INPUT_UNSELECTED = "unselected";
	private final Field<I> field;
	private Map<Object,JRadioButtonMenuItem> button_map;
	private static class CachingMenuItem<I> extends JRadioButtonMenuItem{
		/**
		 * @param text
		 */
		public CachingMenuItem(I item, String text) {
			super(text);
			this.item=item;
		}

		public I getCached(){
			return item;
		}
		private final I item;
	}
	public class BinaryListener implements ItemListener {
		BinaryInput input;

		public BinaryListener(BinaryInput i) {
			input = i;
		}

		public void itemStateChanged(ItemEvent e) {
			clearError();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				
				input.setChecked(true);
			}
			if (e.getStateChange() == ItemEvent.DESELECTED) {
			
				input.setChecked(false);
			}

		}

	}
	public class BooleanItemListener implements ItemListener {
		BooleanInput input;

		public BooleanItemListener(BooleanInput i) {
			input = i;
		}

		public void itemStateChanged(ItemEvent e) {
			clearError();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				
				input.setValue(true);
			}
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				
				input.setValue(false);
			}

		}

	}

	public class  ListItemListener<V,T> implements ItemListener {
		
		ListInput<V,T> input;

		public ListItemListener(ListInput<V,T> i) {
			input = i;
		}

		@SuppressWarnings("unchecked")
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {

				
				clearError();
				Object item = e.getItem();
				if( item instanceof CachingMenuItem){
					item = ((CachingMenuItem)item).getCached();
				}
				String unselected = LIST_INPUT_UNSELECTED;
				if( input instanceof OptionalListInput){
					unselected =((OptionalListInput)input).getUnselectedText();
				}
				if( item == unselected){
					input.setItem(null);
				}else{
					input.setItem((T) item);
				}
			}

		}

	}
	//revalidate the input on loss of focus
	public class TextFocusListener implements FocusListener{
		ParseInput input;

		public TextFocusListener(ParseInput i) {

			input = i;
		}

		public void focusGained(FocusEvent e) {
			
		}

		public void focusLost(FocusEvent ev) {
			JTextComponent c = (JTextComponent) ev.getSource();
			String text = c.getText();
			//only revalidate if text has changed
			if( ! text.equals(input.getString())){
				try {
					input.parse(text);
					input.validate();
					clearError();
				} catch (ParseException e) {
					setError(e.getMessage());
				} catch (ValidateException e) {
					setError(e.getMessage());
				} catch(FieldException e){
					// report others on validate
				}
			}
		}
		
	}

	public class TextActionListener implements ActionListener {
		ParseInput input;

		public TextActionListener(ParseInput i) {

			input = i;
		}

		public void actionPerformed(ActionEvent e) {

			TextChanged(input, e);

		}

		private void TextChanged(ParseInput i, ActionEvent event) {
			JTextComponent c = (JTextComponent) event.getSource();
			String text = c.getText();
			//System.out.println("changed " + text);

			try {
				i.parse(text);
				i.validate();
				clearError();
			} catch (FieldException e) {
				// report all problems if we explicitly enter text
				setError(e.getMessage());
			}
		}
	}
	public class FileActionListener implements ActionListener{
		private final FileInput input;
		private final JComponent parent;
		public FileActionListener(JComponent parent,FileInput input) {
			this.parent=parent;
			this.input=input;
			
		}

		public void actionPerformed(ActionEvent arg0) {
			if("open_file".equals(arg0.getActionCommand())){
				JFileChooser chooser = SwingFormComponentListener.getChooser(conn);
				int ret = chooser.showOpenDialog(parent);
				if( ret == JFileChooser.APPROVE_OPTION){
					input.setValue(new FileStreamData(conn,chooser.getSelectedFile()));
				}
			}
			
		}
		
	}

	private static int MAX_SELECT_ROWS = 32;
	private final AppContext conn;
	//private Logger log;
	private JLabel lab;

	private Map<Input,JComponent> components; // map of inputs to components

	protected SwingField(AppContext conn,Field<I> field) {
		this.field=field;
		components = new Hashtable<Input,JComponent>();
		this.conn=conn;
		//this.log = conn.getService(LoggerService.class).getLogger(getClass());
	}

	private void clearError() {
		if( lab != null ){
			lab.setText(field.getLabel());
		}
	}
	public class MakeComponentVisitor implements InputVisitor<JComponent>{
		
		public JComponent visitBinaryInput(BinaryInput checkBoxInput)
				throws Exception {
			JCheckBox cb = new JCheckBox();
			cb.addItemListener(new BinaryListener(checkBoxInput));
			
			return cb;
		}

		public <V,T extends Input> JComponent visitMultiInput(MultiInput<V,T> multiInput)
				throws Exception {
			JPanel p = new JPanel();
			for(String sub_key : multiInput.getSubKeys()){
				String lab = multiInput.getSubLabel(sub_key);
				if(lab != null){
					p.add(new JLabel(lab));
				}
				Input i = multiInput.getInput(sub_key);
				JComponent j = getComponent(i,null);
				components.put(i, j);
				p.add(j);
			}
			
			return p;
		}

		@SuppressWarnings("unchecked")
		public JComponent visitListInput(ListInput listInput) throws Exception {
			JComboBox box = new JComboBox();
			if( listInput instanceof OptionalInput && ((OptionalInput)listInput).isOptional()){
				String unselected = LIST_INPUT_UNSELECTED;
				if( listInput instanceof OptionalListInput){
					unselected =((OptionalListInput)listInput).getUnselectedText();
				}
				box.addItem(unselected);
			}
			Iterator items = listInput.getItems();
			if( items != null ){
				for (Iterator it = items; it.hasNext();) {
					box.addItem(it.next());
				}
			}
			box.setRenderer(new ListRenderer(listInput));
			box.setMaximumRowCount(MAX_SELECT_ROWS);
			box.setSelectedIndex(-1);
			box.addItemListener(new ListItemListener<Object,Object>(listInput));
			return box;
		}
		public JComponent visitRadioButtonInput(ListInput listInput) throws Exception {
			
			ButtonGroup   group = new ButtonGroup();
			
			button_map = new HashMap<Object, JRadioButtonMenuItem>();
				JMenu menu = new JMenu();

				if( listInput instanceof OptionalListInput){
					
				}
				Iterator items = listInput.getItems();
				if( items != null ){
					for (Iterator it = items; it.hasNext();) {
						Object item = it.next();
						CachingMenuItem mi = new CachingMenuItem(item,listInput.getText(item));
						mi.addItemListener(new ListItemListener(listInput));
						group.add(mi);
						menu.add(mi);
						button_map.put(item, mi);
					}
				}
				return menu;
			
		}

		public JComponent visitLengthInput(LengthInput input) throws Exception {
			String def = null;
			Object o = input.getValue();
			if (o != null) {
				def = o.toString();
			}
			int len = input.getMaxResultLength();
			JTextComponent result;
			// Use a single text box up to twice maxwid
			// above that use a textarea
			int maxwid = 32;
			if (len <= 2 * maxwid) {
				JTextField f;
				// single row input
				int size = len;
				if (len > maxwid) {
					size = maxwid;
				}
				if (def != null && def.length() > 0) {
					f = new JTextField(def, size);
				} else {
					f = new JTextField(size);
				}
				f.addActionListener(new TextActionListener(input));
				f.addFocusListener(new TextFocusListener(input));
				result = f;
			} else {
				JTextArea f;
				int rows = ((len + maxwid - 1) / maxwid);
				if (rows > 24) {
					rows = 24;
				}
				if (def != null && def.length() > 0) {
					f = new JTextArea(def, rows, maxwid);
				} else {
					f = new JTextArea(rows, maxwid);
				}
				f.setWrapStyleWord(true);
				result = f;
			}
			JTextComponent j = result;
			return j;
		}

		public JComponent visitUnmodifyableInput(UnmodifiableInput input)
				throws Exception {
			return new JLabel(input.getLabel());
		}

		public JComponent visitFileInput(FileInput input) throws Exception {
			JButton button = new JButton("Select File");
			button.setActionCommand("open_file");
			button.addActionListener(new FileActionListener(button,input));
			return button;
		}

		public JComponent visitPasswordInput(PasswordInput input)
				throws Exception {
			JPasswordField p = new JPasswordField();
			p.setColumns(input.getMaxResultLength());
			p.addActionListener(new TextActionListener(input));
			return p;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
		 */
		@Override
		public <V, I extends Input> JComponent visitParseMultiInput(
				ParseMultiInput<V, I> multiInput) throws Exception {
			return visitMultiInput(multiInput);
		}
		
	}
	/**
	 * get a GUI component corresponding to the Input
	 * 
	 * @param input
	 * @return a JComponent
	 * @throws Exception 
	 */
	private <T> JComponent getComponent(Input<?> input,T radio_selector) throws Exception {
		
//		if (input instanceof CompositeInput) {
//			return getMultiInput((CompositeInput) input);
//		}
//		JComponent j;
//		if (input instanceof CheckBoxInput) {
//			j = getBinaryComponent((BinaryInput) input);
//		} else if (input instanceof ConstantInput) {
//			j = new JLabel(((ConstantInput) input).getLabel());
//		} else if (input instanceof ListInput) {
//			j = getListComponent((ListInput) input);
//		} else if (input instanceof PasswordInput) {
//			j = getPasswordComponent((PasswordInput) input);
//		} else if (input instanceof ParseAbstractInput) {
//			j = getTextComponent((ParseAbstractInput) input);
//		}else if(input instanceof BooleanInput){
//			j = getBooleanComponent((BooleanInput)input);
//		} else {
//			log.warn("Input "+input.getClass().getCanonicalName()+" not implemented in swing");
//			j = new JLabel("Unimplemented");
//		}
		JComponent cached = components.get(input);
		if( cached == null){
			MakeComponentVisitor vis = new MakeComponentVisitor();
			cached = input.accept(vis);
			components.put(input, cached);
		}
		if( button_map != null && radio_selector != null){
			return button_map.get(radio_selector);
		}
		
		return cached;
	}

	
	

	void addLabel(JComponent form) throws Exception {
		lab = new JLabel(field.getLabel());
		form.add(lab);
		Input<I> input = field.getInput();
		if( input instanceof OptionalInput &&((OptionalInput)input).isOptional()){
			lab.setForeground(Color.GRAY);
		}
		JComponent field_component = getComponent(input,null);
		lab.setLabelFor(field_component);
	}
	<T> void addInput(JComponent form,T radio_selector) throws Exception {
		Input<I> input = field.getInput();
		JComponent field_component = getComponent(input,radio_selector);
		form.add(field_component);
		setComponentValue(input);
	}

	
	/** class to synchronize the components with the input
	 * 
	 * @author spb
	 *
	 */
	public class SetComponentVisitor implements InputVisitor<Object>{

		public Object visitBinaryInput(BinaryInput checkBoxInput)
				throws Exception {
			JCheckBox box = (JCheckBox) components.get(checkBoxInput);
			box.setSelected(checkBoxInput.isChecked());
			return null;
		}

		public Object visitMultiInput(MultiInput input) throws Exception {
			for (Iterator it = input.getInputs(); it.hasNext();) {
				Input<?> i = (Input) it.next();
				i.accept(this);
			}
			return null;
		}

		public <V,T>Object visitListInput(ListInput<V,T> input) throws Exception {
			V value = input.getValue();
			Object item;
			if (value != null) {
				item = input.getItembyValue(value);
				((JComboBox)components.get(input)).setSelectedItem(item);
			} else {
				if( input instanceof OptionalInput && ((OptionalInput)input).isOptional()){
					item = LIST_INPUT_UNSELECTED;
					((JComboBox)components.get(input)).setSelectedItem(item);
				}else{
					((JComboBox)components.get(input)).setSelectedIndex(-1);
				}
			}
			
			return null;
		}
		public <V,T>Object visitRadioButtonInput(ListInput<V,T> input) throws Exception {
			V value = input.getValue();
			Object item;
			JMenu menu =((JMenu)components.get(input));
			if (value != null) {
				item = input.getItembyValue(value);
				Iterator items = input.getItems();
				int pos=0;
				if( items != null ){
					for (Iterator it = items; it.hasNext();) {
						if( it.next().equals(item) ){
							JRadioButtonMenuItem mi=(JRadioButtonMenuItem) menu.getItem(pos);
							mi.setSelected(true);
							break;
						}
						pos++;
					}
				}
			} else{
				menu.setSelected(false);
			}
			
			return null;
		}
		public Object visitLengthInput(LengthInput input) throws Exception {
			String value=input.getString();
			if (value == null) {
				value = "";
			}
			((JTextComponent)components.get(input)).setText(value);
			return null;
		}

		public Object visitUnmodifyableInput(UnmodifiableInput input)
				throws Exception {
			JLabel lab = (JLabel) components.get(input);
			lab.setText(input.getLabel());
			return null;
		}

		public Object visitFileInput(FileInput input) throws Exception {
			
			return null;
		}

		public Object visitPasswordInput(PasswordInput input) throws Exception {
			JPasswordField jpf = (JPasswordField) components.get(input);
			jpf.setText(input.getString());
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
		 */
		@Override
		public <V, I extends Input> Object visitParseMultiInput(
				ParseMultiInput<V, I> multiInput) throws Exception {
			return visitMultiInput(multiInput);
		}
		
	}
	/**
	 * Synchronize the GUI components with the value of the Input
	 * 
	 * @param c
	 * @param val
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void setComponentValue(Input i) throws Exception {
		SetComponentVisitor vis = new SetComponentVisitor();
		i.accept(vis);
//		if (i instanceof CompositeInput) {
//			setCompositeComponentValue((CompositeInput) i);
//		}
//		JComponent c = (JComponent) components.get(i);
//		if (c instanceof JCheckBox && i instanceof CheckBoxInput) {
//			setCheckBoxComponentValue((JCheckBox) c, (BinaryInput) i);
//		}
//		if (c instanceof JCheckBox && i instanceof BooleanInput) {
//			setBooleanComponentValue((JCheckBox) c, (BooleanInput) i);
//		}
//		if (c instanceof JComboBox && i instanceof ListInput) {
//			ListInput<V, T> input2 = (ListInput) i;
//			Object value = input2.getValue();
//			Object item;
//			if (value != null) {
//				item = input2.getItembyValue(value);
//			} else {
//				item = "unselected";
//			}
//			((JComboBox) c).setSelectedItem(item);
//		}
//		if (c instanceof JTextComponent && i instanceof LengthInput) {
//			setTextComponentValue((JTextComponent) c, (LengthInput) i);
//		}
	}

//	private void setCompositeComponentValue(CompositeInput input) {
//		for (Iterator it = input.getInputs(); it.hasNext();) {
//			setComponentValue((Input) it.next());
//		}
//	}


	protected void setComponentValue() throws Exception{
		setComponentValue(field.getInput());
	}
	private void setError(String message) {
		if (message != null && message.length() > 0) {
			lab.setText("<html>" + field.getLabel() + " <font color=#ff0000>"
					+ message + "</font></html>");
		} else {
			clearError();
		}
	}

	

	private void setMissing(boolean miss) {
		if (miss) {
			setError("*");
		} else {
			clearError();
		}
	}

	public boolean validate(){
		try{
			field.validate();
			clearError();
		}catch(MissingFieldException e){
			setMissing(true);
			return false;
		}catch(FieldException e){
			setError(e.getMessage());
			return false;
		}
		return true;
	}
}