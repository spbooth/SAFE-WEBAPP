package uk.ac.ed.epcc.webapp.model.period;

import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampMultiInput;
import uk.ac.ed.epcc.webapp.time.TimePeriod;
/** Interface for class that can split {@link TimePeriod}s
 * Normally this is implemented by the appropriate factory class
 * @author spb
 *
 * @param <T>
 */
public interface SplitManager<T extends TimePeriod> {

	/** get the appropriate date input to use for this class
	 * 
	 * @return
	 */
	public abstract TimeStampMultiInput getDateInput();

	/** Validate a proposed split. The location of the split within the period is validated 
	 * explicitly. This is for additional constraints specific to the class logic.
	 * 
	 * @param orig
	 * @param d
	 * @throws ValidateException
	 */
	public abstract void canSplit(T orig, Date d) throws ValidateException;
	/** Split a record at a specified time returning new record starting
	 * at the split.
	 * 
	 * @param orig
	 * @param d
	 * @return new record or null if split outside record time range.
	 * @throws Exception
	 */
	public abstract T split(T orig, Date d) throws Exception;
	/** Get a date before which edits should be confirmed.
	 * 
	 * @return Date or null
	 */
	 public Date getEditMarker();
}