package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** A {@link AppUserNameFinder} where the name comes from the object ID
 * 
 * This is useful if we want to generate a unique name independly of the fields
 * 
 * @author Stephen Booth
 *
 * @param <AU>
 */
public class IdNameFinder<AU extends AppUser,X extends IdNameFinder> extends AppUserNameFinder<AU, X> {

	public IdNameFinder(AppUserFactory<AU> factory, String realm) {
		super(factory, realm);
	}

	protected String id2Name(int id) {
		return Integer.toString(id);
	}
	protected int Name2id(String name) {
		return Integer.parseInt(name);
	}
	public void validateNameFormat(String name) throws ParseException{
		try {
			// this should also handle an overidden #Name2id method
			Name2id(name);
		}catch(Throwable t) {
			throw new ParseException("Bad ID format", t);
		}
	}
	
	@Override
	public boolean userVisible() {
		return getContext().getBooleanParameter(PROPERTY_PREFIX+getRealm()+".user_visible", true);
	}
	
	@Override
	public SQLFilter<AU> getStringFinderFilter(String name) {
		return getFactory().getFindFilter(Name2id(name));
	}

	@Override
	public SQLFilter<AU> hasCanonicalNameFilter() {
		return new GenericBinaryFilter<AU>(true);
	}

	@Override
	public String getCanonicalName(AU object) {
		return id2Name(object.getID());
	}

	@Override
	public String getNameLabel() {
		return "Id";
	}

	@Override
	public boolean active() {
		return true;
	}

	@Override
	public void setName(AU user, String name) {
		
	}

	@Override
	public boolean canMakeFromString() {
		return false;
	}

}
