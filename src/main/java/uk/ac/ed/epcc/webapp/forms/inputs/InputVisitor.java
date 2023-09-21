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
	/** An input that presents as check-box
	 * 
	 * @param checkBoxInput
	 * @return
	 * @throws Exception
	 */
	R visitBinaryInput(BinaryInput checkBoxInput) throws Exception;
	/** A composite input
	 * 
	 * @param <V> type of data 
	 * @param <I> type of sub-input
	 * @param multiInput
	 * @return
	 * @throws Exception
	 */
	<V,I extends Input> R visitMultiInput(MultiInput<V,I> multiInput) throws Exception;
	/** A {@link MultiInput} that can add/remove its values from a data-map directly
	 * 
	 * @param <V> type of data
	 * @param <I> type of sub-input
	 * @param multiInput
	 * @return
	 * @throws Exception
	 */
	<V,I extends Input> R visitParseMultiInput(ParseMultiInput<V,I> multiInput) throws Exception;
	/** An {@link ItemInput} that presents as a pull-down list
	 * 
	 * @param <V> Type of data
	 * @param <T> Type of item
	 * @param listInput
	 * @return
	 * @throws Exception
	 */
	<V,T> R visitListInput(ListInput<V,T> listInput) throws Exception;
	/** Functionally equivalent to a {@link ListInput} but presented as a series of radio-buttons
	 * 
	 * @param <V>
	 * @param <T>
	 * @param listInput
	 * @return
	 * @throws Exception
	 */
	<V,T> R visitRadioButtonInput(ListInput<V,T> listInput) throws Exception;
	/** An {@link ItemInput} that presents as a text box with a suggested list of auto-completions.
	 * This may allow values not in the autocomplete list
	 * 
	 * @param <V> type of data
	 * @param <T> type of item
	 * @param autocompleteInput
	 * @return
	 * @throws Exception
	 */
	default <V,T> R visitAutoCompleteInput(AutoComplete<V,T> autocompleteInput) throws Exception{
		return visitLengthInput(autocompleteInput);
	}
	/** An {@link ItemInput} that can be presents as either a text box with a suggested list of auto-completions
	 * or a pull-down list. As with a {@link ListInput} values not in the suggestion list will not be accepted.
	 * 
	 * @param <V> type of data
	 * @param <T> type of item
	 * @param acl
	 * @return
	 * @throws Exception
	 */
	default <V,T> R visitAutoCompleteListInput(AutoCompleteListInput<V,T> acl) throws Exception{
		return visitListInput(acl);
	}
	/** A generic text box input 
	 * 
	 * @param <V> type of data
	 * @param input
	 * @return
	 * @throws Exception
	 */
	<V> R  visitLengthInput(LengthInput<V> input) throws Exception;
	/** An input that should be shown as a non editable text label.
 * 
 * The input can still be set queried and validated as normal but the presentation layer will 
 * prevent the user from editing the value
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	R visitUnmodifyableInput(UnmodifiableInput input) throws Exception;
	/** An input for file uploads
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	R visitFileInput(FileInput input) throws Exception;
	/** A password type text box
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	R visitPasswordInput(PasswordInput input) throws Exception;
	/** An input to select a data/time
	 * 
	 * @param t
	 * @return
	 * @throws Exception
	 */
	default R visitTimestampInput(TimeStampInput t) throws Exception {
		return visitLengthInput(t);
	}
	/** An unmodifiable input formed by wrapping any other input type
	 * 
	 * @param <V>
	 * @param l
	 * @return
	 * @throws Exception
	 */
	default <V> R visitLockedInput(LockedInput<V> l)throws Exception{
		return visitUnmodifyableInput(l);
	}
	/** A w
	 * 
	 * @param <X>
	 * @param i
	 * @return
	 * @throws Exception
	 */
	default <X> R visitWrappedInput(WrappedInput<X> i) throws Exception {
		return (R) i.getNested().accept(this);
	}
}