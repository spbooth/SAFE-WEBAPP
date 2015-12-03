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
package uk.ac.ed.epcc.webapp.forms.inputs;


/** A visitor for Inputs
 * This visitor is used to create the actual UI elements and handle the parsing logic.
 * So Inputs need to implement one of the options.
 * As some of the target types are interfaces there may be classes that
 * implement more than one target. It is the implemented accept method
 * that defines which option is used to create the forms.
 * 
 * 
 * @author spb
 *
 * @param <R> type produced by visitor
 */
public interface InputVisitor<R> {
	R visitBinaryInput(BinaryInput checkBoxInput) throws Exception;
	<V,I extends Input> R visitParseMultiInput(ParseMultiInput<V,I> multiInput) throws Exception;
	<V,I extends Input> R visitMultiInput(MultiInput<V,I> multiInput) throws Exception;
	<V,T> R visitListInput(ListInput<V,T> listInput) throws Exception;
	<V,T> R visitRadioButtonInput(ListInput<V,T> listInput) throws Exception;
	R visitLengthInput(LengthInput input) throws Exception;
	R visitUnmodifyableInput(UnmodifiableInput input) throws Exception;
	R visitFileInput(FileInput input) throws Exception;
	R visitPasswordInput(PasswordInput input) throws Exception;
}