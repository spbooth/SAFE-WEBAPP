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
package uk.ac.ed.epcc.webapp.model.data.convert;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.LazyObjectCreator;



public class LazyTypeConverter<T,D,F extends TypeConverter<T,D>> extends LazyObjectCreator<F> implements TypeConverter<T, D>{

	public LazyTypeConverter(AppContext c, F result) {
		super(c, result);
	}

	public LazyTypeConverter(AppContext c, Class<? super F> clazz, String tag) {
		super(c, clazz, tag);
	}

	public Class<? super T> getTarget() {
		return getInner().getTarget();
	}

	public T find(D o) {
		return getInner().find(o);
	}

	public D getIndex(T value) {
		return getInner().getIndex(value);
	}

}