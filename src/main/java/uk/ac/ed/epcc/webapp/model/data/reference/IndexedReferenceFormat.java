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
package uk.ac.ed.epcc.webapp.model.data.reference;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.content.Transform;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
/** Table format form converting reference fields into 
 * object identifier strings.
 * 
 * @author spb
 *
 */


public class IndexedReferenceFormat implements Transform{
    private AppContext c;
    public IndexedReferenceFormat(AppContext c){
    	this.c=c;
    }
    public Object convert(Object old) {
    	if( old == null ){
    		// could be a total row in a category sum
    		return null;
    	}
	    if( old instanceof IndexedReference ){
	    		IndexedReference ref = (IndexedReference) old;
	    		if( ref.getID() == 0 ){
	    			return "Unknown";
	    		}
	    		try {
					Indexed i = ref.getIndexed(c);
					if( i instanceof Identified){
						return ((Identified) i).getIdentifier();
					}
					return i.toString();
				} catch (Exception e) {
					return "Unknown";
				}
	    }
    	return old;
    }
}