package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;

public class IntegerBoundedSelector implements BoundedSelector<Integer, IntegerInput> {

	private Integer min,max;
	public IntegerBoundedSelector(Integer min,Integer max) {
		this.min=min;
		this.max=max;
	}

	@Override
	public IntegerInput getInput() {
		IntegerInput i = new IntegerInput();
		if( min != null) {
			i.setMin(min);
		}
		if( max != null ) {
			i.setMax(max);
		}
		return i;
	}

	@Override
	public IntegerBoundedSelector narrowBounds(Integer min, Integer max) {
		Integer new_min=this.min;
		Integer new_max=this.max;
		if( min != null) {
			if( new_min == null || new_min.intValue() > min.intValue()) {
				new_min=min;
			}
		}
		if( max != null ) {
			if( new_max == null || new_max.intValue() > max.intValue()) {
				new_max=max;
			}
		}
		
		return new IntegerBoundedSelector(new_min, new_max);
	}

}
