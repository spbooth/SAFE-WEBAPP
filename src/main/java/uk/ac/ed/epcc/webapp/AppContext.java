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
/*
 * Created on 14-Nov-2003
 *
 
 */
package uk.ac.ed.epcc.webapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.DefaultConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.DefaultDataBaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.logging.print.PrintLoggerService;
import uk.ac.ed.epcc.webapp.model.data.Composable;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.resource.DefaultResourceService;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/**
 * The {@link AppContext} is used to encapsulate the runtime environment.
 * 
 * To a first approximation the <code>AppContext</code> is passed down to all
 * levels of the code and most significant classes contain a reference to an <code>AppContext</code>. 
 * Because Web applications are typically multi threaded
 * we wish to avoid static variables. The <code>AppContext</code> is used to hold any information 
 * that we would otherwise be tempted to have as static and any static methods usually need to take
 * an {@link AppContext} as a parameter.
 *
 * <p>
 * Though there is a high degree of dependence between <code>AppContext</code> and most of 
 * the code base we concentrate global dependencies within this Object which
 * reduces coupling to the classes contained within the 
 * <code>AppContext</code> such as the database connection.
 * <p>
 * Each page request will have an independent
 * <code>AppContext</code> and short term state can be cached within in using the <code>setAttribute</code>
 * method. This data can be retrieved using the <code>getAttribute</code> method. 
 * 
 *  <p>
 * The AppContext also acts as a composite policy object allowing different behaviours 
 * to be selected depending on the environment. 

 * This allows a policy object specific to a particular type of functionality (e.g. logging) to be retrieved using the {@link #getService(Class)} method (the class object for
 * the required interface/superclass is used as the key). e.g.
 * <pre>
 * LoggerService lfac = conn.getService(LoggerService.class);
 * Logger log = lfac.getLogger(getClass())
 * log.debug("Debug message");
 * </pre> 
 * <p> 
 * Services all implement {@link AppContextService} and can be pre-set using the {@link #setService(AppContextService)} method or they can be constructed dynamically by a call to {@link #makeObject(Class, String)}
 * The FQCN of the class used to request the service is used as the identifying tag when lookup up the implementation class. If the located class is annotated with the {@link PreRequisiteService} annotation
 * then the AppContext will attempt to ensure the referenced services are in place before calling the constructor of the requested service.
 * All services are cached as attributes so the same instance will be returned by subsequent  calls to 
 * {@link #getService(Class)}. Frequently {@link AppContextService}s will be chained together with the currently installed implementation of a service holding a reference to the implementation it replaced and possibly
 * forwarding some requests to that nested version. 
 * <p>
 * Key {@link AppContextService}s include:
 * <ul>
 * <li> {@link LoggerService} - application logging policy </li>
 * <li> {@link ConfigService} - configuration property policy </li>
 * <li> {@link SessionService} - user session and persistence <li>
 * <li> {@link DatabaseService} - database connection service </li>
 * </ul>
 * 
 * Note that there may be legacy methods in AppContext for functionality that was once part of the class proper but has been moved 
 * into a separate service.
 * <p> 
 *  The AppContext class is final so all specialisations need to be implemented via {@link AppContextService}s.
 * <p>
 * The AppContext encodes the conventions for constructing objects by reflection.
 * A typical use might be:
<pre>
TargetType t = conn.makeObject(TargetType.class,"tag-name");
</pre>
 *  This uses the {@link #getPropertyClass(Class, String)} method that looks for a class name in 
 *  a configuration property of the form
 *  <b>class.<i>tag-name</i></b> This may either be a full class-name or the name of a 
 *  <em>classdef</em> parameter e.g. 
<pre>
  class.fred=myclass
  class.bill=myclass
  classdef.myclass=com.example.thing
</pre>
 *  The target type can be an interface or a superclass of desired object. If the target type is a non
 *  abstract class it will be used as the default if no class is configured using parameters.
 *  The AppContext will attempt various constructor signatures when creating an object in this way attempting to pass the
 *  tag-name and the AppContext. Some of these include the tag-name so its possible to refer to several instances of the same type
 *  by different tag-names. Factory classes that correspond to different database tables often follow this pattern, using the table name as the tag.
 *  If a class implements the {@link Tagged} interface the value returned by the {@link Tagged#getTag()} method should be the same
 *  as the tag used to construct the object.
 * <p>
 * 
 * 
 * <p>
 *<h3>Configuration parameters</h3>
 *The AppContext provides mechanisms for retrieving configuration parameters. e.g.
 *<code>
 *<pre>
   AppContext c;
   String param1 = c.getInitParameter("param1");
   String param2 = c.getInitParameter("param2","Default value");
   int int_param = c.getIntegerParameter("param3",13); // default to 13
 * </pre>
 * </code>
 * These methods use an underlying {@link ConfigService}.
 * 
 
 *<h3>Error reporting</h3>
 * The AppContext provides error reporting methods
 * <code>
<pre>
  try{
    ....
  }catch(Exception e){
    c.error(e,"An error occurred");
  }
 </pre> 
 * </code>
 * These are forwarded onto an underlying {@link LoggerService} if one is available but converts the error to a fatal error otherwise.
 * Normally it is better to use a {@link Logger} specific to 
 * the class where the exception is caught unless reporting error from within a {@link AppContextService}.
 * We will probably remove or reduce the visibility of the AppContext methods in the near future.
 * <p>

 * 
 *
 * 
 * @author spb
 * 
 */
