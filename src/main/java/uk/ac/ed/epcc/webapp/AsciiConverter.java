//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;
import java.text.Collator;

public class AsciiConverter {

	private final Collator coll; 
	private final boolean allow_7bit;
	public AsciiConverter(){
		this(true);
	}
	public AsciiConverter(boolean allow_7bit){
		this.allow_7bit=allow_7bit;
	   coll= Collator.getInstance();
	   coll.setStrength(Collator.PRIMARY);
	}
	static final char target[] = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	public char convert(char input){
		if( allow_7bit && input < 128){
			// 7 bit ascii
			return input;
		}
		for(char c : target){
			if( 0 == coll.compare(Character.toString(c),Character.toString(input))){
				return c;
			}
		}
		return ' ';
	}
	public String convert(String input){
		StringBuilder sb = new StringBuilder();
		if( input != null ){
			for( char c : input.toCharArray()){
				sb.append(convert(c));
			}
		}
		return sb.toString();
	}
}