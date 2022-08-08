//| Copyright - The University of Edinburgh 2016                            |
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

import java.text.NumberFormat;
import java.util.List;

/** format a table as text.
 * 
 * @author spb
 *
 */
public class TextTableFormatter<C,R> {
	/**
	 * @param table
	 */
	public TextTableFormatter(Table<C, R> table) {
		this(null,table);
	}
	public TextTableFormatter(NumberFormat nf,Table<C, R> table) {
		super();
		this.nf=nf;
		this.table = table;
		 List<C> cols = table.getCols();
			int ncols = cols.size();	
			if( table.printKeys()){
				ncols++;
			}
			wid = new int[ncols];
			// Header width
			int pos = 0;
			if( table.printGroups()) {
				if( table.printKeys()) {
					wid[pos]=0;
					pos++;
				}
				for( C name : cols){
					C group = table.getGroup(name);
					if( group != null) {
						wid[pos++]=group.toString().length();
					}else {
						wid[pos++]=0;
					}
				}
			}
			pos=0;
			// normal heading
			if( table.printKeys()){
				wid[pos] = table.getKeyName().length();
				pos++;
			}
			for(  C name : cols){
				String text = table.getCol(name).getName();
				if( text == null ) {
					text = name.toString();
				}
				wid[pos]=max(wid[pos],text.length());
				pos++;
			}
			for( R row : table.getRows()){
				pos = 0;
			
				if( table.printKeys() ){
					wid[pos] = max(table.getKeyText(row).toString().length(),wid[pos]);
					pos++;
				}
				for( C col : cols){
					wid[pos] = max( table.getText(col, row).length(), wid[pos]);
					pos++;
				}
			}
		    // Now have the width of each col
	}

	private final Table<C,R> table;
	private final NumberFormat nf;
	private int wid[];
	
	public void add(StringBuilder sb){
	   addBar(sb);
	   addHeader(sb);
	   addBar(sb);
	   for(R row : table.getRows()){
		   addRow(sb, row);
	   }
	   addBar(sb);
		
	}
	
	private void addline(StringBuilder sb, int len, char c){
		for(int i=0;i<len;i++){
			sb.append(c);
		}
	}
	
	private void addBar(StringBuilder sb){
		int pos=0;
		sb.append("+");
		if( table.printKeys()){
			addline(sb, wid[pos++],'-');
			sb.append("++");
		}
		while(  pos < wid.length ){
			addline(sb,wid[pos++],'-');
			sb.append('+');
		}
		sb.append("\n");
	}
	private void addHeader(StringBuilder sb){
		int pos=0;
		if( table.printGroups()) {
			sb.append("|");
			if( table.printKeys()){
				leftJustify(sb, "", wid[pos++]);
				sb.append("||");
			}
			for( C col : table.getColumNames()){
				C group = table.getGroup(col);
				String text = "";
				if( group != null) {
					text = group.toString();
				}
				rightJustify(sb,text,wid[pos++]);
				sb.append("|");
			}
			sb.append("\n");
			pos=0;
		}
		sb.append("|");
		if( table.printKeys()){
			leftJustify(sb, table.getKeyName(), wid[pos++]);
			sb.append("||");
		}
		for( C col : table.getColumNames()){
			String text = table.getCol(col).getName();
			if( text == null ) {
				text = col.toString();
			}
			rightJustify(sb,text,wid[pos++]);
			sb.append("|");
		}
		sb.append("\n");
	}
	private void addRow(StringBuilder sb,  R row){
		int pos=0;
		sb.append("|");
		if( table.printKeys()){
			leftJustify(sb, table.getKeyText(row).toString(), wid[pos++]);
			sb.append("||");
		}
		for( C col : table.getColumNames()){
			rightJustify(sb,table.getText(nf,col, row),wid[pos++]);
			sb.append("|");
		}
		sb.append("\n");
	}
	private int max(int a, int b){
		if( a > b ){
			return a;
		}else{
			return b;
		}
	}
	private void leftJustify(StringBuilder sb, String val, int len){
		sb.append(val);
		for(int i=val.length(); i < len ; i++){
			sb.append(" ");
		}
	}
	private void rightJustify(StringBuilder sb, String val, int len){
		for(int i=val.length(); i < len ; i++){
			sb.append(" ");
		}
		sb.append(val);
	}
}
