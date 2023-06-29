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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.jdbc.filter.GetListFilterVisitor;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.session.SessionService;
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
		//content.setMaximumSize(new Dimension(800, 10000));
		content.setLayout(new BoxLayout(content ,BoxLayout.PAGE_AXIS));
		content.setAlignmentX(Component.CENTER_ALIGNMENT);
		
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
		//content.setMaximumSize(new Dimension(1024, 102400));
		content.setLayout(new BoxLayout(content ,BoxLayout.PAGE_AXIS));
		content.setAlignmentX(Component.CENTER_ALIGNMENT);
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
		if( type_classes != null && type_classes.length > 0 ) {
			this.type_class=type_classes[0];
		}else {
			this.type_class="default";
		}
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
		private final boolean in_line;
		public XMLPanel(SwingContentBuilder parent,ExtendedXMLBuilder nested,boolean in_line) {
			this.parent = parent;
			this.nested=nested;
			this.in_line=in_line;
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
			builder.addHtml(nested.toString());
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
		
		
		if( n instanceof Number ){
			dest.addText(nf.format((Number)n));
			return;
		}
		dest.addObject(n);
		
    }

	@Override
	public void addButton(AppContext c,String text, FormResult action) {
		log.debug("add button");
		JButton button = new JButton(text);
		
		button.addActionListener(new FormResultActionListener(conn, frame, action));
		addComponent(button);
	}

	@Override
	public void addButton(AppContext c, String text, String hover, FormResult action){
		log.debug("add button");
		JButton button = new JButton(text);
		button.setToolTipText(hover);
		button.addActionListener(new FormResultActionListener(conn, frame, action));
		addComponent(button);
	}
	@Override
	public void addLink(AppContext c,String text, FormResult action) {
		log.debug("add link");
		addButton(c,text, action);

	}
	@Override
	public void addLink(AppContext c,String text, String hover,FormResult action) {
		log.debug("add link");
		addButton(c,text, hover,action);

	}
	@Override
	public void addLink(AppContext c,String text, String hover,String style,FormResult action) {
		log.debug("add link");
		addButton(c,text, hover,action);

	}
	@Override
	public <C,R> void addTable(AppContext conn,Table<C,R> t) {
		addTable(conn,null,t);
	}
	@Override
	public <C,R> void addTable(AppContext conn,NumberFormat nf,Table<C,R> t) {
		log.debug("add table");
		JPanel table=new JPanel(new GridBagLayout());
		setStyle(table);
		addComponent(new JScrollPane(table));
		GridBagConstraints c = new GridBagConstraints();
		SwingContentBuilder inner=new SwingContentBuilder(conn, table, frame);
		inner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inner.add_param=c;
		c.ipadx=4;
		c.ipady=4;
		c.fill=GridBagConstraints.BOTH;
		c.insets=new Insets(1, 1, 1, 1);
		c.weightx=0.9;
		c.gridx=0;
		c.gridy=0;
		//first the headings
		if(t.isPrintHeadings()){
			Color old = inner.bg;
			inner.bg=Color.LIGHT_GRAY;
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
			inner.bg=old;
		}
		// then the body
		for(R row : t.getRows()){

			c.gridx=0;
			if( t.printKeys()){
				inner.addObject(t.getKeyText(row));
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
	
	
	@Override
	public ExtendedXMLBuilder getText() {
		log.debug("getText");
		return new XMLPanel(this,new HtmlBuilder(),false);
	}
	@Override
	public ExtendedXMLBuilder getSpan() {
		log.debug("getSpan");
		return new XMLPanel(this,new HtmlBuilder(),true);
	}

	@Override
	public ContentBuilder getHeading(int level) {
		log.debug("getHeading");
		SwingContentBuilder builder = new SwingContentBuilder(this,"h"+level,"");
		//builder.content.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		builder.content.setLayout(new FlowLayout(FlowLayout.LEADING));
		builder.content.setAlignmentX(Component.CENTER_ALIGNMENT);
		return builder;
	}
	@Override
	public ContentBuilder getPanel(String ... type)
			throws UnsupportedOperationException {
		log.debug("getPanel");
		
		SwingContentBuilder panel = new SwingContentBuilder(this,"div",type);
		//panel.content.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel.content.setLayout(new BoxLayout(panel.content, BoxLayout.PAGE_AXIS));
		//panel.content.setAlignmentX(Component.RIGHT_ALIGNMENT);
		return panel;
	}
	
	@Override
	public ContentBuilder addParent() throws UnsupportedOperationException {
		log.debug("addParent");
		if( parent == null){
			throw new UnsupportedOperationException("No parent");
		}
		content.validate();
		parent.addComponent(content);
		return parent;
	}
	
	@Override
	public <C, R> void addColumn(AppContext conn, Table<C, R> t, C col) {
		JPanel table=new JPanel(new GridBagLayout());
		setStyle(table);
		addComponent(new JScrollPane(table));
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

		
		// then the body
		for(R row : t.getRows()){
			c.gridx=0;
			if( t.printKeys()){
				Color old = inner.bg;
				inner.bg=Color.LIGHT_GRAY;
				inner.addText(t.getKeyText(row).toString());
				c.gridx++;
				inner.bg=old;
			}
			
			addCell(t,col,row,null,inner);
			c.gridx++;
			
			c.gridy++;
		}
		addComponent(inner.getComponent());
	}
	@Override
	public void addText(String text) {
		if( text != null ) {
			log.debug("addText: "+text);
			text=text.trim();
			
			JLabel ta = new JLabel(text);
			//ta.setLineWrap(true);
			ta.setOpaque(true);
			//ta.setMaximumSize(new Dimension(800, 800));
			ta.setAlignmentX(Component.CENTER_ALIGNMENT);
			//label.setMaximumSize(new Dimension(800, 10000));
			setStyle(ta);
			//ta.setBorder(BorderFactory.createLineBorder(Color.GREEN));
			addComponent(ta);
		}
	}
	public void addHtml(String text) {
		if( text != null ) {
			log.debug("addHtml: "+text);
			JLabel label = new JLabel("<html>"+text+"</html>");
			label.setOpaque(true);
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			label.setMaximumSize(new Dimension(800, 800));
			setStyle(label);
			//label.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
			addComponent(label);
		}
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
	@Override
	public void addHeading(int level, String text) {
		log.debug("addHeading: "+text);
		ContentBuilder heading = getHeading(level);
		heading.addText(text);
		heading.addParent();
	}
	@Override
	public boolean cleanFormatted(int max, String s) {
		HtmlBuilder hb = new HtmlBuilder();
		boolean result = hb.cleanFormatted(max, s);
		HtmlPanel comp = new HtmlPanel(hb.toString());
		setStyle(comp);
		//comp.setBorder(BorderFactory.createLineBorder(Color.RED));
		addComponent(comp);
		return result;
	}
	@Override
	public <C, R> void addTable(AppContext conn, Table<C, R> t, String style) {
		addTable(conn,t,null,style);
	}
	@Override
	public <C, R> void addTable(AppContext conn, Table<C, R> t, NumberFormat nf,String style) {
		addTable(conn, nf,t);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(uk.ac.ed.epcc.webapp.AppContext, java.lang.Iterable)
	 */
	@Override
	public void addFormTable(AppContext conn, Iterable<Field> f) {
		addFormTable(conn, f, false);
	}
	public void addFormTable(AppContext conn, Iterable<Field> f,boolean validate) {
		try {
			JPanel panel = listener.getPanel(f, validate);
			content.add(panel,BorderLayout.CENTER);
		} catch (Exception e) {
			getLogger().error("Error adding panel",e);
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field)
	 */
	@Override
	public <I,T> void addFormLabel(AppContext conn, Field<I> f,T item) {
		if( listener != null){
		SwingField<I> field = listener.getSwingField(f);
		try {
			field.addLabel(content);
		} catch (Exception e) {
			getLogger().error("Error adding label",e);
		}
		}
	}
	@Override
	public <I,T> void addFormError(AppContext conn, Field<I> f,T item) {
		if( listener != null){
		SwingField<I> field = listener.getSwingField(f);
		try {
			field.addError(content);
		} catch (Exception e) {
			getLogger().error("Error adding label",e);
		}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field)
	 */
	@Override
	public <I,T> void addFormInput(AppContext conn, Field<I> f,T radio_selector) {
		if( listener != null){
		SwingField<I> field = listener.getSwingField(f);
		try {
			field.addInput(content,radio_selector);
		} catch (Exception e) {
			getLogger().error("Error adding label",e);
		}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void addActionButtons(Form f,String legend,Set<String> actions) {
		if( form_dialog != null ){
			content.add(form_dialog.getActionButtons(f,actions),BorderLayout.EAST);
		}
	}
	@Override
	public void addActionButton(Form f, String name) {
		content.add(form_dialog.getActionButton(f,name));
		
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
	@Override
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
	@Override
	public <X> void addObject(X target) {
		if( target instanceof UIProvider){
			((UIProvider)target).getUIGenerator().addContent(this);
		}else if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if(target instanceof XMLPanel) {
			((XMLPanel)target).appendTo(this);
		}else if( target instanceof SwingContentBuilder) {
			SwingContentBuilder sw = (SwingContentBuilder)target;
			content.add(sw.content);
		}else if( target instanceof Identified){
			addText(((Identified)target).getIdentifier());
		}else if( target instanceof Principal) {
			addText(((Principal)target).getName());
		}else if( target  instanceof Iterable){
			addList((Iterable)target);
		}else if( target instanceof Object[]) {
			addList((Object [])target);
		}else{
			addText(target.toString());
		}
	}
	public <X> boolean canAdd(X target) {
		if( target instanceof UIProvider){
			return true;
		}else if( target instanceof UIGenerator){
			return true;
		}else if(target instanceof XMLPanel) {
			return true;
		}else if( target instanceof SwingContentBuilder) {
			return true;
		}else if( target instanceof Identified){
			return true;
		}else if( target instanceof Principal) {
			return true;
		}else if( target  instanceof Iterable){
			return true;
		}else if( target instanceof Object[]) {
			return true;
		}else{
			return false;
		}
	}
	/* (non-Javadoc)
	 */
	@Override
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
		return this;
	}
	@Override
	public void closeDetails() {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addNumberedList(int, java.lang.Iterable)
	 */
	@Override
	public <X> void addNumberedList(int start, Iterable<X> list) {
		addList(list);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addImage(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, uk.ac.ed.epcc.webapp.forms.result.ServeDataResult)
	 */
	@Override
	public void addImage(AppContext conn, String alt, String hover, Integer width, Integer height,
			ServeDataResult image) {
		try {
		SessionService sess = conn.getService(SessionService.class);
		MimeStreamData data = image.getProducer().getData(sess, image.getArgs());
		BufferedImage bi = ImageIO.read(data.getInputStream());
				
		JLabel label = new JLabel(new ImageIcon(bi));
		addComponent(label);
		}catch(Exception t) {
			getLogger().error("Error making image",t);
		}
	}
	/**
	 * @return
	 */
	public Logger getLogger() {
		return this.conn.getService(LoggerService.class).getLogger(getClass());
	}

}