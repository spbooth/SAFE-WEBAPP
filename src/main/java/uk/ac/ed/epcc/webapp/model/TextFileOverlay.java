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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incava.diff.Diff;
import org.incava.diff.Difference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.InfoInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.RetireAction;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.model.serv.SettableServeDataProducer;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
import uk.ac.ed.epcc.webapp.session.SessionDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** TextFileOverlay represents a DB overlay above the local file-system
 * intended to allow overrides of file contents to be stored in the database.
 * Resources are indexed by two strings group-name and file-name the combination of which should be unique.
 * 
 * <p>
 * For the file-system representation of a resource the parent directory is 
 * located by using the group name as a configuration parameter. (Non absolute directory paths 
 * are relative to the application root. Multiple search directories can be specified as a comma seperated list
 * <p>
 * When a resource is requested it is first looked up in the database. 
 * If it does not exist (or the contents are null) then the file-system resource is retrieved.
 * otherwise the DB contents are retrieved.
 * <p>
 * All lookups cause a DB entry with null 
 * contents will be created to register that resource for form updates. 
 * The update form always populates with the
 * value a lookup would return but it only stores a value if this differs from the file-system
 * value.
 * <p>
 * The create method is used to create database only entries. For example to add
 * entries to a location within the application war-file. 
 * 
 * @author spb
 * @param <T> 
 *
 */



public class TextFileOverlay<T extends TextFileOverlay.TextFile> extends DataObjectFactory<T> {

	protected static final String GROUP="Group";
	protected static final String NAME="Name";
	protected static final String TEXT="Text";
	protected static String getStringFromStream(AppContext conn,InputStream stream) throws IOException {
		if( stream != null ){
				StringBuilder file_buffer = new StringBuilder();
				InputStreamReader f = new InputStreamReader(stream);

				char buffer[] = new char[4096];
				int chars_read = f.read(buffer, 0, 4096);
				while (chars_read != -1) { // while not EOF
					file_buffer.append(buffer, 0, chars_read);

					chars_read = f.read(buffer, 0, 4096);
				}
				return file_buffer.toString();
			
		}
		return null;
	}

	protected static InputStream getResourceStream(AppContext conn,
			final String group, String name) {
		Logger log = conn.getService(LoggerService.class).getLogger(TextFileOverlay.class);
		ResourceService serv = conn.getService(ResourceService.class);
		String dirs=conn.getInitParameter(group);
		log.debug("dirs="+dirs);
		if( dirs != null ){
			for( String dir : dirs.split(",")){
				dir=dir.trim();
				String resource_name = dir+"/"+name;
				log.debug("input stream resource name is "+resource_name);
				try{
				InputStream stream = 
					serv.getResourceAsStream(resource_name);
				if( stream != null ){
					log.debug("stream found");
					return stream;
				}
				}catch(Exception e){
					conn.error(e,"Error getting resource stream for TextFile");
				}
			}
		}
		return null;
	}

	// If this is non null then all URLs are mapped relative to this base
	// this allows the TextFileOverlay to be inserted into 
	// a URL resolver class
	private URL url_base=null;
	public static class TextFile extends DataObject implements Retirable, TextProvider{

		private final  URL base_url;
		
		protected TextFile(Record r,URL base) {
			super(r);
			this.base_url=base;
		}
		/** Is there data for this resource
		 * 
		 * @return boolean true if data exists
		 */
		public boolean hasData(){
			return getText() != null || hasResourceStream();
		}
		URL getURL(){
			AppContext conn = getContext();
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			log.debug("In getURL");
			if(base_url != null ){
				try {
					URL url = new URL(base_url+"/"+getName());
					log.debug("returning url "+url);
					return url;
				} catch (MalformedURLException e) {
					conn.error(e,"Error making URL from base");
				}
			}
			
			
			ResourceService serv = conn.getService(ResourceService.class);
			String dirs=conn.getInitParameter(getGroup());
			log.debug("dirs="+dirs);
			String name = getName();
			if( dirs != null ){
				for( String dir : dirs.split(",")){
					dir=dir.trim();
					String resource_name = dir+"/"+name;
					log.debug("resource name is "+resource_name);
					URL url = serv.getResource(resource_name);
					if( url != null){
						log.debug("url is "+url);
						return url;
					}
				}
			}
			return null;
		}
		
		/** Get the underlying resource as a stream
		 * 
		 * @return InputStream
		 */
		public InputStream getResourceStream(){
			AppContext conn = getContext();
			final String group = getGroup();
			String name = getName();
			
			return TextFileOverlay.getResourceStream(conn, group, name);
		}
		/** Do we have an underlying resource
		 * 
		 * @return boolean true if resouce exists
		 */
		boolean hasResourceStream(){
			AppContext conn = getContext();
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			ResourceService serv = conn.getService(ResourceService.class);
			String dirs=conn.getInitParameter(getGroup());
			log.debug("dirs="+dirs);
			String name = getName();
			if( dirs != null ){
				for( String dir : dirs.split(",")){
					String resource_name = dir+"/"+name;
					log.debug("input stream resource name is "+resource_name);
					URL url = 
						serv.getResource(resource_name);
					if( url != null ){
						return true;
					}
				}
			}
			return false;
		}
		/** Do we have an override text;
		 * 
		 * @return
		 */
		boolean hasText(){
			return getText() != null;
		}
		
		public String getName(){
			return record.getStringProperty(NAME).trim();
		}
		public String getGroup(){
			return record.getStringProperty(GROUP).trim();
		}
		/** Get the override text.
		 * 
		 * @return String text
		 */
		public String getText(){
			return record.getStringProperty(TEXT);
		}
		public void setText(String text){
			record.setProperty(TEXT, text);
		}
		public void setGroup(String group){
			record.setProperty(GROUP, group);
		}
		public void setName(String name){
			record.setProperty(NAME, name);
		}
	
		/** Get he underlying resoruce as a String
		 * 
		 * @return String text
		 */
		public String getResourceString(){
			try{
				InputStream stream = getResourceStream();
				return getStringFromStream(getContext(),stream);
			} catch (Exception e) {
				getContext().error(e, "Error getting stream resource");
				return null;
			}
		}
		/** get a string representation of the current data balue
		 * 
		 * @return
		 */
		@Override
		public String getData(){
			String text = getText();
			if( text != null ){
				return text;
			}
		
			text =  getResourceString();
			if( text != null ){
				return text;
			}
			getLogger().error("TextFileOverlay "+getIdentifier()+" file="+getName()+" no readable file found");
			return "";
		}
		
		@Override
		public String getIdentifier(int max) {
			String tag ="";
			if( hasText()){
				String resource = getResourceString();
				if( resource == null ){
					tag = "[D]"; // database only
				}else if( getText().equals(resource)){
					tag = "[U]"; // unmodified
				}else{
					tag="[M]"; // modified
				}
			}else {
				String resource = getResourceString();
				if( resource == null ){
					tag = "[E]"; // no text or database
				}
			}
			return getGroup()+":"+getName()+tag;
		}
		@Override
		public boolean canRetire() {
			return true;
		}
		@Override
		public void retire() throws Exception {
			delete();	
		}
	}
	public TextFile find(String group,String name) throws DataFault{
		SQLAndFilter<T> get = new SQLAndFilter<>(getTarget());
		get.addFilter(new SQLValueFilter<>(getTarget(),res,GROUP,group));
		get.addFilter(new SQLValueFilter<>(getTarget(),res,NAME,name));
		T tf;
		try {
			tf = find(get,true);
		} catch (DataException e) {
			tf=null;
		}
		if( tf == null ){
			tf = makeBDO();
			tf.setGroup(group);
			tf.setName(name);
			tf.setText(null);
			if( ! tf.hasResourceStream()) {
				return null;
			}
			
			tf.commit();
		}
		
		return tf;
	}

	@Override
	protected T makeBDO(Record res) throws DataFault {
		return (T) new TextFile(res,url_base);
	}
	
	public void setBaseURL(URL base){
		url_base=base;
	}
	public URL getBaseURL(){
		return url_base;
	}
	/** Normalise and split input.
	 * 
	 * Used for diff operations.
	 * 
	 * @param input
	 * @return
	 */
	protected String[] splitNormalised(String input) {
		return input.split("\r?\n");
	}
	public class TextFileDiffAction extends FormAction{
		TextFile tf;
		/**
		 * 
		 */
		public TextFileDiffAction(TextFile tf) {
			this.tf=tf;
			setMustValidate(false);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			try {

				String originalArray[] = splitNormalised(tf.getResourceString());
				String newArray[] = splitNormalised(tf.getText());
				Diff<String> diff = new Diff<>(originalArray,newArray);
				StringBuilder result = new StringBuilder();
				for (Difference difference :diff.execute()) {
					

					int as = difference.getAddedStart();
					int ae = difference.getAddedEnd();
					int ds = difference.getDeletedStart();
					int de = difference.getDeletedEnd();

					if (difference.getDeletedEnd() == -1) {
						result.append(
								ds+"a"+
										(as+1)+","+
										(ae+1)
										+"\n");
						for (int i = as; i <= ae; i++) {
							result.append("> "+newArray[i]+"\n");
						}

					} else if (ae == -1) {
						result.append(
								(as)+
								"d"+
								(ds+1)+","+
								(de+1)
								+"\n");
						for (int i = ds; i <= de; i++) {
							result.append("< "+originalArray[i]+"\n");
						}            	

					} else {
						result.append(ds+1);
						if (ds != de) {
							result.append(","+(de+1));
						}
						result.append("c");
						result.append(as+1);
						if (as != ae) {
							result.append(","+(ae+1));
						}
						result.append("\n");

						for (int i = difference.getDeletedStart(); i <= difference.getDeletedEnd(); i++) {
							result.append("< "+originalArray[i]+"\n");
						}
						result.append("---\n");
						for (int i = difference.getAddedStart(); i <= difference.getAddedEnd(); i++) {
							result.append("> "+newArray[i]+"\n");
						}
					}

				}
				SettableServeDataProducer producer = getContext().makeObjectWithDefault(SettableServeDataProducer.class, SessionDataProducer.class, ServeDataProducer.DEFAULT_SERVE_DATA_TAG);
				ByteArrayMimeStreamData diffdata = new ByteArrayMimeStreamData(result.toString().getBytes());
				diffdata.setMimeType("text/plain");
				diffdata.setName("diffdata.txt");
				return new ServeDataResult(producer, producer.setData(diffdata));
			}catch(Exception t) {
				throw new ActionException("internal_error", t);
			}
		}

	}
	public static class TextFileUpdateAction extends FormAction{
		TextFile dat;
		String type_name;
		public TextFileUpdateAction(
				String type_name,
				TextFile dat) {
			this.type_name=type_name;
			this.dat=dat;
		}

		@Override
		public MessageResult action(Form f) throws ActionException {
			String new_text = (String) f.get(TEXT);
			String file_text=dat.getResourceString();
			if(new_text == null ||  new_text.equals(file_text)){
				dat.setText(null);
			}else{
				dat.setText(new_text);
			}
			try {
				dat.commit();
			} catch (DataFault e) {
				dat.getContext().error(e,"Update failed");
				throw new ActionException("Update failed");
			}
			return new MessageResult("object_updated",type_name,dat);
		}
		
	}
	public static class TextFileRevertAction extends FormAction{
		TextFile dat;
		String type_name;
		public TextFileRevertAction(
				String type_name,
				TextFile dat) {
			this.type_name=type_name;
			this.dat=dat;
			setMustValidate(false);
		}

		@Override
		public MessageResult action(Form f) throws ActionException {
			
			dat.setText(null);
			try {
				dat.commit();
			} catch (DataFault e) {
				dat.getContext().error(e,"Revert failed");
				throw new ActionException("Revert failed");
			}
			return new MessageResult("object_updated",type_name,dat);
		}
		
	}
	public class TextFileUpdator implements StandAloneFormUpdate<T>, UpdateTemplate<T>{

		@Override
		public void buildSelectForm(Form f, String label, T dat) {
			Input<Integer> i = getInput();
            if( ! isValid()){
            	return;
            }
			f.addInput(getUniqueIdName(), label, i);
			if (dat != null && isMine(dat)) {
			    i.setValue(new Integer(dat.getID()));
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void buildUpdateForm(String type_name,Form f, T dat,SessionService<?> operator) throws DataException {
			boolean show_diff=false;
			if(dat == null ||  ! isValid()){
				return;
			}
			f.addInput(GROUP, GROUP, new InfoInput(dat.getGroup()));
			f.addInput(NAME, NAME, new InfoInput(dat.getName()));
			URL file = dat.getURL();
			if( file != null ){
				f.addInput("File", "File location", new InfoInput(file.toString()));
			}
			boolean from_file = ! dat.hasText();
			String location;
			if( from_file ){
				String resource_text = dat.getResourceString();
				if( resource_text == null){
					location = "No content";
				}else {
					location = "File";
				}
			}else{
				String resource_text = dat.getResourceString();
				if( resource_text == null){
					location ="Database-only";
				}else{
					if( resource_text.equals(dat.getText())){
						location = "Database-unchanged";
					}else{
						location = "Database";
						show_diff=true;
					}
				}
			}
			
			f.addInput("Location", "Location",new InfoInput(location));
			Map<String,Selector> sel = getSelectors();
			Input<String> text = (Input<String>) sel.get(TEXT).getInput();
			text.setValue(dat.getData());
		
			f.addInput(TEXT, TEXT, text);
			f.addAction("Update", new TextFileUpdateAction(type_name,dat));
			if( show_diff ) {
				f.addAction("Revert",new TextFileRevertAction(type_name,dat));
				f.addAction("Diff", new TextFileDiffAction(dat));
			}
			f.addAction("Delete",new RetireAction<>(type_name,dat));
		}

		@Override
		@SuppressWarnings("unchecked")
		public T getSelected(Form f) {
			DataObjectItemInput<T> input = (DataObjectItemInput<T>) f.getInput(getUniqueIdName());
			return input.getItem();
		}

		@Override
		public AppContext getContext() {
			return TextFileOverlay.this.getContext();
		}

		@Override
		public void postUpdate(T o, Form f,Map<String,Object> orig) throws DataException {
			
		}

		@Override
		public FormResult getResult(String typeName, T dat, Form f) {
			return new MessageResult("object_updated",typeName,dat);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
		 */
		@Override
		public void preCommit(T dat, Form f, Map<String, Object> orig) throws DataException {
			// TODO Auto-generated method stub
			
		}		
		
	}
	@Override
	public FormUpdate<T> getFormUpdate(AppContext c) {
		return new TextFileUpdator();
	}
	public TextFileOverlay(AppContext c,String table){
		initContext(c, table);
	}
	public TextFileOverlay(AppContext c){
		String table = c.getInitParameter("table.overlay","TextFileOverlay");
		if( table != null){
			initContext(c,table);
		}
	}

	private void initContext(AppContext c, String table) {
		setContext(c, table);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table){
		TableSpecification s = new TableSpecification();
		// mysql (5.1 at least) has a 1000 byte limit on index size for MyISAM
		// and its 3 bytes per char
		s.setField(GROUP, new StringFieldType(false, null, 64));
		s.setField(NAME, new StringFieldType(false, null, 255));
		s.setField(TEXT, new StringFieldType(true,null,102400 ));
		try {
			s.new Index("find_key", true, GROUP,NAME);
		} catch (InvalidArgument e) {
			c.error(e,"Error making find_key");
		}
		return s;
	}
	public FilterResult<T> allbyGroup(String groupName) throws DataFault {		
		return new FilterSet(new SQLValueFilter<>(getTarget(),res,TextFileOverlay.GROUP ,groupName.trim()));
		
	}
	/** Check we are only creating a new entry and one where no
	 * matching file already exists.
	 * 
	 * @author spb
	 *
	 */
	public class ExistsValidator implements FormValidator{

		@Override
		public void validate(Form f)
				throws ValidateException {
			try {
				TextFile tf = find((String)f.get(GROUP),(String)f.get(NAME));
				if( tf == null ){
					// OK no existing entry of matching file
					return;
				}
			} catch (DataFault e) {
				getContext().error(e,"Error looking for TextFile");
			}
			throw new ValidateException("Target file already exists");
		}
		
	}
	/** Create a database only TextFile object
	 * 
	 * @author spb
	 *
	 */
	public class TextFileCreator extends Creator<T>{

		public TextFileCreator() {
			super(TextFileOverlay.this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.Creator#customiseCreationForm(uk.ac.ed.epcc.webapp.model.data.forms.Form)
		 */
		@Override
		public void customiseCreationForm(Form f) throws Exception {
			f.put(TEXT, getInitialCreateText());
			f.addValidator(new ExistsValidator());
		}
		
	}
	/** Generates initial text to use when creating. 
	 * 
	 * @return
	 */
	protected String getInitialCreateText(){
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getFormCreator(uk.ac.ed.epcc.webapp.model.AppUser)
	 */
	@Override
	public FormCreator getFormCreator(AppContext c) {
		return new TextFileCreator();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getSelectors()
	 */
	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String,Selector> sel = new HashMap<>();
		
		sel.put(TEXT,new Selector() {

			@Override
			public Input getInput() {
				TextInput text = new TextInput();
				text.setMaxResultLength(1<<24);
				return text;
			}
			
		});
		Selector s = new Selector() {

			@Override
			public Input getInput() {
				TextInput name = new TextInput();
				name.setSingle(true);
				return name;
			}
			
		};
		
		sel.put(NAME, s);
		
		sel.put(GROUP, s);
		return sel;
	}

	

	@Override
	public Class<T> getTarget() {
		return (Class) TextFile.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getOrder()
	 */
	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(GROUP, false));
		order.add(res.getOrder(NAME, false));
		return order;
	}
	
}