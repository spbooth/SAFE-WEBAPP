package uk.ac.ed.epcc.webapp.session;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.DNInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.MetaDataContributer;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;

public class CertificateComposite extends AppUserComposite<AppUser, CertificateComposite> implements AnonymisingComposite<AppUser>, SummaryContributer<AppUser>,MetaDataContributer<AppUser> {
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		if(OPTIONAL_CERTIFICATE_FEATURE.isEnabled(getContext())){
			optional.add(PERSONAL_CERTIFICATE);
		}
		return optional;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(PERSONAL_CERTIFICATE, new StringFieldType(true, null, 200));
		return spec;
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSelectors(java.util.Map)
	 */
	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		selectors.put(PERSONAL_CERTIFICATE, new DNInput());
		return selectors;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#postUpdate(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
	 */
	@Override
	public void postUpdate(AppUser o, Form f, Map<String, Object> orig) throws DataException {
		if( NOTIFY_CERTIFICATE_CHANGE_FEATURE.isEnabled(getContext())){
			String extra ="";
			String old_dn = (String) orig.get(PERSONAL_CERTIFICATE);
			String new_dn = getPersonalCertificateDn(o);
			if( old_dn!=null && new_dn != null && ! old_dn.trim().equals(new_dn.trim())){
				extra += "personal certificate dn changed\n";
				extra += "["+old_dn+"]->["+new_dn+"]\n";
			}else if( (old_dn == null || old_dn.trim().length()==0) && new_dn != null && new_dn.trim().length() >0){
				extra += "personal certificate dn added\n";
			}else if( old_dn != null &&  old_dn.trim().length() >0 && (new_dn == null || new_dn.trim().length()==0)){
				extra += "personal certificate dn removed\n";
			}
			if( ! extra.isEmpty()){
				o.notifyChange(extra);
			}
		}
		
	}

	private static final Feature OPTIONAL_CERTIFICATE_FEATURE = new Feature("optional.person-certificate",true,"Are personal certificates optional");
	private static final Feature NOTIFY_CERTIFICATE_CHANGE_FEATURE = new Feature("person.notify-certificate-change",false,"Generate notify ticket if user changes certificate DN");
	private static final String PERSONAL_CERTIFICATE = "PersonalCertificate";

	public CertificateComposite(AppUserFactory fac) {
		super(fac);
		
	}
	public String getPersonalCertificateDn(AppUser person){
		return getRecord(person).getStringProperty(PERSONAL_CERTIFICATE);
	}
	public void setPersonalCertificateDn(AppUser person,String dn){
		getRecord(person).setOptionalProperty(PERSONAL_CERTIFICATE, dn);
	}
	public boolean hasPersonalCertificate(AppUser person){
		String dn = getPersonalCertificateDn(person);
		return dn != null && dn.trim().length() > 0;
	}
	
	@Override
	protected Class<? super CertificateComposite> getType() {
		return CertificateComposite.class;
	}

	@Override
	public void anonymise(AppUser target) {
		setPersonalCertificateDn(target, "");
	}

	@Override
	public void addAttributes(Map<String, Object> attributes, AppUser target) {
		String personal_cert = getPersonalCertificateDn(target);
		if( personal_cert != null && personal_cert.trim().length() > 0 ){
			attributes.put("Personal Certificate", personal_cert);
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.MetaDataContributer#addMetaData(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addMetaData(Map<String, String> attributes, AppUser person) {
		String dn = getPersonalCertificateDn(person);
		if( dn != null && dn.trim().length() > 0){
			attributes.put("Certificate",dn);
		}
		
	}

}
