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
package uk.ac.ed.epcc.webapp.content;

import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import uk.ac.ed.epcc.webapp.forms.Identified;

/** Policy object to map a {@link Table} to a {@link SimpleXMLBuilder}
 * 
 * @author spb
 * @param <C> 
 * @param <R> 
 *
 */


public class TableXMLFormatter<C,R> implements TableFormatPolicy<C, R> {
    protected final SimpleXMLBuilder hb;
    private final NumberFormat nf;
    private boolean table_sections=false;
    private boolean add_scope=false;
    private boolean allow_span=true;
  
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
    public void setUseScope(boolean val) {
    	add_scope=val;
    }
    public void setAllowSpan(boolean val) {
    	allow_span=val;
    }
    
    /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TableFormatPolicy#add(uk.ac.ed.epcc.webapp.content.Table)
	 */
    public  void add(Table<C,R> t){
    	if( t == null || ! t.hasData()){
    		return;
    	}
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
		Object caption = t.getCaption();
		if( caption != null) {
			hb.open("caption");
			hb.addObject(caption);
			hb.close();
		}
		int nrow=0;
		if(t.isPrintHeadings()){
			if(table_sections){
				hb.open("thead");
				hb.clean("\n");
			}
			if(t.printGroups()) {
				
				hb.open( "tr", new String[][]{
					{"count",Integer.toString(nrow++)}
				});
				int col=0;
				boolean first_col=true;
				if (t.printKeys()) {
					hb.open("th",new String[][]{
						{"class","key"},
						{"count",Integer.toString(col)}
					});
					if( allow_span ) {
						hb.attr("rowspan", "2");
					}
					if( add_scope) {
						hb.attr("scope","col" );
					}
					hb.clean( t.getKeyName());
					hb.close();
					col++;
					first_col=false;
				}
				if( allow_span ) {
					C prev_group = null;
					int span=0;
					for (C key : t.getColumNames()) {
						C group = t.getGroup(key);
						if( prev_group != null && ! prev_group.equals(group)) {
							// output previous group
							addTh(t,prev_group,col,first_col,1,span);
							col+=span;
							span=0;
						}
						if( group == null  ) {
							// no group show as 2 row
							addTh(t, key, col, first_col, 2,1);
							first_col=false;
							col++;
						}else {
							// we have group
							span++;
						}
						prev_group = group;
					}
					if( prev_group != null  ) {
						// output previous group
						addTh(t,prev_group,col,first_col,1,span);
						col+=span;
						span=0;
					}
				}else {
					for (C key : t.getColumNames()) {
						C group = t.getGroup(key);
						if( group == null ) {
							addTh(t,key,col,first_col,1,1);
						}else {
							addTh(t,group,col,first_col,1,1);
						}
						first_col=false;
						col++;
					}
				}
				hb.close();
				hb.clean("\n");
				hb.open( "tr", new String[][]{
					{"count",Integer.toString(nrow++)}
				});
				first_col=true;
				col=0;
				if (t.printKeys()) {
					if( ! allow_span ) {
						hb.open("th",new String[][]{
							{"class","key"},
							{"count",Integer.toString(col)}
						});
					}
					first_col=false;
					col++;
				}
				
				for (C key : t.getColumNames()) {
					C group = t.getGroup(key);
					if( group != null || ! allow_span) {
						// only add elements with a group
						// all others had a rowspan.
						addTh(t, key, col, first_col, 1,1);
						
					}
					first_col=false;
					col++;
				}
				
				hb.close();
				hb.clean("\n");
			}else {
				hb.open( "tr", new String[][]{
					{"count",Integer.toString(nrow++)}
				});

				int col=0;
				boolean first_col=true;
				if (t.printKeys()) {
					hb.open("th",new String[][]{
						{"class","key"},
						{"count",Integer.toString(col++)}
					});
					if( add_scope) {
						hb.attr("scope","col" );
					}
					hb.clean( t.getKeyName());
					hb.close();
					first_col=false;
				}

				for (C key : t.getColumNames()) {
					addTh(t, key, col, first_col,1,1);
					col++;
					first_col=false;
				}
				hb.close();
				hb.clean("\n");
			}
			
			if( table_sections){
				hb.close();
				hb.clean("\n");
			}
		}
		nrow = addBody(nrow,t);
		if( table_sections) {
			nrow = addFooter(nrow, t);
		}
		hb.close();
		hb.clean("\n");
		
    }
    protected void addTh(Table<C, R> t, C key, int col, boolean first_col,int row_span,int col_span) {

    	hb.open("th",new String[][]{
    		{"class",first_col ? "first" : "main"},
    		{"count",Integer.toString(col)}
    	});
    	if( row_span > 1) {
    		hb.attr("rowspan",Integer.toString(row_span));
    	}
    	if( col_span > 1) {
    		hb.attr("colspan",Integer.toString(col_span));
    	}
    	if( add_scope) {
    		hb.attr("scope","col" );
    	}
    	if( key instanceof XMLGenerator){
    		((XMLGenerator)key).addContent(hb);
    	}else{
    		if( t.containsCol(key)) {
    			// this is a normal column
    			String text = t.getCol(key).getName();
    			if( text != null) {
    				hb.clean(text);
    			}else {
    				hb.addObject(key);
    			}
    		}else {
    			// a group
    			hb.addObject(key);
    		}
    		
    	}

    	hb.close();

    }

