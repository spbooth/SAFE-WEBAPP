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
package uk.ac.ed.epcc.webapp.content;


/** XMLGenerator that maps spaces onto non-breaking spaces if
 * the generator is actually an ExtendedXMLGenerator.
 * 
 * @author spb
 *
 */
public class HtmlSpaceGenerator implements XMLGenerator {
	@Override
	public int hashCode() {
		return value.hashCode();
	}


	@Override
	public String toString() {
		return value;
	}


	public HtmlSpaceGenerator(String value) {
		super();
		this.value = value;
	}


	private final String value;
	 

	
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ExtendedXMLBuilder){
			 String input = value;	
				
				for(int i=0 ;i< input.length(); i++){
					char c= input.charAt(i);
					if(  c == ' '){
						((ExtendedXMLBuilder)builder).nbs();
					}else{
						builder.clean(c);
					}
				}
		}else{
			builder.clean(value);
		}
		
		return builder;
	}

}