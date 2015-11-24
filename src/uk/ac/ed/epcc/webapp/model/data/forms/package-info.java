/** classes to support the default create/update transitions.
 * <p>
The <code> FormUpdate</code> interface supports selecting and editing existing objects.
<code>FormCreator</code> supports creating objects from a form. This functionality is now mapped onto
the transition mechanism via a standard provider. The same operations can be performed through custom providers using sub-classes of {@link uk.ac.ed.epcc.webapp.model.data.forms.CreateTransition} or
{@link uk.ac.ed.epcc.webapp.model.data.forms.UpdateTransition}
</p>
 * 
 */
package uk.ac.ed.epcc.webapp.model.data.forms;