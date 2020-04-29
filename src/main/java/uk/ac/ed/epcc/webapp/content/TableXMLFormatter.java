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
import java.util.Map;

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
				if( add_scope) {
					hb.attr("scope","col" );
				}
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
					if( add_scope) {
						hb.attr("scope","col" );
					}
					if( key instanceof XMLGenerator){
						((XMLGenerator)key).addContent(hb);
					}else{
						hb.clean(t.getCol(key).getName());
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
						hb.clean(t.getCol(key).getName());
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
			int skip_col=0;
			for (C key: t.getColumNames()) {
				if( skip_col > 0) {
					skip_col--;
					col++;
				}else {
					Object n = t.get(key, row_key);
					String dc=null;
					int cols=0;
					
					if( n instanceof MultiColumn && hb instanceof HtmlPrinter) {
						cols = ((MultiColumn) n).getColumns();
						if( cols > 1 ) {
							
							skip_col = cols-1;
						}
						dc = ((MultiColumn) n).getDisplayClass();
						
					}
					addTd(t, row_key, nrow-1,col, key, n, dc, cols,1);
					col++;
				}
			}
			hb.close();
			hb.clean("\n");
			nrow++;
		}
		if( table_sections){
			hb.close();
			hb.clean("\n");
		}
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
	protected void addTd(Table<C, R> t, R row_key, int row,int col, C key, Object n, String dc, int cols,int rows) {
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