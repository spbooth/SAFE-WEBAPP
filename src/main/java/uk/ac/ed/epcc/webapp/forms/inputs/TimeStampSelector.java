package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Date;

import uk.ac.ed.epcc.webapp.model.data.BoundedSelector;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

public class TimeStampSelector implements Selector<TimeStampInput>, BoundedSelector<Date, TimeStampInput> {

	private final long res;
	private final Date min;
	private final Date max;
	public TimeStampSelector(long res, Date min,Date max) {
		this.res=res;
		this.min=min;
		this.max=max;
	}

	public TimeStampSelector(Date min,Date max) {
		this(1000L,min,max);
	}
	@Override
	public TimeStampInput getInput() {
		TimeStampInput input = new TimeStampInput(res);
		if( min != null ) {
			input.setMin(min);
		}
		if( max != null ) {
			input.setMax(max);
		}
		return input;
	}


	@Override
	public BoundedSelector<Date, TimeStampInput> narrowBounds(Date min, Date max) {
		if( min == null ) {
			min=this.min;
		}else {
			if( this.min != null && this.min.after(min)) {
				min = this.min;
			}
		}
		if( max == null ) {
			max = this.max;
		}else {
			if( this.max != null && this.max.before(max)) {
				max = this.max;
			}
		}
		return new TimeStampSelector(res, min, max);
	}

}
