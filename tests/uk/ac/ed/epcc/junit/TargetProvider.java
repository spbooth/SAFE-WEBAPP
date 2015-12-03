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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;
/** Interface to be implemented by a Test case that
 * wishes to support an interface test.
 * 
 * This interface is needed by {@link ExampleInterfaceTest} to retreive the
 * task under test but is sufficiently general that it could also be uses
 * for unrelated interface tests.
 * 
 * @author spb
 *
 * @param <T> Type of interface to be tested
 */
public interface TargetProvider<T> {
  T getTarget();
}