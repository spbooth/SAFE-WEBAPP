package uk.ac.ed.epcc.webapp.forms.inputs;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;





import uk.ac.ed.epcc.webapp.forms.inputs.PercentInput;

public class PercentInputTestCase extends NumberInputTestCase<Double, PercentInput> {

	@Override
	protected String getExpectedType() {
		return null;
	}

	public Set<String> getGoodParseData() {
		Set<String> good=new HashSet<String>();
		good.add("0%");
		good.add("100%");
		good.add("55%");
		return good;
	}

	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<String>();
		bad.add("boris");
		bad.add("110");
		bad.add("-7");
		bad.add("110%");
		bad.add("-7%");
		return bad;
	}

	public Set<Double> getGoodData() throws Exception {
		Set<Double> good = new HashSet<Double>();
		good.add(0.0);
		good.add(1.0);
		good.add(0.75);
		return good;
	}

	public Set<Double> getBadData() throws Exception {
		Set<Double> bad = new HashSet<Double>();
		bad.add(-0.3);
		bad.add(7.0);
		return bad;
	}

	public PercentInput getInput() throws Exception {
		
		return new PercentInput();
	}
	
	@org.junit.Test
	public void testRange() throws Exception{
		PercentInput input = getInput();
		assertEquals("0",input.formatRange(input.getMin()));
		assertEquals("100",input.formatRange(input.getMax()));
		assertEquals("1",input.formatRange(input.getStep()));
	}

}
