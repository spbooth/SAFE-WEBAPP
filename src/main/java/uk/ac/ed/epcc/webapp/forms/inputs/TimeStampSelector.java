package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

public class TimeStampSelector implements Selector<TimeStampInput> {

	private final long res;
	
	public TimeStampSelector(long res) {
		this.res=res;
	}

	public TimeStampSelector() {
		this(1000L);
	}
	@Override
	public TimeStampInput getInput() {
		TimeStampInput input = new TimeStampInput(res);
		return input;
	}


}
