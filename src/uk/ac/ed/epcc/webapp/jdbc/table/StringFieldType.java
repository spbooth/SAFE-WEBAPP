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
package uk.ac.ed.epcc.webapp.jdbc.table;



public class StringFieldType extends FieldType<String> {
    private final int max_length;
	public StringFieldType( boolean can_null,
			String default_val, int max_length) {
		super(String.class, can_null, default_val);
		this.max_length=max_length;
	}
	public int getMaxLEngth(){
		return max_length;
	}
	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitStringFieldType(this);
	}

}