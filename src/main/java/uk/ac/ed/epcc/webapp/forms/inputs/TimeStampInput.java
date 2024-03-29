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
package uk.ac.ed.epcc.webapp.forms.inputs;

public class TimeStampInput extends AbstractDateInput implements HTML5Input{

	@Override
	protected int getHintIndex() {
		return 1;
	}

	@Override
	public String[] getFormats() {
		// A html5 picker input will use a timestamp including the T
		return new String[] {"yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy-MM-dd'T'HH:mm","yyyy-MM-dd HH", "yyyy-MM-dd" };
	}

	public TimeStampInput(long resolution) {
		super(resolution);
	}

	public TimeStampInput(){
		super();
	}
	@Override
	public String getType(){
		return "datetime-local";
	}

	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitTimestampInput(this);
	}
}