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