public final class AppContext {
	
	public static final String CLASSDEF_PROP_PREFIX = "classdef.";
	public static final Feature CONTEXT_CACHE_FEATURE = new Feature("ContextCache",true,"cache objects that implement ContextCache in the AppContext");
	public static final Feature DATABASE_FEATURE = new Feature("database",true,"use database");
	public static final Feature TAG_CHECK_FEATURE = new Feature("appcontext.tag_check",false,"appcontext checks tags against values used to construct");
	public static final String CLASS_PREFIX = "class.";
	// a hastable for caching objects in the AppContext
	private Map<Object,Object> attributes = null;

    private LinkedHashMap<Class,AppContextService> services = null;
    // services known not to resolve
    private Set<Class> missing_services =null;
    private boolean disable_service_creation=false;
 
    
    public AppContext(){
    	this(null);
    }
    public AppContext(Properties props) {
    	// Set default values for the mandatory services
    	setService(new DefaultResourceService(this));
    	setService(new DefaultConfigService(this,props));
    	setService(new PrintLoggerService());
    	clearService(TimerService.class);
    	if( DATABASE_FEATURE.isEnabled(this)){
    		// we can supress database setup with a config parameter
    		setService(new DefaultDataBaseService(this));
    	}
    	
    }
   

    
	/** Cleans up resources held by the {@link AppContext}
	 * 
	 *  The {@link AppContextService#cleanup()} method is called on
	 * each service in the reverse order that services were created.
	 * 
	 */
	public synchronized void close()  {
		
		// Don't want to re-populate 
		disable_service_creation=true;
		
		if (attributes != null) {
			for( Iterator<Entry<Object, Object>> it =attributes.entrySet().iterator();it.hasNext();) {
				Map.Entry e = it.next();
				Object o = e.getValue();
				if( o instanceof AppContextCleanup) {
					((AppContextCleanup)o).cleanup();
				}
				it.remove();
			}
			attributes.clear();
			attributes = null;
		}
		if( services != null ){
			Set<Class> keySet = services.keySet();
			Class keys[] = keySet.toArray(new Class[keySet.size()]);
			/** close in reverse allocation order */
			for(int i=keys.length-1;i>=0;i-- ){
				AppContextService serv = services.get(keys[i]);
				services.remove(keys[i]);
				if( serv != null ){
				   serv.cleanup();
				}
			}
			services.clear();
			services=null;
		}
	}
	

