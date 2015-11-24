package uk.ac.ed.epcc.webapp;
/** A Number that represents an average value.
 * 
 * @author spb
 *
 */
public class AverageValue extends Number{
	public final  double sum;
	public final long count;
	public AverageValue(double sum,long count) {
		this.sum=sum;
		this.count=count;
	}
	@Override
	public double doubleValue() {
		if( count == 0L){
			return 0.0;
		}
		return sum / (double) count;
	}
	@Override
	public float floatValue() {
		return (float) doubleValue();
	}
	@Override
	public int intValue() {
		return (int) doubleValue();
	}
	@Override
	public long longValue() {
		return (long) doubleValue();
	}
	public double getSum(){
		return sum;
	}
	public long getCount(){
		return count;
	}

}
