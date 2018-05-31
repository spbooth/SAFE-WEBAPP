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
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.content.XMLGenerator;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
/** builds a swing version of content.
 * 
 * @author spb
 *
 */


public class SwingContentBuilder  implements ContentBuilder{
	private final JFrame frame;
	private final JComponent content;
    private SwingContentBuilder parent=null;
    private JFormDialog form_dialog=null; // enclosing JformDialog
    private String type=null;
    private String type_class=null;
    private Font font=null;
    private int align=SwingConstants.LEFT;
    private Border border=BorderFactory.createEmptyBorder();
    private Color fg=null;
    private Color bg=null;
	private final AppContext conn;
	private final Logger log;
	private SwingFormComponentListener listener;

	private Object add_param=null;
	public SwingContentBuilder(AppContext conn, JFrame frame){
		this(conn,new JPanel(),frame);
		//TOP-level stacks vertically
		content.setMaximumSize(new Dimension(450, 10000));
		content.setLayout(new BoxLayout(content ,BoxLayout.Y_AXIS));
		
	}
	/** Create a SwingContentBuilder for building FormContent in
	 * a form dialog.
	 * 
	 * @param dialog
	 */
	SwingContentBuilder(JFormDialog dialog){
		this(dialog.getContext(),new JPanel(),dialog.getFrame());
		this.form_dialog=dialog;
		this.listener = new SwingFormComponentListener(dialog.getContext());
		content.setLayout(new BoxLayout(content ,BoxLayout.Y_AXIS));
	}
	public SwingContentBuilder(AppContext conn, JComponent content,JFrame frame){
		this.conn=conn;
		this.content=content;
		this.frame=frame;
		log=conn.getService(LoggerService.class).getLogger(getClass());
		log.debug("made SwingContentBuilder");
	}
	private SwingContentBuilder(SwingContentBuilder parent,String type, String ... type_classes){
		this(parent.conn,parent.frame);
		this.parent=parent;
		this.form_dialog=parent.form_dialog;
		this.listener=parent.listener;
		// parse config parameters.
		this.type=type;
		//TODO handle multiple type classes
		this.type_class=type_classes[0];
		ConfigService cfg = parent.conn.getService(ConfigService.class);
		Properties prop = cfg.getServiceProperties();
		this.font=parseFont(prop.getProperty("style.font."+type+"."+type_class,prop.getProperty("style.font."+type)),parent.font);
		this.align=parseAlign(prop.getProperty("style.align."+type+"."+type_class,prop.getProperty("style.align."+type)),parent.align);
		this.border=parseBorder(prop.getProperty("style.border."+type+"."+type_class,prop.getProperty("style.border."+type)),parent.border);
		this.bg=parseColor(prop.getProperty("style.bg."+type+"."+type_class,prop.getProperty("style.bg."+type)),parent.bg);
		this.fg=parseColor(prop.getProperty("style.fg."+type+"."+type_class,prop.getProperty("style.fg."+type)),parent.fg);
		setStyle();
	}
	private Color parseColor(String col,Color def){
		if( col == null || col.trim().length()==0){
			return def;
		}
		if( col.startsWith("#")){
			return new Color(Integer.parseInt(col.substring(1), 16));
		}
		return def;
	}
	private Font parseFont(String font,Font def){
		if(font == null || font.trim().length()==0){
			return def;
		}
		String fields[] = font.split("\\s+");
		int style = Font.PLAIN;
		int size=16;
		if( fields.length > 1){
			if( fields[1].equalsIgnoreCase("bold")){
				style=Font.BOLD;
			}else if( fields[1].equalsIgnoreCase("italic")){
				style=Font.ITALIC;
			}else if( fields[1].equalsIgnoreCase("plain")){
				style=Font.PLAIN;
			}
		}
		return new Font(fields[0],style,size);
	}
	private Border parseBorder(String border,Border def){
		if( border == null || border.trim().length()==0){
			return def;
		}
		String fields[] = border.split("\\s+");
		Color col = Color.BLACK;
		if( fields.length > 1){
			col = parseColor(fields[1], Color.BLACK);
		}
		int width=1;
		if( fields.length > 2){
			width=Integer.parseInt(fields[2]);
		}
		if( fields[0].equalsIgnoreCase("solid")){
			return BorderFactory.createLineBorder(col,width);
		}
		return def;
	}
	private int parseAlign(String align, int def){
		if(align == null || align.trim().length()==0){
			return def;
		}
		if(align.equalsIgnoreCase("left")){
			return SwingConstants.LEFT;
		}
		if(align.equalsIgnoreCase("right")){
			return SwingConstants.RIGHT;
		}
		if(align.equalsIgnoreCase("center")){
			return SwingConstants.CENTER;
		}
		return def;
	}
	private void setStyle(){
		if( font != null ){
			content.setFont(font);
		}
		if( bg != null){
			content.setBackground(bg);
		}
		if( fg != null ){
			content.setForeground(fg);
		}
		if( border != null ){
			content.setBorder(border);
		}
	}
	public void setFont(Font f){
		font=f;
	}
	public Font getFont(){
		return font;
	}
	public void setAlign(int align){
		this.align=align;
	}
	public int getAlign(){
		return align;
	}
	public void setBorder(Border border){
		this.border=border;
	}
	public Border getBorder(){
		return border;
	}
	public JComponent getComponent(){
		return content;
	}

