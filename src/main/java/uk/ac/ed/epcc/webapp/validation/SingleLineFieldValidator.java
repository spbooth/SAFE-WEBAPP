package uk.ac.ed.epcc.webapp.validation;
/** A marker interface for {@link FieldValidator}s on String fields that
 * should be presented as a single text line.
 * This does not change the validation of the field but can act as a hint for
 * how the UI presents the input
 * 
 */
public interface SingleLineFieldValidator extends FieldValidator<String> {

}
