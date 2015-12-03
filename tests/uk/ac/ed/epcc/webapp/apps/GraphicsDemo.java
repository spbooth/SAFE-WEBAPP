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
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.swing.SwingContentBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class GraphicsDemo extends GraphicsCommand {

	public GraphicsDemo(AppContext conn) {
		super(conn);
	}

	@Override
	protected JComponent getMainPanel(JFrame frame,SessionService session_service) {
		SwingContentBuilder builder = new SwingContentBuilder(getContext(),frame);
		builder.addHeading(2, "A Level 2 heading");
		builder.addHeading(3, "A level 3 heading");
		builder.addText("A paragraph of text");
		builder.addText("A very very very long line of text, loren ipsum la la la we all live in a yellow submarine a yellow submarine a yellow submarine");
		builder.cleanFormatted(40, "A line\nAnother line\nYet anouther line");
		Table t = new Table();
		t.put("First Col", "A", "A");
		t.put("First Col", "B", "B");
		t.put("Second Col", "A", "AA");
		builder.addTable(getContext(), t);
		return builder.getComponent();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppContext conn = new AppContext();
		LinkedList<String> data = new LinkedList<String>();
		CommandLauncher.setupContext(args, data, conn);
		GraphicsDemo test = new GraphicsDemo(conn);
		test.run(data);
	}

}