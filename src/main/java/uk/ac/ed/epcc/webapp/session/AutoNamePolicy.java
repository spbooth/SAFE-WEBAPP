package uk.ac.ed.epcc.webapp.session;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AsciiConverter;
import uk.ac.ed.epcc.webapp.content.TemplateContributor;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** A {@link FieldNameFinder} that lazily populates the name based on initial + surname
 * 
 * @author Stephen Booth
 *
 */
public final class AutoNamePolicy<AU extends AppUser> extends FieldNameFinder<AU,AutoNamePolicy>implements  AnonymisingComposite<AU>, TemplateContributor<AU>, SummaryContributer<AU> {

	
	public AutoNamePolicy(AppUserFactory<AU> factory, String realm) {
		super(factory, realm);
	}

	public String getName(AU p) {
		RealNameComposite rnc = (RealNameComposite)p.getFactory().getComposite(RealNameComposite.class);
		if( rnc == null){
			return null;
		}
		String firstname = rnc.getFirstname(p);
		if( firstname == null  ){
			firstname="";
		}
		String lastname = rnc.getLastname(p);
		if( lastname == null || lastname.isEmpty()){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if( firstname.length() > 0 ){
			sb.append(firstname.substring(0, 1).toLowerCase());
		}
		int max = 6;
		AsciiConverter conv = new AsciiConverter(false);
		lastname = conv.convert(lastname).replaceAll("\\s", "");
		sb.append(lastname.substring(0, max < lastname.length() ? max : lastname.length()).toLowerCase() );
		
		String prefix = sb.toString();
		for(int i=0; i < 1000; i++){
			String name;
			if( i == 0){
				name=prefix;
			}else{
				name= prefix+i;
			}
			try {
				AppUser p2 = fac.find(getStringFinderFilter( name),true);
				if( p2 == null ){
					return name;
				}
			} catch (DataException e) {
				getLogger().error("Error checking for unique wikiname",e);
				return name;
			}
		}
		return null;
	}
	
	


	@Override
	public void anonymise(AU target) {
		if( getContext().getBooleanParameter("person.anonymise."+getRealm(), true) && getRepository().hasField(getRealm())) {
			getRecord(target).setProperty(getRealm(), "Person-"+target.getID());
		}
	}

	@Override
	public void setTemplateContent(TemplateFile template, String prefix, AU target) {
		String globalname = getCanonicalName(target);
		if( globalname != null ){
			template.setProperty(prefix+getRealm(), globalname);
		}
		
	}

	@Override
	public String getCanonicalName(AU object) {
		
		String name = super.getCanonicalName(object);
		if( name == null ) {
			// Try setting the name so its persistent.
			name = getName(object);
			setName(object, name);
			try {
				object.commit();
			} catch (DataFault e) {
				getLogger().error("Error setting canonical name", e);
			}
		}
		return name;
	}

	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		String name = getCanonicalName(target);
		if( name != null ) {
			attributes.put(getNameLabel(), name);
		}
		
	}
}
