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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.Date;




public class MillisecondAccessor<R> implements Accessor<Long,R>{
	private final Accessor<Date,R> a;
	public MillisecondAccessor(Accessor<Date,R> a){
		this.a=a;
	}
	public Long getValue(R r) {
		return Long.valueOf(a.getValue(r).getTime());
	}
	public Class<? super Long> getTarget() {
		return Long.class;
	}
	@Override
	public String toString(){
		return "Millis("+a.toString()+")";
	}
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, Long value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
}