	public final String getTableTag() {
		return style;
	}
    public  int addBody(int nrow,Table<C,R> t){
		if( table_sections){
			hb.open("tbody");
			hb.clean("\n");
		}
		int n = emitRows(nrow, t, table_sections ? (r)-> ! t.isFooter(r) : null);
		if( table_sections){
			hb.close();
			hb.clean("\n");
		}
		return n;
	}
    public  int addFooter(int nrow,Table<C,R> t){
    	if( ! t.hasFooter()) {
    		return nrow;
    	}
		if( table_sections){
			hb.open("tfoot");
			hb.clean("\n");
		}
		int n = emitRows(nrow, t, table_sections ? (r)-> t.isFooter(r) : null);
		if( table_sections){
			hb.close();
			hb.clean("\n");
		}
		return n;
	}
    /** Emit a series of table rows.
     * 
     * @param nrow    - starting row position
     * @param t		  - table
     * @param filter   - {@link Predicate} to select rows to emait
     * @return
     */
    private int emitRows(int nrow, Table<C, R> t, Predicate<R> filter) {
    	int pos=0;
    	for (R row_key: t.getRows()) {
    		if( filter == null || filter.test(row_key)) {
    			hb.open("tr", new String[][]{
    				{"count",Integer.toString(nrow)}
    			});
    			if (t.getWarning(row_key)) {
    				hb.attr("class","notice");
    			}else if (t.getHighlight(row_key)) {
    				hb.attr("class","highlight");	
    			}
    			int col=0;
    			if (t.printKeys()) {
    				hb.open(t.isPrintHeadings()?"td":"th", new String[][]{
    					{ "class","key"},
    					{"count",Integer.toString(col++)}
    				});
    				addContent(t.getKeyText(row_key));
    				hb.close();
    			}
    			ArrayList<R> row_keys=null;
    			int skip_col=0;
    			for (C key: t.getColumNames()) {
    				Table<C,R>.Col column = t.getCol(key);
    				if( skip_col > 0) {
    					skip_col--;
    					col++;
    				}else {
    					Object n = t.get(key, row_key);
    					String dc=null;
    					int cols=0;

    					if( n instanceof MultiColumn && hb instanceof HtmlPrinter && allow_span) {
    						cols = ((MultiColumn) n).getColumns();
    						if( cols > 1 ) {

    							skip_col = cols-1;
    						}
    						dc = ((MultiColumn) n).getDisplayClass();

    					}
    					if( column.isDedup() && allow_span) {
    						if( row_keys == null ) {
    							row_keys = new ArrayList<R>();
    							for(R row : t.getRows()) {
    								if( filter == null || filter.test(row)) {
    									row_keys.add(row);
    								}
    							}
    						}
    						addTdWithDeDup(row_keys, t, row_key, pos, col, key, n, dc, cols);
    					}else {
    						addTd(t, row_key,col, key, n, dc, cols,1);
    					}
    					col++;
    				}
    			}
    			hb.close();
    			hb.clean("\n");
    			nrow++;
    			pos++;
    		}
    	}
    	return nrow;
    }
	/**
	 * @param t  Table
	 * @param row_key  
	 * @param col   
	 * @param key
	 * @param n
	 * @param dc
	 * @param cols
	 */
	protected void addTd(Table<C, R> t, R row_key, int col, C key, Object n, String dc, int cols,int rows) {
		hb.open("td",new String[][]{
			{"class",col==0?"first":"main"},
			{"count",Integer.toString(col)}
		});
		if( dc != null && hb instanceof HtmlPrinter) {
			((HtmlPrinter)hb).addClass(dc);
		}
		if( cols >1) {
			hb.attr("colspan", Integer.toString(cols));
		}
		if( rows > 1) {
			hb.attr("rowspan", Integer.toString(rows));
		}
		addCell(t,key,row_key, n);
		hb.close();
	}
    protected void addTdWithDeDup(ArrayList<R> row_keys,Table<C, R> t, R row_key, int pos,int col, C key, Object n, String dc, int cols) {
    	int rows=1;
    	if( pos > 0 ) {
			Object prev = t.get(key, row_keys.get(pos-1));
			if( n != null && prev != null && n.equals(prev)) {
				hb.clean("\t\t");
				return;  //supress duplicate
			}
		}
		for( int i=pos+1; i< row_keys.size();i++) {
			Object next= t.get(key, row_keys.get(i));
			if( n != null && next != null && n.equals(next)) {
				rows++;
			}else {
				break;
			}
		}
		addTd(t, row_key, col, key, n, dc, cols, rows);
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
    			if( add_scope ) {
    				hb.attr("scope", "row");
    			}
    			addContent(t.getKeyText(row));
    			hb.close();
    		}
    		hb.open("td");
    		addCell(t, key, row, t.get(key, row));
    		hb.close();
    		hb.close();
    		hb.clean("\n");
    	}
    	hb.close();
    	hb.clean("\n");
    }

	@SuppressWarnings("unchecked")
	protected void addCell(Table<C, R> t, C key, R row_key, Object n) {
		Map<String,String> my_attr = t.getAttributes(key,row_key);
		for(String a : my_attr.keySet()){
			hb.attr(a,my_attr.get(a));
		}
		
		Table.Formatter format = t.getColFormat(key);
		if (format != null) {
			n = format.convert(t,key,row_key,n);
		}
		addContent(n);
	}
	protected void addContent(Object n){
		if( hb instanceof ContentBuilder && n instanceof UIProvider){
			((UIProvider)n).getUIGenerator().addContent(((ContentBuilder)hb));
			return;
		}
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
		if( n instanceof Principal) {
			hb.clean(((Principal)n).getName());
			return;
		}
		if( n instanceof Identified) {
			hb.clean(((Identified)n).getIdentifier());
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
		if( hb instanceof ContentBuilder ) {
			((ContentBuilder)hb).addObject(n);
			return;
		}
		hb.clean(n.toString());
	}
}