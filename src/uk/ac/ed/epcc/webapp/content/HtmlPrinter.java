//| Copyright - The University of Edinburgh 2015                            |
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


/** Class for building HTML fragments.
 * essentially a StringBuilder with additional methods to do
 * html quoting so that the input can't corrupt the html structure or insert
 * unwanted content like scripts. 
 * 
 * @author spb
 *
 */
public class HtmlPrinter extends XMLPrinter implements ExtendedXMLBuilder{

	public HtmlPrinter() {
		super();
	}
	public HtmlPrinter(HtmlPrinter parent){
		super(parent);
	}

	public void nbs() {
		endOpen();
		if( getValidXML()){
			// &nbs; is an XML entity reference and only valid if defined.
			// we substitute the unicode equivalent for valid XML
			sb.append("&#160;");
		}else{
			sb.append("&nbsp;");
		}
	}

	/** Convert a string into a html fragment with matching linebreaks
	 * also comparing the line lengths with a maximum value
	 * @param max
	 * @param s
	 * @return true if lines are long
	 */
	public final boolean longLines(int max, String s) {
		boolean is_long=false;
		open("p");
		attr("class", "longlines");
		clean("\n");
		for(String line : s.split("\n")){
			String tmp = line.replace("\t", "        ");
			if( tmp.length() > max){
				is_long = true;
			}
			clean(line);
			br();
		}
		close();
		clean("\n");
		return is_long;
	}

	public final boolean cleanFormatted(int max, String s) {
		HtmlBuilder hb = new HtmlBuilder();
		if( hb.longLines(max,s)){
			append(hb);
			return true;
		}else{
			open("pre");
			clean(s);
			close();
			return false;
		}
	}

	public void br() {
		endOpen();
		sb.append("<br/>\n");
		
	}

}