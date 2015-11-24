// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;
import java.util.Map;

/** Policy object to map a {@link Table} to a {@link SimpleXMLBuilder}
 * 
 * @author spb
 * @param <C> 
 * @param <R> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TableXMLFormatter.java,v 1.8 2015/04/02 22:06:56 spb Exp $")

public class TableXMLFormatter<C,R> implements TableFormatPolicy<C, R> {
    protected final SimpleXMLBuilder hb;
    private final NumberFormat nf;
    private boolean table_sections=false;
    private String style;
    public TableXMLFormatter(SimpleXMLBuilder builder,NumberFormat nf,String style){
    	this.hb=builder;
    	this.nf=nf;
    	this.style=style;
    }
    public TableXMLFormatter(SimpleXMLBuilder builder,NumberFormat nf){
    	this(builder,nf,"auto");
    }
    public void setTableSections(boolean val){
    	table_sections=val;
    }
    /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TableFormatPolicy#add(uk.ac.ed.epcc.webapp.content.Table)
	 */
    public  void add(Table<C,R> t){
    	
    	hb.open("table",new String[][]{
				{"class",getTableTag()},
				{"rows",Integer.toString(t.nRows())},
				{"cols",Integer.toString(t.nCols())}
				});
    	String id=t.getId();
    	if( id != null && id.trim().length() > 0){
    		hb.attr("id", id);
    	}
		hb.clean("\n");
		if(t.isPrintHeadings()){
			if(table_sections){
				hb.open("thead");
				hb.clean("\n");
			}
			hb.open( "tr", new String[][]{
					{"count","0"}
			});

			int col=0;
			boolean first_col=true;
			if (t.printKeys()) {
				hb.open("th",new String[][]{
						{"class","key"},
						{"count",Integer.toString(col++)}
				});
				hb.clean( t.getKeyName());
				hb.close();
				first_col=false;
			}

			for (C key : t.getColumNames()) {
				if( first_col ){
					hb.open("th",new String[][]{
							{"class","first"},
							{"count",Integer.toString(col++)}
					});
					if( key instanceof XMLGenerator){
						((XMLGenerator)key).addContent(hb);
					}else{
						hb.clean(key.toString());
					}

					hb.close();
					first_col=false;
				}else{
					hb.open("th",new String[][]{
							{"class","main"},
							{"count",Integer.toString(col++)}
					});
					if( key instanceof XMLGenerator){
						((XMLGenerator)key).addContent(hb);
					}else{
						hb.clean(key.toString());
					}
					hb.close();
				}
			}
			hb.close();
			hb.clean("\n");
			if( table_sections){
				hb.close();
				hb.clean("\n");
			}
		}
		addBody(t);
		hb.close();
		hb.clean("\n");
		
    }

	public final String getTableTag() {
		return style;
	}
    public  void addBody(Table<C,R> t){
		int nrow=1; // after header
		if( table_sections){
			hb.open("tbody");
			hb.clean("\n");
		}
		for (R row_key: t.getRows()) {
			hb.open("tr", new String[][]{
					{"count",Integer.toString(nrow++)}
					});
			if (t.getWarning(row_key)) {
				hb.attr("class","notice");
			}else if (t.getHighlight(row_key)) {
				hb.attr("class","highlight");	
			}
			boolean first_col = true;
			int col=0;
			if (t.printKeys()) {
				hb.open("td", new String[][]{
						{ "class","key"},
						{"count",Integer.toString(col++)}
						});
				addContent(t.getKeyText(row_key));
				hb.close();
				first_col = false;
			}
			for (C key: t.getColumNames()) {
				if (first_col) {
					hb.open("td",new String[][]{
							{"class","first"},
							{"count",Integer.toString(col++)}
							});
		
					first_col = false;
				} else {
					hb.open("td",new String[][]{
							{"class","main"},
							{"count",Integer.toString(col++)}
							});
				}
				addCell(t,key,row_key);
				
				hb.close();
			}
			hb.close();
			hb.clean("\n");
		}
		if( table_sections){
			hb.close();
			hb.clean("\n");
		}
	}
    
    /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TableFormatPolicy#addColumn(uk.ac.ed.epcc.webapp.content.Table, C)
	 */
    public void addColumn(Table<C,R> t , C key){
    	hb.open("table");
    	hb.attr("class", "column");
    	hb.clean("\n");
    	for(R row : t.getRows()){
    		hb.open("tr");
    		if (t.getWarning(row)) {
				hb.attr("class","notice");
			}else if (t.getHighlight(row)) {
				hb.attr("class","highlight");	
			}
    		if( t.printKeys()){
    			hb.open("th");
    			addContent(t.getKeyText(row));
    			hb.close();
    		}
    		hb.open("td");
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
		if( n instanceof Number){
			hb.attr("numeric", "true");
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