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