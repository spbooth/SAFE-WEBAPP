package uk.ac.ed.epcc.webapp.charts;

/** Specify custom range bounds
 * 
 * Null values indicate the values should be auto-computed
 * 
 * @author Stephen Booth
 *
 */
public class DataRange {
	public DataRange(Number min, Number max) {
		this.min = min;
		this.max = max;
		if( min != null && max != null && max.doubleValue()< min.doubleValue()) {
			throw new IllegalArgumentException("Reversed bounds");
		}
	}
	private final Number min;
	private final Number max;
	public Number getMin() {
		return min;
	}
	public Number getMax() {
		return max;
	}
	

}
