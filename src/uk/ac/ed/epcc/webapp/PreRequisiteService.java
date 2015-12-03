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
package uk.ac.ed.epcc.webapp;
import java.lang.annotation.*; 

/** An annotation that defines services that are required by an AppContextService.
 * This does not include optional services that can be used but are not essential.
 * This allows the AppContext to build the requisites before attempting to instantiate the target
 * and should simplify the error reporting.
 * 
 * @author spb
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PreRequisiteService {
	public Class<? extends AppContextService>[] value();
}