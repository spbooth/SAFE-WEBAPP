package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

public class DateSelector implements Selector<DateInput> {

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
		DateSelector other = (DateSelector) obj;
		if (res != other.res)
			return false;
		return true;
	}
	private final long res;
	
	public DateSelector(long res) {
		this.res=res;
	}

	public DateSelector() {
		this(1000L);
	}
	@Override
	public DateInput getInput() {
		DateInput input = new DateInput(res);
		return input;
	}


}