	private void addComponent(JComponent comp){
		if( add_param == null ){
			content.add(comp);
		}else{
			content.add(comp,add_param);
		}
	}

	public static class XMLPanel implements ExtendedXMLBuilder{
		// XML content built here then converted to stringand added to parent
		private final ExtendedXMLBuilder nested;
		private final SwingContentBuilder parent;
		public XMLPanel(SwingContentBuilder parent,ExtendedXMLBuilder nested) {
			this.parent = parent;
			this.nested=nested;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.CharSequence)
		 */
		@Override
		public SimpleXMLBuilder clean(CharSequence s) {
			nested.clean(s);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(char)
		 */
		@Override
		public SimpleXMLBuilder clean(char c) {
			nested.clean(c);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.Number)
		 */
		@Override
		public SimpleXMLBuilder clean(Number i) {
			nested.clean(i);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String)
		 */
		@Override
		public SimpleXMLBuilder open(String tag) {
			nested.open(tag);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String, java.lang.String[][])
		 */
		@Override
		public SimpleXMLBuilder open(String tag, String[][] attr) {
			nested.open(tag, attr);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#attr(java.lang.String, java.lang.CharSequence)
		 */
		@Override
		public SimpleXMLBuilder attr(String name, CharSequence s) {
			nested.attr(name, s);
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#close()
		 */
		@Override
		public SimpleXMLBuilder close() {
			nested.close();
			return this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getNested()
		 */
		@Override
		public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
			return nested.getNested();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#appendParent()
		 */
		@Override
		public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
			appendTo(parent);
			return null;
		}
		protected void appendTo(SwingContentBuilder builder) {
			builder.addText(nested.toString());
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getParent()
		 */
		@Override
		public SimpleXMLBuilder getParent() {
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#setEscapeUnicode(boolean)
		 */
		@Override
		public boolean setEscapeUnicode(boolean escape_unicode) {
			return nested.setEscapeUnicode(escape_unicode);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#nbs()
		 */
		@Override
		public void nbs() {
			nested.nbs();
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#br()
		 */
		@Override
		public void br() {
			nested.br();
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#addClass(java.lang.CharSequence)
		 */
		@Override
		public SimpleXMLBuilder addClass(CharSequence s) {
			nested.addClass(s);
			return this;
		}

	}
    private <C,R> void addCell(Table<C,R> t, C key, R row_key,NumberFormat nf,SwingContentBuilder dest){
    	Object n = t.get(key,row_key);
		Table.Formatter format = t.getColFormat(key);
		if( format != null){
		
			n=format.convert(t,key,row_key,n);
		}
		if( n == null ){
			dest.addText("");
			return;
		}
		if( n instanceof UIGenerator){
			((UIGenerator)n).addContent(dest);
			return;
		}
		if( n instanceof XMLGenerator){
			ExtendedXMLBuilder text = dest.getText();
			((XMLGenerator)n).addContent(text);
			text.appendParent();
			return;
		}
		if( n instanceof Table ){
			dest.addTable(conn, (Table) n, nf,"inner");
		}
		if( n instanceof Number && n != null ){
			dest.addText(nf.format((Number)n));
		}else{
			dest.addText(n.toString());
		}
    }

	public void addButton(AppContext c,String text, FormResult action) {
		log.debug("add button");
		JButton button = new JButton(text);
		
		button.addActionListener(new FormResultActionListener(conn, frame, action));
		addComponent(button);
	}

	public void addButton(AppContext c, String text, String hover, FormResult action){
		log.debug("add button");
		JButton button = new JButton(text);
		button.setToolTipText(hover);
		button.addActionListener(new FormResultActionListener(conn, frame, action));
		addComponent(button);
	}
	public void addLink(AppContext c,String text, FormResult action) {
		log.debug("add link");
		addButton(c,text, action);

	}
	public void addLink(AppContext c,String text, String hover,FormResult action) {
		log.debug("add link");
		addButton(c,text, hover,action);

	}
	public <C,R> void addTable(AppContext conn,Table<C,R> t) {
		addTable(conn,null,t);
	}
	public <C,R> void addTable(AppContext conn,NumberFormat nf,Table<C,R> t) {
		log.debug("add table");
		JPanel table=new JPanel(new GridBagLayout());
		
		addComponent(table);
		GridBagConstraints c = new GridBagConstraints();
		SwingContentBuilder inner=new SwingContentBuilder(conn, table, frame);
		inner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		inner.add_param=c;
		c.ipadx=4;
		c.ipady=4;
		c.fill=GridBagConstraints.BOTH;
		c.insets=new Insets(1, 1, 1, 1);
		c.gridx=0;
		c.gridy=0;
		//first the headings
		if(t.isPrintHeadings()){
			if(t.printKeys()){
				inner.addText(t.getKeyName());
				c.gridx++;
			}
			for(C col : t.getCols()){
				if( col instanceof UIGenerator){
					((UIGenerator)col).addContent(inner);
				}else{
					inner.addText(col.toString());
				}
				c.gridx++;
			}
			c.gridy++;
		}
		// then the body
		for(R row : t.getRows()){

			c.gridx=0;
			if( t.printKeys()){
				inner.addText(t.getKeyText(row).toString());
				c.gridx++;
			}
			for(C col : t.getCols()){
				addCell(t,col,row,nf,inner);
				c.gridx++;
			}
			c.gridy++;
		}
		addComponent(inner.getComponent());

	}
	
	
	public ExtendedXMLBuilder getText() {
		log.debug("getText");
		return new XMLPanel(this,new HtmlBuilder());
	}
	public ExtendedXMLBuilder getSpan() {
		log.debug("getText");
		return new XMLPanel(this,new HtmlBuilder());
	}

	public ContentBuilder getHeading(int level) {
		log.debug("getHeading");
		SwingContentBuilder builder = new SwingContentBuilder(this,"h"+level,"");
		return builder;
	}
	public ContentBuilder getPanel(String ... type)
			throws UnsupportedOperationException {
		log.debug("getPanel");
		
		SwingContentBuilder panel = new SwingContentBuilder(this,"div",type);
		panel.content.setLayout(new BoxLayout(panel.content, BoxLayout.Y_AXIS));
		return panel;
	}
	
	public ContentBuilder addParent() throws UnsupportedOperationException {
		log.debug("addParent");
		if( parent == null){
			throw new UnsupportedOperationException("No parent");
		}
		parent.addComponent(content);
		return parent;
	}
	
	public <C, R> void addColumn(AppContext conn, Table<C, R> t, C col) {
		JPanel table=new JPanel(new GridBagLayout());
		addComponent(table);
		GridBagConstraints c = new GridBagConstraints();
		SwingContentBuilder inner=new SwingContentBuilder(conn, table, frame);
		inner.add_param=c;
		c.gridx=0;
		c.gridy=0;
		
		// then the body
		for(R row : t.getRows()){
			c.gridx=0;
			if( t.printKeys()){
				inner.addText(t.getKeyText(row).toString());
				c.gridx++;
			}
			
			addCell(t,col,row,null,inner);
			c.gridx++;
			
			c.gridy++;
		}
		addComponent(inner.getComponent());
	}
	public void addText(String text) {
		log.debug("addText: "+text);
		JLabel label = new JLabel("<html>"+text+"</html>");
		label.setMaximumSize(new Dimension(400, 10000));
		setStyle(label);
		addComponent(label);
		
	}
	private void setStyle(JComponent comp) {
		if( font != null){
		  comp.setFont(font);
		}
		if( border != null ){
			comp.setBorder(border);
		}
		if( fg != null){
			comp.setForeground(fg);
		}
		if( bg != null ){
			comp.setBackground(bg);
		}
	}
	public void addHeading(int level, String text) {
		log.debug("addHeading: "+text);
		ContentBuilder heading = getHeading(level);
		heading.addText(text);
		heading.addParent();
	}
	public boolean cleanFormatted(int max, String s) {
		HtmlBuilder hb = new HtmlBuilder();
		boolean result = hb.cleanFormatted(max, s);
		HtmlPanel comp = new HtmlPanel(hb.toString());
		setStyle(comp);
		addComponent(comp);
		return result;
	}
	public <C, R> void addTable(AppContext conn, Table<C, R> t, String style) {
		addTable(conn,t,null,style);
	}
	public <C, R> void addTable(AppContext conn, Table<C, R> t, NumberFormat nf,String style) {
		addTable(conn, nf,t);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(uk.ac.ed.epcc.webapp.AppContext, java.lang.Iterable)
	 */
	public void addFormTable(AppContext conn, Iterable<Field> f) {
		addFormTable(conn, f, false);
	}
	public void addFormTable(AppContext conn, Iterable<Field> f,boolean validate) {
		try {
			JPanel panel = listener.getPanel(f, validate);
			content.add(panel,BorderLayout.CENTER);
		} catch (Exception e) {
			conn.error(e,"Error adding panel");
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field)
	 */
	public <I> void addFormLabel(AppContext conn, Field<I> f) {
		if( listener != null){
		SwingField<I> field = listener.getSwingField(f);
		try {
			field.addLabel(content);
		} catch (Exception e) {
			conn.error(e,"Error adding label");
		}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field)
	 */
	public <I,T> void addFormInput(AppContext conn, Field<I> f,T radio_selector) {
		if( listener != null){
		SwingField<I> field = listener.getSwingField(f);
		try {
			field.addInput(content,radio_selector);
		} catch (Exception e) {
			conn.error(e,"Error adding label");
		}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	public void addActionButtons(Form f) {
		if( form_dialog != null ){
			content.add(form_dialog.getActionButtons(f),BorderLayout.EAST);
		}
	}
	/**
	 * 
	 */
	public void setComponentValues() {
		listener.setComponentValues();
	}
	public boolean validate(Form form) throws ValidateException{
		return listener.validate(form);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.util.Collection)
	 */
	public <X> void addList(X[] list) {
		for(X target : list){
			if( target instanceof UIGenerator){
				ContentBuilder item = getPanel("li");
				((UIGenerator)target).addContent(item);
				item.addParent();
			}else{ 
				String text;
				if( target instanceof Identified){
					text =((Identified)target).getIdentifier();
				}else{
					text=target.toString();
				}
				addText(text);
			}
		}
		
		
	}
	public <X> void addObject(X target) {
		if( target instanceof UIProvider){
			((UIProvider)target).getUIGenerator().addContent(this);
		}else if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if(target instanceof XMLPanel) {
			((XMLPanel)target).appendTo(this);
		}else if( target instanceof Identified){
			addText(((Identified)target).getIdentifier());
		}else if( target  instanceof Iterable){
			addList((Iterable)target);
		}else{
			addText(target.toString());
		}
	}
	/* (non-Javadoc)
	 */
	public <X> void addList(Iterable<X> list) {
		for(X target : list){
			if( target instanceof UIGenerator){
				ContentBuilder item = getPanel("li");
				((UIGenerator)target).addContent(item);
				item.addParent();
			}else{ 
				String text;
				if( target instanceof Identified){
					text =((Identified)target).getIdentifier();
				}else{
					text=target.toString();
				}
				addText(text);
			}
		}
		
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.util.Map, java.lang.Iterable)
	 */
	@Override
	public <X> void addList(Map<String, String> attr, Iterable<X> list) {
		addList(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getDetails(java.lang.String)
	 */
	@Override
	public ContentBuilder getDetails(Object summary_text) {
		addObject(summary_text);
		return getPanel();
	}

}