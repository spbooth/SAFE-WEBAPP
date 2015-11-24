package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.inputs.ElapsedSecondInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

public class ElapsedSecondTestInput extends ParseAbstractInputTestCase<Number,ElapsedSecondInput> {

	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("5");
		res.add("5:06");
		res.add("6:00:00");
		return res;
	}

	public Set<String> getBadParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("-17");
		res.add("10:00:00:00");
		res.add("wombat");
		return res;
	}

	public Set<Number> getGoodData() throws Exception {
		HashSet<Number> res = new HashSet<Number>();
		res.add(5L);
		res.add(60L);
		res.add(8000L);
		return res;
	}

	public Set<Number> getBadData() throws Exception {
		HashSet<Number> res = new HashSet<Number>();
		res.add(-3);
		return res;
	}

	public ElapsedSecondInput getInput() throws Exception {
		return new ElapsedSecondInput();
	}

	

}
