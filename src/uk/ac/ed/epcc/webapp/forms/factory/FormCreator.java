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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.forms.Form;

/**
 * FormCreator Implemented by classes that can implement Object creation forms
 * By having an Interface we can re-use a lot of servlet boilerplate passing the
 * appropriate class that implements the Interface. 
 * 
 * Normally this interface is implemented by inner classes produced by the target Factory class 
 * This way all the
 * necessary factory methods are still accessible but the behaviour of the form
 * can be customised. It also gives us somewhere to cache additional state that
 * may be required to process the form like the user making the request.
 * 
 * Creation actions are defined as those that start form a default form
 * configuration rather than populating a form from an existing object.
 * 
 * @author spb

 * 
 */
public interface FormCreator extends FormFactory {

	/**
	 * build a form to create an object. This should include Action Buttons that
	 * actually create the object
	 * @param type_name NAme of target type as presented to user
	 * 
	 * @param f
	 * @throws Exception 
	 */
	public void buildCreationForm(String type_name,Form f) throws Exception;
	
}