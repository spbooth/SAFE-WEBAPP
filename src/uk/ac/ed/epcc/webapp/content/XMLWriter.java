package uk.ac.ed.epcc.webapp.content;

import java.io.IOException;
import java.io.Writer;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

public class XMLWriter extends AbstractXMLBuilder {
	private Writer writer;
	public XMLWriter(Writer w) {
		writer = w;
	}

	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		return new XMLPrinter(this);
	}

	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new ConsistencyError("Error closing writer", e);
		}
		return null;
	}

	public SimpleXMLBuilder getParent() {
		
		return null;
	}

	@Override
	protected void append(CharSequence s) {
		try {
			writer.append(s);
		} catch (IOException e) {
			throw new ConsistencyError("Error writting text", e);
		}
	}

	@Override
	protected void append(char s) {
		try {
			writer.append(s);
		} catch (IOException e) {
			throw new ConsistencyError("Error writting text", e);
		}
	}

	/** Close the existing {@link Writer} an redirect output
	 * to a new {@link Writer}
	 * 
	 * @param w
	 * @throws IOException 
	 */
    public void setWriter(Writer w) throws IOException{
    	writer.close();
    	writer=w;
    }

}
