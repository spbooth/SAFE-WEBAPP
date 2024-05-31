package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

public class TimeStampSelector implements Selector<TimeStampInput> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (res ^ (res >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeStampSelector other = (TimeStampSelector) obj;
		if (res != other.res)
			return false;
		return true;
	}
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
