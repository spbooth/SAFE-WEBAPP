package uk.ac.ed.epcc.webapp.session;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.content.ScrollText;
import uk.ac.ed.epcc.webapp.content.TemplateContributor;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.MetaDataContributer;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;

/** A {@link Composite} that adds a SSH public key to an {@link AppUser}
 * 
 * @author spb
 * @param <X> type key parses to
 *
 *
 */
public abstract class PublicKeyComposite<X> extends AppUserComposite<AppUser, PublicKeyComposite> implements UpdateNoteProvider<AppUser> , AnonymisingComposite<AppUser>, SummaryContributer<AppUser>, MetaDataContributer<AppUser>, TemplateContributor<AppUser>, HistoryFieldContributor{
	/**
	 * 
	 */
	public static final String NORMALISED_PUBLIC_KEY_META_ATTR = "NormalisedPublicKey";
	/**
	 * 
	 */
	public static final String PUBLIC_KEY_META_ATTR = "PublicKey";
	private static final Feature PUBLIC_KEY_FEATURE = new Feature("public-key",false,"Can we collect ssh public key for people");
	public static final Feature SSH_REQUIRE_RSA_FEATURE = new Feature("ssh.require.rsa", false,"ssh public keys must be RSA key");
	private static final Feature NOTIFY_SSH_KEY_CHANGE_FEATURE = new Feature("person.notify-key-change",false,"Generate notify ticket if user changes ssh-key");
	private static final Feature OPTIONAL_PUBLIC_KEY_FEATURE = new Feature("optional.public-key",true,"Are ssh keys optional");
	
	public static final String PUBLIC_KEY = PUBLIC_KEY_META_ATTR;
	
	public PublicKeyComposite(AppUserFactory fac) {
		super(fac);
	}

	@Override
	protected Class<? super PublicKeyComposite> getType() {
		return PublicKeyComposite.class;
	}

	@Override
	public Map<String, String> addTranslations(Map<String, String> translations) {
		translations.put(PUBLIC_KEY, "SSH Public key");
		return translations;
	}

	protected String normalise(String old) throws Exception {
		if( old == null ) {
			return null;
		}
		return format(load(old));
	}
	protected abstract X load(String value) throws Exception;
	protected abstract String format(X key) throws Exception;
	protected abstract ParseAbstractInput<String> getInput();
	@Override
	public Map<String, Selector> addSelectors(Map<String, Selector> selectors) {
		
		selectors.put(PUBLIC_KEY,new Selector() {

			@Override
			public Input getInput() {
				return  new FileUploadDecorator(PublicKeyComposite.this.getInput());
			}
			
		});
		return selectors;
	}

	@Override
	public void postUpdate(AppUser o, Form f, Map<String, Object> orig, boolean changed) throws DataException {
		if( changed && NOTIFY_SSH_KEY_CHANGE_FEATURE.isEnabled(getContext())){
			String extra="";
			String old_key = (String) orig.get(PUBLIC_KEY);
			try{
				old_key = normalise(old_key);
			}catch(Exception e){
				getLogger().warn("Old public key failed to normalise",e);
			}
			String new_key = getPublicKey(o);
			try{
				new_key = normalise(new_key);
			}catch(Exception e){
				getLogger().warn("New public key failed to normalise",e);
			}
			if( old_key!=null && new_key != null && ! old_key.trim().equals(new_key.trim())){
				extra += "public key changed\n";
				extra += "["+old_key+"]->["+new_key+"]";
			}else if( (old_key == null || old_key.trim().length() ==0) && new_key != null && new_key.trim().length() >0){
				extra += "public key added\n";
			}else if( old_key != null && old_key.trim().length() > 0 && (new_key == null || new_key.trim().length() ==0)){
				extra += "public key removed\n";
			}
			if( ! extra.isEmpty()){
				// still want to supress key based updates  if the machine manages keys explicitly
				o.notifyChange(PUBLIC_KEY, extra, old_key, new_key);
			}
		}
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(PUBLIC_KEY, new StringFieldType(true, null, 4096),isOptional());
		return spec;
	}

	@Override
	public Set<String> addOptional(Set<String> optional) {
		if(isOptional()){
			optional.add(PUBLIC_KEY);
		}
		return optional;
	}

	/**
	 * @return
	 */
	protected boolean isOptional() {
		return OPTIONAL_PUBLIC_KEY_FEATURE.isEnabled(getContext());
	}

	public boolean usePublicKey(){
		return getRepository().hasField(PUBLIC_KEY) && PUBLIC_KEY_FEATURE.isEnabled(getContext());
	}
	@Override
	public <CB extends ContentBuilder> CB addUpdateNotes(CB cb,AppUser person) {
		if( usePublicKey() ){
			if(isOptional()){
				ExtendedXMLBuilder text = cb.getText();
				text.addObject(new PreDefinedContent(getContext(), "ssh_key.note"));
				text.appendParent();
			}
		}
		return cb;
	}
	public String getPublicKey(AppUser person){
		return getRecord(person).getStringProperty(PUBLIC_KEY);
	}
	
	/** Get an array of all the Public keys stored for the user.
	 * This is an
	 * 
	 * @param person
	 * @return
	 */
	public String[] getPublicKeys(AppUser person) {
		String key = getPublicKey(person);
		if( key == null || key.isEmpty()) {
			return new String[0];
		}
		return new String[] { key };
	}
	public String getNormalisedPublicKey(AppUser person) throws Exception{
		return normalise(getPublicKey(person));
	}
	public void setPublickey(AppUser person,String key){
		getRecord(person).setOptionalProperty(PUBLIC_KEY, key);
	}
	public boolean hasPublicKey(AppUser person){
		String key = getPublicKey(person);
		return key != null && ! key.isEmpty();
	}

	@Override
	public void anonymise(AppUser target) {
		setPublickey(target, "");
	}

	@Override
	public void addAttributes(Map<String, Object> attributes, AppUser target) {
		String ssh_key = getPublicKey(target);
		if( ssh_key != null && ssh_key.trim().length() > 0 ){
//			StringBuilder sb = new StringBuilder();
//			for(int i=0 ; i< ssh_key.length() ; i++){
//				sb.append(ssh_key.charAt(i));
//				if( i > 0 && (i % 40 == 0)){
//					sb.append("\n");
//				}
//			}
//			attributes.put("SSH Key", sb.toString());
			attributes.put("SSH Key", new ScrollText(ssh_key));
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.MetaDataContributer#addMetaData(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addMetaData(Map<String, Object> attributes, AppUser person) {
		String publickey = getPublicKey(person);
		if( publickey != null && ! publickey.trim().isEmpty()){
			attributes.put(PUBLIC_KEY_META_ATTR,publickey);
			try{
				X key = load(publickey);
				attributes.put(NORMALISED_PUBLIC_KEY_META_ATTR,format(key));
			}catch(Exception e){
				getLogger().error("Error adding normalised key", e);
			}

		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TemplateContributor#setTemplateContent(uk.ac.ed.epcc.webapp.content.TemplateFile, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void setTemplateContent(TemplateFile template, String prefix, AppUser target) {
		String key=getPublicKey(target);
		if(key != null){
			template.setProperty(prefix+"publickey", key);
		}
	}

	@Override
	public void addToHistorySpecification(TableSpecification spec) {
		spec.setField(PUBLIC_KEY, new StringFieldType(true, null, 4096),isOptional());
		
	}
}
