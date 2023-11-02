package uk.ac.ed.epcc.webapp.session.twofactor;

import java.security.Key;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
/** A {@link FieldValidator} that validats a code against a {@link TotpProvider}
 * 
 * @param <A>
 */
public class CodeValidator<A extends DataObject> implements FieldValidator<Integer>{
	/**
	 * 
	 */
	private final TotpProvider<A> totp_provider;

	/**
	 * @param key
	 * @param prov {@link TotpProvider} to validate key
	 */
	public CodeValidator(TotpProvider<A> prov, Key key) {
		super();
		totp_provider = prov;
		this.key = key;
	}

	private final Key key;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
	 */
	@Override
	public void validate(Integer data) throws FieldException {
		StringBuilder sb = new StringBuilder();
		if( ! totp_provider.verify(null,key, data,null,sb) ) {
			if( sb.length() > 0 ) {
				throw new ValidateException(sb.toString());
			}
			throw new ValidateException("Incorrect");
		}
		
	}
}