	/**
	 * Report an application error.
	 * 
	 * @param e
	 *            Exception generating error.
	 * @param text
	 *            Text of error.
	 */
	public void error(Throwable e,String text){
		LoggerService serv = getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(text,e);
			}else{
				// cannot log convert to error
				throw new ConsistencyError(text, e);
			}
		}else{
			// cannot log convert to error
			throw new ConsistencyError(text, e);
		}
	}
	
	/**
	 * Report an application error.
	 * 
	 * @param errors
	 *            Text of error.
	 */
	
	public final void error(String errors) {
		LoggerService serv = getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors);
			}
		}
	}

	
	/**
	 * method for returning Objects cached in the AppContext using setAttribute.
	 * Code should never assume the existence of an attribute this should only
	 * be used for short term caching to avoid unnecessary database queries
	 * 
	 * <p>
	 * We use Object as the key instead of String so that classes can create private
	 * key objects (e.g. an Enum) to ensure cached objects are not accessible outside the class. 
	 * 
	 * @param key
	 * @return cached object or null if it does not exist
	 */
	public final Object getAttribute(Object key) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(key);
	}
	/** Do we have the specified attribute set
	 * 
	 * @param key
	 * @return boolean
	 */
    public boolean hasAttribute(Object key){
    	if( attributes == null ){
    		return false;
    	}
    	return attributes.containsKey(key);
    }
    public Map<Object,Object> getAttributes(){
    	if( attributes == null ){
    		return new HashMap<>();
    	}
    	return new HashMap<>(attributes);
    }
    @SuppressWarnings("unchecked")
    /** get a Service requested by the Interface class of the service.
     * This method will not allow recursive calls so pre-requisite services must be 
     * pre-configured.
     * 
     */
	public final synchronized <S extends AppContextService> S getService(Class<S> clazz){
    	// We have to be very careful not to call getService recursively
    	// so only report errors by hard_error
    	// 
    	if( services != null && services.containsKey(clazz)){
    		return (S) services.get(clazz);
    	}
    	if( missing_services != null && missing_services.contains(clazz)){
    		// known to be missing
    		return null;
    	}
    	if( missing_services == null ){
    		missing_services=new HashSet<>();
    	}
    	missing_services.add(clazz); // setService will undo this if found.
    	if( disable_service_creation ){
    		// this is a recursive call to getService 
    		// This can cause all sorts of problems so is dis-allowed. 
    		// You can trigger required services to be pre-made using the
    		// PreRequisiteService annotation
    		return null;
    	}
    	disable_service_creation=true;
    	S res=null;
		try {
			if( clazz == ConfigService.class){
				throw new ConsistencyError("No config service available");
			}
			
			res = makeService(clazz);
			if( res != null ){
				setService(res);
			}
		} catch (Exception e) {
			throw new ConsistencyError("Error creating AppContextService "+clazz.getCanonicalName(),e);
		} finally{
			disable_service_creation = false;
		}
		
    	return res;
    }

    /** Return a collection of the current {@link AppContextService}s
     * 
     * This is used for generating debugging info.
     * 
     * @return Collection
     */
    public final Collection<AppContextService> getServices(){
    	return new LinkedList<>(services.values());
    }

    /** Internal method to make services. This does not obey the disable_Service_creation
     * flag so it can make pre-requisites.
     * 
     * 
     * @param <S>
     * @param clazz
     * @return
     * @throws Exception
     */

	private <S extends AppContextService> S makeService(Class<S> clazz) throws Exception {
		Class<? extends S> target = null;
		String tag=clazz.getCanonicalName();
		if( clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())){
			// Make sure we don't try instantiating the template as this will generate
			// errors for the normal case of an interface and this in turn
			// may cause service lookups in the reporting code which may repeat the process.
			// services must be pre-configured or explicitly set using a property.
			target =getPropertyClass(clazz, null, tag);
		}else{
			target = getPropertyClass(clazz, tag);
		}
		
		
		if( target != null ){
			checkPreRequisites(target);
			return makeObject(target);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <S extends AppContextService> void checkPreRequisites(Class<S> target)
			throws ConsistencyError{
		// check for pre-requisites
		PreRequisiteService prereq = target.getAnnotation(PreRequisiteService.class);
		if( prereq != null ){
			for(Class req : prereq.value()){
				if( missing_services != null && missing_services.contains(req)){
					// already failed to get this one
					throw new ConsistencyError("Missing PreRequisite service "+req.getCanonicalName()+" needed by "+target.getCanonicalName());
				}
				if( services == null || ! services.containsKey(req)){
					if( missing_services == null ){
						missing_services= new HashSet<>();
					}
					missing_services.add(req);
					try{
						// make PreRequisite if we can
						AppContextService serv = makeService(req);
						if( serv != null ){
							setService(serv);
						}
					}catch(Exception e){
						throw new ConsistencyError("Failed to make missing PreRequisite service "+req.getCanonicalName()+" needed by "+target.getCanonicalName(),e);
					}
				}
			}
		}
	}
	public <S extends AppContextService<S>>void clearService(Class<S> clazz){
		if( services != null ){
			services.remove(clazz);
		}
	}
    @SuppressWarnings("unchecked")
	public <S extends AppContextService> S setService(S service){
    	if( service == null ){
    		return null;
    	}
    	Class<? extends AppContextService> clazz = service.getType();
    	if(services == null){
    		services=new LinkedHashMap<>();
    	}
    	try {
    		// even if we have already constructed the service we 
    		// can't be sure its going to work properly so we should check that
    		// the pre-requisites are in place when we install it. 
			checkPreRequisites(service.getClass());
		} catch (ConsistencyError e) {
			throw e;
		} catch (Exception e) {
			throw new ConsistencyError("Error making pre-requisites", e);
		}
    	if( missing_services != null ){
    		missing_services.remove(clazz);
    	}
    	return (S) services.put(clazz, service);
    }
	/**
	 * Check for a boolean parameter yes/on/true or no/off/false
	 * 
	 * @param name
	 *            String name of paameter
	 * @param def
	 *            boolean default if it does not exist.
	 * @return boolean
	 */
	public boolean getBooleanParameter(String name, boolean def) {
		String parm = getInitParameter(name);
		return parseBooleanParam(parm, def);
	}


	public static boolean parseBooleanParam(String parm, boolean def) {
		if (parm == null) {
			return def;
		}
		boolean res = def;
		parm = parm.trim();
		if (parm.equalsIgnoreCase("yes") || parm.equalsIgnoreCase("on")
				|| parm.equalsIgnoreCase("true")) {
			res = true;
		}
		if (parm.equalsIgnoreCase("no") || parm.equalsIgnoreCase("off")
				|| parm.equalsIgnoreCase("false")) {
			res = false;
		}
		return res;
	}

	
	
	public double getDoubleParam(String name, double def) {
		String parm = getInitParameter(name);
		if (parm == null) {
			return def;
		}
		double res = def;
		try {
			res = Double.parseDouble(parm.trim());
		} catch (NumberFormatException e) {
			error(e, "badly fomatted parameter " + name + " value [" + parm
					+ "]");
		}
		return res;
	}

	/**
	 * Query environmental parameter.
	 * 
	 * @param name
	 *            Name of parameter
	 * @return value or null.
	 */
	public final  String getInitParameter(String name){
		// Load the properties file if this has not already been done:
		ConfigService service = getService(ConfigService.class);
		if( service == null ){
			return null;
		}
		return service.getServiceProperties().getProperty(name);
	}
	
    /** Query an environmental parameter with nested parameter expansion
     * 
     */
	public final String getExpandedProperty(String name){
		String text_to_expand = getInitParameter(name);
		Map<String,String> values = new HashMap<>();
		values.put(name, "");
		return getPropertyExpander().expandText(values,text_to_expand);
	}
	public final String getExpandedProperty(String name,String def){
		String text_to_expand = getInitParameter(name,def);
		Map<String,String> values = new HashMap<>();
		values.put(name, "");
		return getPropertyExpander().expandText(values,text_to_expand);
	}

	/** perform config parameter expansion on a text fragment.
	 * 
	 * Text of the form ${name} is replaced with the corresponding config parameter
	 * A fall-back value can be specified by using ${name:fallback}
	 * 
	 * @param text_to_expand
	 * @return expanded text
	 */
	public String expandText(String text_to_expand) {
		return getPropertyExpander().expandText(new HashMap<String,String>(), text_to_expand);
	}
	private PropertyExpander getPropertyExpander() {
		Properties p =null;
		ConfigService cfg= getService(ConfigService.class);
		if( cfg != null) {
			p=cfg.getServiceProperties();
		}
		return new PropertyExpander(p);
	}
	
	/** Query a property with generic and specialised forms.
	 * First look for a property called <b>name.tag</b> If this does not exist 
	 * look for a property called <b>name</b>
	 * 
	 * @param name  base property name
	 * @param tag specialisation 
	 * @return String
	 */
	public final String getTaggedProperty(String name,String tag){
		ConfigService service = getService(ConfigService.class);
		if( service == null ){
			return null;
		}
		Properties prop = service.getServiceProperties();
		String result = prop.getProperty(name+"."+tag);
		if( result != null ){
			return result;
		}
		return prop.getProperty(name);
	}

	/**
	 * Query an environmental parameter returning a default value if not found
	 * 
	 * @param name
	 *            Name of parameter
	 * @param fallback
	 *            Default value
	 * @return String
	 */
	public final String getInitParameter(String name, String fallback) {
		ConfigService serv = getService(ConfigService.class);
		if( serv == null ){
			return fallback;
		}
		return serv.getServiceProperties().getProperty(name,fallback);
	}
	@SuppressWarnings("unchecked")
	public final <E extends Enum> E getEnumParameter(Class<E> clazz, String name, E fallback){
		if( name == null ){
			error("null passed to getEnumParameter "+clazz.getCanonicalName());
			return fallback;
		}
		String enum_name = getInitParameter(name.trim());
		if( enum_name == null ){
			return fallback;
		}
		try{
			enum_name=enum_name.trim();
			return (E) Enum.valueOf(clazz, enum_name);
		}catch(Exception t){
			error(t,"Error getting EnumParameter "+clazz.getCanonicalName()+" "+enum_name);
			return fallback;
		}
	}

	/**
	 * Get all environmental parameters starting with a given prefix.
	 * 
	 * @param prefix
	 *            String
	 * @return Hashtable
	 */
	public final Hashtable<String,String> getInitParameters(String prefix){
		// Load the properties file if this has not already been done:
		ConfigService service = getService(ConfigService.class);
		if( service == null ){
			return new Hashtable<>();
		}
		Properties service_props = service.getServiceProperties();
	
		// Map the service properties into a hashtable
		Hashtable<String,String> h = new Hashtable<>();
		Enumeration e = service_props.propertyNames(); 
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (name.startsWith(prefix)) {
				h.put(name, service_props.getProperty(name));
			}
		}
		return h;
	}

	public int getIntegerParameter(String name, int def) {
		String parm = getInitParameter(name);
		if (parm == null) {
			return def;
		}
		int res = def;
		try {
			res = Integer.parseInt(parm.trim());
		} catch (NumberFormatException e) {
			error(e, "badly fomatted parameter " + name + " value [" + parm
					+ "]");
		}
		return res;
	}
	public long getLongParameter(String name, long def) {
		String parm = getInitParameter(name);
		if (parm == null) {
			return def;
		}
		long res = def;
		try {
			res = Long.parseLong(parm.trim());
		} catch (NumberFormatException e) {
			error(e, "badly fomatted parameter " + name + " value [" + parm
					+ "]");
		}
		return res;
	}


	/**
	 * return a copy of the Service properties associated with the AppContext
	 * 
	 * @return Properties
	 */
	public  Properties getProperties() {
		ConfigService service = getService(ConfigService.class);
		if( service == null ){
			return null;
		}
		return (Properties) service.getServiceProperties().clone();
	}
	


	/** Construct an object that implements Contexed  
	 * This interface implies that the object implements one of a number of 
	 * standard constructor signatures.
	 * @see Contexed
	 * 
	 * @param <T> type of object being created
	 * @param clazz 
	 * @param tag
	 * @return created object
	 * @throws Exception 
	 */
	private <T extends Contexed> T makeContexedObject(Class<T> clazz, String tag) throws Exception{
		if( tag == null ){
			return makeContexedObject(clazz);
		}
		// We use the actual class here in case the target object needs a particular sub-class of AppContext
		Constructor<T> c = findConstructorFromParamSignature(clazz,getClass(),String.class );
		if( c == null ){
			return makeContexedObject(clazz);
		}
		if( (c.getModifiers() & (Modifier.PRIVATE|Modifier.PROTECTED))!=0 ){
			throw new ConsistencyError("Non visible constructor for "+clazz.getCanonicalName());
		}
		T obj = c.newInstance(this,tag);
		if( obj instanceof Tagged){
			if( ! ((Tagged)obj).getTag().equals(tag)){
				error("Tag "+tag+" returns tagged object with different tag");
			}
		}
		// if we implement tagged the tag should match the one returned
		// by the instance
		assert((!(obj instanceof Tagged)) || ((Tagged)obj).getTag().equals(tag));
		
		return obj;
	}
	/** Construct an object that implements Contexed using the non-tagged constructor
	 * (if it exists).
	 * 
	 * @param <T>
	 * @param clazz
	 * @return instance of T
	 * @throws Exception
	 */
	public <T extends Contexed> T makeContexedObject(Class<T> clazz) throws Exception{
		if( clazz == null ){
			return null;
		}
		Constructor<T> c;
		try{
			c= clazz.getConstructor( AppContext.class );
		}catch(NoSuchMethodException e){
			// look for constructor specific to this AppContext sub-class
			c =findConstructorFromParamSignature(clazz, getClass());
		}
		if( c == null ){
			error("Cannot make Contexted object no valid constructor "+clazz.getCanonicalName());
			return null;
		}
		return  c.newInstance(this);
	}
	
	/** Method to check a class implements one of the supported constructors
	 * for {@link #canMakeContexedObject(Class)}
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends Contexed> boolean canMakeContexedObject(Class<T> clazz){
		return findConstructorFromParamSignature(clazz, getClass()) != null;
	}
	/** Create an instance from a class.
	 * The target object is assumed to either have a public no argument constructor or to 
	 * implement Contexed and take an AppContext as a constructor parameter.
	 * @see Contexed
	 * 
	 * @param <T> type to produce
	 * @param clazz Class of type to create
	 * @return instance of T
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <T> T makeObject(Class<T> clazz) throws Exception {
			
		  if( Contexed.class.isAssignableFrom(clazz)){
			  Class<? extends Contexed> clazz2 = (Class<? extends Contexed>) clazz;
			  return (T) makeContexedObject(clazz2);
		  }else{
			  return  clazz.newInstance();
		  }
		
	}
	/** Method to check if a class implements one of the permitted constructors for {@link #makeObject(Class)}
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> boolean canMakeObject(Class<T> clazz){
		 if( Contexed.class.isAssignableFrom(clazz)){
			 return canMakeContexedObject((Class)clazz);
		 }else{
			 return findConstructorFromParamSignature(clazz) != null;
		 }
	}
	
	
	
	/** Construct an object based on a supplied parameter list.
	 * This is a convenience routine that searched for a compatible constructor.
	 * It used the first constructor where the supplied parameters may be assigned to the
	 * parameter types of the constructor.
	 * 
	 * @param <T>
	 * @param clazz Class of object to make
	 * @param param parameter list
	 * @return new instance
	 * @throws Exception
	 */
		
	public <T> T makeParamObject(Class<T> clazz, Object... param) throws Exception{
		if( clazz == null ){
			throw new NullPointerException("Null class parameter");
		}
		Constructor<T> c = findConstructor(clazz, param);
		if( c == null){
			StringBuilder sb = new StringBuilder();
			sb.append("No matching signature found ");
			sb.append(clazz.getCanonicalName());
			sb.append(" params:");
			for( Object o : param){
				sb.append(" ");
				if( o != null ){
				  sb.append(o.getClass().getCanonicalName());
				}else{
					sb.append("null");
				}
			}
			throw new NoSuchMethodException(sb.toString());
		}
		return c.newInstance(param);
	}
	/** Search for a matching constructor considering supertypes
	 * 
	 * @param <T>
	 * @param clazz
	 * @param param
	 * @return Constructor or null
	 */

	public <T> Constructor<T> findConstructor(Class<T> clazz, Object... param){
		if( clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			error("Cannot get construtor for abstract/interface class "+clazz.getCanonicalName());
			return null;
		}
		Class sig[] = new Class[param.length];
		for(int i=0;i<param.length;i++){
			if( param[i] == null ){
				sig[i] = null;
			}else{
				sig[i]=param[i].getClass();
			}
		}
		return findConstructorFromParamSignature(clazz, sig);
	}
	/** Search for Constructor considering super-types
	 * 
	 * @param <T>
	 * @param clazz
	 * @param param array of class types from parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Constructor<T> findConstructorFromParamSignature(Class<T> clazz,Class ... param ){
		//Logger log = getLogger();
		for(  Constructor<?>  c : clazz.getConstructors()){
			//log.debug("consider "+c.toGenericString());
			Class<?>[] types = c.getParameterTypes();
			if( param.length == types.length){
				//log.debug("param length matches");
				boolean match=true;
				for(int i=0;i<param.length && match;i++){
					if( param[i] != null && ! types[i].isAssignableFrom(param[i])){
						//log.debug("param "+i+" does not match, expecting "+types[i].getCanonicalName()+" got "+param[i].getCanonicalName());
						match = false;
					}
				}
				if( match ){
					return (Constructor<T>) c;
				}
			}
		}
		return null;
	}
	
	/**Construct an object identified by a string tag.
	 * 
	 * The exact class to construct can be set using the property
	 * <b>class.<em>tag</em></b> 
	 * If the property is not set then the default_class is constructed.
	 * If the default class cannot be instantiated null is returned.
	 * 
	 * 
	 * It is quite common to need to parameterise the constructed class using the tag.
	 * We therefore look for a string parameterised constructor in preference.
	 * If such a constructor is found:
	 *  
	
	
	 * @param <T>  return type 
	 * @param clazz  default super class 
	 * @param tag 
	 * @return instance
	 */
	public final <T> T makeObject(Class<T> clazz, String tag){
		if( clazz.isInterface() ) {
			// can't use as default so use null
			T result =  makeObjectWithDefault(clazz, null, tag);
			if( result == null && Composable.class.isAssignableFrom(clazz)) {
				// look for a composite
				DataObjectFactory<?> fac = makeObjectWithDefault(DataObjectFactory.class, null, tag);
				if( fac != null ) {
					//Interface cannot be the same as registration class because it can't extends Composite
					// search for appropriate composite and return the first one found.
					for(T x : fac.getComposites(clazz)) {
						result = x;
						break;
					}
				}
			}
			return result;
		}else if( Modifier.isAbstract(clazz.getModifiers())){
			// can't use as default so use null
			return makeObjectWithDefault(clazz, null, tag);
		}
		return makeObjectWithDefault(clazz, clazz, tag);
	}
	
	
	
	/** Construct an object identified by a string tag.
	 * 
	 * The exact class to construct can be set using the property
	 * <b>class.<em>tag</em></b> 
	 * If the property is not set then the default_class is constructed.
	 * If the default class cannot be instantiated null is returned.
	 * 
	 * 
	 * It is quite common to need to parameterise the constructed class using the tag.
	 * We therefore look for a string parameterised constructor in preference.
	 * If such a constructor is found:
	 *  
	
	 * @param <T>  return type 
	 * @param clazz  return type class 
	 * @param default_class 
	 * @param tag 
	 * @return instance
	 */
	public final <T> T makeObjectWithDefault(Class<T> clazz, Class<? extends T> default_class, String tag){
		return makeObjectWithDefault(clazz, default_class,null, tag);
	}
	/** Special class to key objects created by {@link AppContext#makeObjectWithDefault(Class, Class, String, String)}
	 * stored as attributes.
	 * It is a private class to prevent these being accessed other than via the {@link AppContext} methods.
	 * We only key on the path and tag as this is sufficient to identify the class.
	 * 
	 * @author Stephen Booth
	 *
	 */
	private static final class ObjectCacheKey{
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjectCacheKey other = (ObjectCacheKey) obj;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			return true;
		}
		/**
		 * @param path
		 * @param tag
		 */
		public ObjectCacheKey(String path, String tag) {
			super();
			this.path = path;
			this.tag = tag;
		}
		private final String path;
		private final String tag;
	}
	/** Construct an object identified by a string tag and an optional qualifier string.
	 * <p>
	 * The qualifier string if for cases where it is convenient to use the same tag string
	 * for various related classes.  
	 * <p>
	 * The exact class to construct can be set using the property
	 * <b>class.<em>path</em>.<em>tag</em></b> 
	 * Or (if path is null or the property is not set)
	 * <b>class.<em>tag</em></b> 
	 * If neither property is not set then the default_class is constructed.
	 * If the default class cannot be instantiated null is returned.
	 * 
	 * 
	 * It is quite common to need to parameterise the constructed class using the tag.
	 * We therefore look for a string parameterised constructor in preference.
	 * If such a constructor is found.
	 *  
	
	 * @param <T>  return type 
	 * @param clazz  return type class 
	 * @param default_class
	 * @param path 
	 * @param name 
	 * @return instance
	 */
	@SuppressWarnings("unchecked")
	public final <T> T makeObjectWithDefault(Class<T> clazz, Class<? extends T> default_class,String path, String name){
		
		String tag=name;
		if( path != null && name != null && path.length() > 0 && name.length() > 0){
			tag = path+"."+name;
		}
		TimerService timer = getService(TimerService.class);
		String timer_name;
		if( tag != null ){
			timer_name="makeObjectWithDefault."+tag;
		}else{
			timer_name="makeObjectWithDefault."+clazz.getCanonicalName();
		}
		T result=null;
		ObjectCacheKey key = null;
		if( tag != null  && ContextCached.class.isAssignableFrom(clazz)&&CONTEXT_CACHE_FEATURE.isEnabled(this)) {
			key = new ObjectCacheKey(path, tag);
			Object o = getAttribute(key);
			if( o != null) {
				if( clazz.isAssignableFrom(o.getClass())) {
					return (T) o;
				}else {
					error("Unexpected class "+o.getClass().getCanonicalName()+" not assignable to "+clazz.getCanonicalName()+" tag:"+tag);
					removeAttribute(key);
				}
			}
		}
		Class<? extends T> target = getPropertyClass(clazz,default_class,tag);
		if( target == null ){
			// try without path
			target = getPropertyClass(clazz,default_class,name);
		}
		if( target == null ){
			return null;
		}
		try{
			if(timer != null ){
				timer.startTimer(timer_name);
			}
			if( Contexed.class.isAssignableFrom(target)){
				result =  (T) makeContexedObject((Class<? extends Contexed>)target, name);
			}else{
				Constructor<? extends T> c = findConstructor(target, name);
				if( c != null ){
					result =  c.newInstance(name);
				}else{
					result =  target.newInstance();
				}
			}
			if( key != null ){
				setAttribute(key, result);
			}
			if( result instanceof Tagged){
				if( TAG_CHECK_FEATURE.isEnabled(this) && ! name.equals(((Tagged)result).getTag())){
					error("tag missmatch "+name+"->"+((Tagged)result).getTag()+" "+result.getClass().getCanonicalName());
				}
			}
			return result;
		}catch(Exception e){
			error(e,"Error making class "+target.getCanonicalName());
			return null;
		}finally{
			if(timer != null ){
				timer.stopTimer(timer_name);
			}
		}
	}
	/** lookup a desired implementation class using the Config service.
	 * When tag is not null this method looks up the property <b>class.</b><em>tag</em> interpreting the result as a class name.
	 * If this class name exists, can be instantiated and extends or implements the template class then this class is returned.
	 * Otherwise this method will return either the template class (if it can be instantiated) or null.
	 * 
	 * It it a common convention to use the database table name of the target as the tag.
	 * 
	 * @param <T> type that the target class must extend or implement
	 * @param template  Class object for T
	 * @param tag  
	 * @return Class of target or null.
	 */
	
	public <T> Class<? extends T> getPropertyClass(Class<T> template, String tag){
		return getPropertyClass(template,template,tag);
	}
	/** lookup a desired implementation class using the Config service.
	 * When tag is not null this method looks up the property <b>class.</b><em>tag</em> to determine the target class.
	 * This can either be a fully qualified java class name or (if not qualified) then it can also be the name of a class definition parameter.
	 * <b>classdef.</b><em>name</em> defining the fully qualified name. 
	 * If the target class name exists, can be instantiated and extends or implements the template class then this class is returned.
	 * Otherwise this method will return either the default_class (if it can be instantiated) or null.
	 * <p>
	 * The use of classdef parameters is preferred as it makes it easier to provide backwards compatibility when classes are renamed.
	 * It also allows 
	 * 
	 * @param <T> type that the target class must extend or implement
	 * @param template  Class object for T
	 * @param default_class Class to use if no property set
	 * @param tag
	 * @return Class of target or null.
	 */
	public final <T> Class<? extends T> getPropertyClass(Class<T> template, Class<? extends T> default_class,String tag){
		String class_name = null;
		
		
		if( tag != null ){
			tag=tag.trim();
			class_name=getInitParameter(CLASS_PREFIX+tag);
		}
		Class<? extends T> target=getClassFromName(template,default_class,class_name);
		
		if( target == null ){
			return null;
		}
		
		LoggerService service = getService(LoggerService.class);
		if( target.isInterface() ){
			if( tag == null ){
				error("Attempt to create interace "+target.getCanonicalName()+" without specifying config tag");
			}
			if( class_name == null){
				error("property "+tag+" not set when super is interface "+target.getCanonicalName());
			}
			if( service != null ){
				
				Logger log =service.getLogger(getClass());
				log.debug("getPropertyClass targetted Interface "+target.getCanonicalName()+" not class");
			}
			return null;
		}
	
		if( Modifier.isAbstract(target.getModifiers()) ){
			if( class_name == null){
				error("property "+tag+" not set when super is abstract "+target.getCanonicalName());
			}
			if( service != null){
				Logger log =service.getLogger(getClass());
				log.debug("getPropertyClass "+tag+" targetted abstract class "+target.getCanonicalName());
			}
			return null;
		}
		return target;
	}
	public List<Class> getClassList(Class template,String name){
		LinkedList<Class> result = new LinkedList<>();
		String list = getExpandedProperty(name);
		if( list == null || list.isEmpty()) {
			return result;
		}
		for(String tag: list.split("\\s*,\\s*")) {
			Class c = getPropertyClass(template,null, tag);
			if( c == null) {
				c = getClassDef(template, tag);
			}
			if( c != null ) {
				result.add(c);
			}
		}
		return result;
	}
	/** Generate a map of tags to classes assignable to (or containing
	 * an assignable {@link Composable} {@link Composite} of) a target type
	 * This will only find classes registered with the config service.
	 * @param <T> type being looked for.
	 * @param template Class of target.
	 * @return Map of tag to class
	 */
	public <T> Map<String,Class> getClassMap(Class<T> template){
	
		Map<String,Class> result = new TreeMap<>();
		Map<String,String> tagmap = getInitParameters(CLASS_PREFIX);
		for(String s : tagmap.keySet()){
			assert(s.startsWith(CLASS_PREFIX));
			try{
				String tag = s.substring(CLASS_PREFIX.length());
				// Only single string tags
				if( ! tag.contains(".")){
					// no template here as we are looking up the FQN and
					// can't check for composites
					Class<? extends T> cand = getClassDef(null, tagmap.get(s));

					if( checkTemplate(template, cand,tag)){

						result.put(tag, cand);
					}

				}
			}catch(Exception t){
				LoggerService serv = getService(LoggerService.class);
				if( serv != null){
					// make this a non fatal error in tests
					Logger log = serv.getLogger(getClass());
					log.warn("Error making classmap for key "+s,t);
				}
			}
		}
		return result;
	}
		/** rules to check if a candidate class matches a template
		 * 
		 * A null template always matches. a null candidate always fails.
		 * if tag is non null and the template implements {@link Composable} then
		 * composites are checked for a match as well
		 * 
		 * @param template      can be null
		 * @param candidate
		 * @return
		 */
	private boolean checkTemplate(Class template, Class candidate,String tag) {
		if( candidate == null) {
			return false;
		}
		if( template == null) {
			return true;
		}
		if( template.isAssignableFrom(candidate) ) {
			return true;
		}else {
			if( tag != null && Composable.class.isAssignableFrom(template) && DataObjectFactory.class.isAssignableFrom(candidate)) {
				// have to check composites.
				for(Class c : getClassList(Composite.class, tag+DataObjectFactory.COMPOSITES_SUFFIX)) {
					if( template.isAssignableFrom(c)) {
						return true;
					}
				}
				// now static composites, only works if available as an accessible field of the right type.
				for(Field f : candidate.getFields()) {
					if( template.isAssignableFrom(f.getType()) && Composite.class.isAssignableFrom(f.getType())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/** resolve a class-name into a class.
	 * This will attempt expand classdef definitions if the name is in the default package. 
	 * 
	 * If a default_class is specified this will be returned if no definition is found
	 * in the configuration service. And a {@link ClassCastException} will be thrown if 
	 * a definition incompatible with the template is found. Without a default_class null is returned in both cases.
	 * 
	 * 
	 * Note this can take fully qualified class names so it is safer not to allow the
	 * class_name to be set from untrusted content. Instead use {@link #getPropertyClass}
	 * which ensures only classes registered with the {@link ConfigService} can be generated.
	 * 
	 * 
	 * 
	 * @param <T>
	 * @param template
	 * @param default_class result to use if no definition
	 * @param tag  Either a fully qualified class name or a classdef tag.
	 * @return target class or null
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<? extends T> getClassFromName(Class<T> template, Class<? extends T> default_class,String tag){
	
		String class_name=null;
		Class<? extends T> target=default_class; // default to the target
		if( tag != null) {
			if( tag.contains(".")){
				// tag might be a bare class name use as default
				class_name=tag;
				//tag = null;  // supress Composable check
			}else {
				// try looking up a classdef
				tag=tag.trim();
				class_name=getInitParameter(CLASSDEF_PROP_PREFIX+tag);
			}
		}
		//log.debug("class name for "+tag+" is "+class_name);
		if( class_name != null ){
			class_name=class_name.trim();
			if( class_name.length() > 0){
				Class t = null;
				try {
					t = Class.forName(class_name);
				} catch (ClassNotFoundException e) {
					error(e,"Class "+class_name+" not found");
				}
				if(t != null ){
					// don't check for composites here as we want a
					// class assignable to the template not one that is just cast-able
					if( checkTemplate(template, t,null)){
						target=t; // Also OK meets constraints
					}else{
						if( default_class == null ){
							// assume we are just querying the definitions and
							// can handle a null result.
							return null;
						}
						// This is bad class defined but does not conform to target
						// the existence of a default_class implies we are going to try
						// constructing the class returned 
						throw new ClassCastException(template.getCanonicalName()+" not assignable from "+t.getCanonicalName()+" resolved from "+tag);
					}
				}
			}
		}
		
		return target;
	}
	public <T> Class<? extends T> getClassDef(Class<T> template, String name){
		return getClassFromName(template, null, name);
	}
	
	
	
	
   
	

	

	/**
	 * remove an object cached in the AppContext
	 * 
	 * @param key
	 */
	public final void removeAttribute(Object key) {
		if (attributes != null) {
			attributes.remove(key);
		}
	}

	/**
	 * method for caching an object in the AppContext Code should never assume
	 * the existance of an attribute this should only be used for short term
	 * caching to avoid unecessary database queries.
	 * <p>
	 * We use Object as the key instead of String so that classes can create private
	 * key objects (e.g. an Enum) to ensure cached objects are not accessible outside the class. 
	 * 
	 * @param key
	 *            used to identify attribute
	 * @param value
	 *            Object to be stored.
	 */
	public final void setAttribute(Object key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(key, value);
	}

	public final void clearAttributes(){
		if( attributes != null ){
			attributes.clear();
		}
		attributes=null;
	}
	

	public final void removeCached(String path,String tag) {
		removeAttribute(new ObjectCacheKey(path, tag));
	}
	
}