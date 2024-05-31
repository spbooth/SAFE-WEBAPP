//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.forms.stateful;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.FieldConstraint;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;
import uk.ac.ed.epcc.webapp.validation.MaxValueValidator;
import uk.ac.ed.epcc.webapp.validation.MinValueValidator;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public class ConstrainedFactory extends DataObjectFactory<ConstrainedFactory.ConstrainedObject> {

	/**
	 * 
	 */
	private static final String MAX = "Max";
	/**
	 * 
	 */
	private static final String VALUE = "Value";
	/**
	 * 
	 */
	private static final String MIN = "Min";

	public ConstrainedFactory(AppContext conn) {
		super();
		setContext(conn, "Constrained");
	}
	
	@Override
	protected Map<String, FieldConstraint> getFieldConstraints() {
		Map<String, FieldConstraint> cst = super.getFieldConstraints();
		cst.put(VALUE,new FieldConstraint<Integer>() {
			@Override
			public boolean requestMultiStage(Map fixtures) {
				if( fixtures.containsKey(MIN) && fixtures.containsKey(MAX)) {
					return false;
				}
				return true;
			}
			@Override
			public FieldValidationSet<Integer> validationSet(FieldValidationSet<Integer> original, Map<String,Object> fixtures) {
				Integer min = (Integer) fixtures.get(MIN);
				if( min != null ) {
					original.addValidator(new MinValueValidator<Integer>(min));
				}
				Integer max = (Integer) fixtures.get(MAX);
				if( max != null) {
					original.addValidator(new MaxValueValidator<Integer>(max));
				}
				return FieldConstraint.super.validationSet(original, fixtures);
			}
			@Override
			public FormValidator getFormValidator() {
				return new FormValidator() {
					
					@Override
					public void validate(Form f) throws ValidateException {
						Integer min = (Integer) f.get(MIN);
						Integer value = (Integer) f.get(VALUE);
						Integer max = (Integer) f.get(MAX);
						if( value.intValue() < min.intValue()) {
							throw new ValidateException("Value too small");
						}
						if( value.intValue() > max.intValue()) {
							throw new ValidateException("Value too large");
						}
						
					}
				};
			}
			

			@Override
			public Integer defaultValue( Integer original,Map<String,Object> fixtures) {
				Integer min = (Integer) fixtures.get(MIN);
				Integer max = (Integer) fixtures.get(MAX);
				Integer val = (Integer) original;
				
				if( original != null && min != null && val.intValue() < min.intValue()) {
					return min;
				}

				if( original != null && max != null && val.intValue() > max.intValue()) {
					return max;
				}
				return original;
			}

			
		});
		return cst;
	}

	@Override
	protected ConstrainedObject makeBDO(Record res) throws DataFault {
		return new ConstrainedObject(res);
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(MIN, new IntegerFieldType());
		spec.setField(VALUE, new IntegerFieldType());
		spec.setField(MAX, new IntegerFieldType());
		
		return spec;
	}

	public class ConstrainedObject extends DataObject{

		/**
		 * @param r
		 */
		protected ConstrainedObject(Record r) {
			super(r);
		}
		
	}

	@Override
	public void customiseForm(Form f) {
		// Use form validator to validate  min against max
		// we want these in one level
		f.addValidator(new FormValidator() {
			
			@Override
			public void validate(Form f) throws ValidateException {
				Integer min = (Integer) f.get(MIN);
				Integer max = (Integer) f.get(MAX);
				
				if( ! (min.intValue() < max.intValue())) {
					throw new ValidateException("Min must be less than Max");
				}
				
			}
		});
	}

	@Override
	protected Set<String> getOptional() {
		
		return new HashSet<String>();
	}

	@Override
	protected Map<String, Object> getDefaults() {
		
		Map<String, Object> defaults = super.getDefaults();
		defaults.put(MIN, 1);
		defaults.put(MAX, 100);
		return defaults;
	}
}
