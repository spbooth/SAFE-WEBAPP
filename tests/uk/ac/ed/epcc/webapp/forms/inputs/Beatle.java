//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

/**
 * @author spb
 *
 */

public class Beatle {

	
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if( obj == null ){
			return false;
		}
		return obj.getClass().equals(getClass());
	}
	public Beatle(){
		
	}
	public static class Ringo extends Beatle{
		
	}
	public static class Paul extends Beatle{
		
	}
	public static class John extends Beatle{
		
	}
	public static class George extends Beatle{
		
	}
	public static class Mick{
		
	}
}