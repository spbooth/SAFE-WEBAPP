// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;
import java.util.Map;

/** Policy object to map a {@link Table} to a {@link SimpleXMLBuilder}
 * this uses an alternative format where each row becomes a data record with element names 
 * following the column headings.
 * @author spb
 * @param <C> 
 * @param <R> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TableXMLDataFormatter.java,v 1.1 2014/10/28 11:58:17 spb Exp $")

public class TableXMLDataFormatter<C,R> implements TableFormatPolicy<C, R> {
    /** enclosing element name
	 * 
	 */
	private static final String DATA = "Data";
	/** row element name
	 * 
	 */
	private static final String RECORD = "record";
	protected final SimpleXMLBuilder hb;
    private final NumberFormat nf;
    
   
    public TableXMLDataFormatter(SimpleXMLBuilder builder,NumberFormat nf){
    	this.hb=builder;
    	this.nf=nf;
    }
   
    public  void add(Table<C,R> t){
    	
    	hb.open(DATA);
    	String id=t.getId();
    	if( id != null && id.trim().length() > 0){
    		hb.attr("id", id);
    	}
		hb.clean("\n");
		
		addBody(t);
		hb.close();
		hb.clean("\n");
		
    }

	
    public  void addBody(Table<C,R> t){
		int nrow=1; // after header
		
		for (R row_key: t.getRows()) {
			hb.open(RECORD);
			
			if (t.printKeys()) {
				hb.open(t.getKeyName());
				addContent(t.getKeyText(row_key));
				hb.close();
			}
			for (C key: t.getColumNames()) {
				hb.open(key.toString());
				addCell(t,key,row_key);
				hb.close();
			}
			hb.close();
			hb.clean("\n");
		}
	}
    
    public void addColumn(Table<C,R> t , C key){
    	hb.open(DATA);
    	hb.clean("\n");
    	for(R row : t.getRows()){
    		hb.open(RECORD);
    		if( t.printKeys()){
    			hb.open(t.getKeyName());
    			addContent(t.getKeyText(row));
    			hb.close();
    		}
    		hb.open(key.toString());
    		addCell(t, key, row);
    		hb.close();
    		hb.close();
    		hb.clean("\n");
    	}
    	hb.close();
    	hb.clean("\n");
    }

	@SuppressWarnings("unchecked")
	protected void addCell(Table<C, R> t, C key, R row_key) {
		Map<String,String> my_attr = t.getAttributes(key,row_key);
		for(String a : my_attr.keySet()){
			hb.attr(a,my_attr.get(a));
		}
		Object n = t.get(key,row_key);
		Table.Formatter format = t.getColFormat(key);
		if (format != null) {
			n = format.convert(t,key,row_key,n);
		}
		addContent(n);
	}
	protected void addContent(Object n){
		if( hb instanceof ContentBuilder && n instanceof UIGenerator){
			((UIGenerator)n).addContent(((ContentBuilder)hb));
			return;
		}
		if( n instanceof XMLGenerator){
			((XMLGenerator)n).addContent(hb);
			return;
		}
		if( n instanceof Table){
			add((Table)n);
			return;
		}
		if (nf != null && n instanceof Number) {
			hb.clean(nf.format(n));
			return;
		}
		if (n == null) {
			return;
		}

		hb.clean(n.toString());
	}
}