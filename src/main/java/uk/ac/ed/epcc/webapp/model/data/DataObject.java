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
 * Created on 27-Apr-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextIndexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/**
 * 
 * DataObject is the application facing view of a row in a database table.
 * <p>
 * This class
 * is sub-classed to create objects corresponding to particular tables.
 * Essentially this class is implemented as a wrapper round the
 * <code>Repository.Record</code> class. Where <code>Repository.Record</code> accesses 
 * database fields as named properties a <code>DataObject</code> subclass will typically 
 * provide conventional get and set methods.  
 * Sub-classes should implement these methods by calling 
 * <code>record.setProperty</code>/<code>record.get(Type)Property</code>.
 
 * <p>
 * The primary constructor is one that takes a <code>Record</code>
 * Unless the objects are only to be created from the appropriate factory
 * sub-classes need constructors that create new records or 
 * retrieve existing ones. <code>DataObject</code> contains several protected static methods
 * returning <code>Record</code> that can be used to implement these.  

 * <p>
 * In most cases there is a one to one correspondance between a Java class and a
 * SQL table. In these cases the table name can be hardwired into the
 * constructors. 
 * In a few cases we may want multiple tables to map to the same class. In this
 * case the constructors that specify the table name must be used or the objects should only be created via a factory.
 * 
 * <p>
 * Though it is possible to refer to DataObjects via their integer handle this should be done sparingly.
 * Any method call that takes an integer handle as a parameter is intrinsically less type safe then one that takes a
 * reference to the appropriate object. The {@link IndexedReference} class exists to implement type-safe lightweight references to 
 * objects (like {@link DataObject}) that implement {@link Indexed}
 * <p>
 * Most sub-classes will also override the <code>getIdentifier()</code> method. This generates
 * a unique text identifier for the <code>DataObject</code>. This is used in pull down menus etc.
 * so typically should reflect the end users concept of an objects name.
 * <p>
 * 
 */
public abstract class DataObject implements ContextIndexed, Identified, Releasable{
	protected static final boolean DEBUG = false;

	/** All the fields for this record. */
	public Repository.Record record;

	

    /** Helper method for subclass constructors.
     * 
     * Creates an empty Record.
     * 
     * @param ctx
     * @param table
     * @return uncommitted empty Record
     * @throws ConsistencyError
     */
	protected static Repository.Record getRecord(AppContext ctx,String table)
	throws ConsistencyError{

		Repository res=Repository.getInstance(ctx, table);
		Repository.Record record = res.new Record();

		return record;
	}
	/** Helper method for use in Subclass constructors.
	 * Make a new object from scratch. The hashtable contains the initial data
	 * for the object. It is entirely legal to add additional fields that are
	 * not represented in the database. These will simply be ignored.
	 * 
	 * Note that this mechanism only works for pre-existing tables. Use methods on
	 * a factory class rather than explicit constructors if you need automatic table creation.
	 * 
	 * 
	 * @param ctx
	 * @param values
	 * @throws ConsistencyError
	 * @throws DataFault
	 */
	protected static Repository.Record getRecord(AppContext ctx,String table, Map<String,Object> values)
			throws ConsistencyError, DataFault {

		Repository res=Repository.getInstance(ctx, table);
		Repository.Record record = res.new Record();
		record.putAll(values);
		record.commit();
		return record;
	}

	protected static Repository.Record getRecord(AppContext c,String table, int id) throws DataException {
		Repository res=Repository.getInstance(c, table);
		Repository.Record record = res.new Record();
		record.setID(id);
		return record;
	}

	

	
	/**
	 *  create an object using an existing Record
	 *  this is for use within the Factory class when a factory may return different 
	 *  sub-types.
	 * @param r
	 */
	protected DataObject(Repository.Record r){
		record = r;
	}
    
	/**
	 * Writes any changed values back to the database. Only values actually
	 * changed are written.
	 * 
	 * @return boolean true if changed
	 * @throws DataFault
	 */
	public final boolean commit() throws DataFault {
		pre_commit(record.isDirty());
		boolean changed = record.commit();
		post_commit(changed);
		return changed;
	}
	/** Extension point called at start of commit.
	 * It is legal to change the record in this call
	 * 
	 * @param dirty is record known to be modified.
	 */
	protected void pre_commit(boolean dirty) throws DataFault{
		
	}
	/** Extension point called at end of commit
	 * 
	 * @param changed record was changed by commit.
	 */
	protected void post_commit(boolean changed)throws DataFault{
		
	}

	/**
	 * Used to remove the data from the database - USE WITH CAUTION!
	 * 
	 * In particular deleting records from within an iterator may 
	 * throw off record chunking. Instead the object should implement {@link Removable}
	 * and be removed from iterator (which will recursively call delete).
	 * 
	 * @return <code>true</code> if the object was successfully deleted.
	 * @throws DataFault
	 */
	public boolean delete() throws DataFault {

		record.delete();
		record=null;
		return true;
	}

	@Override
	public final boolean equals(Object o) {
		if( o == null ){
			return false;
		}
		if (getID() == -1) {

			return super.equals(o);
		}
		if ((o instanceof DataObject)
				&& (getFactoryTag().equals(((DataObject) o).getFactoryTag()))
				&& (getID() == ((DataObject) o).getID())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * update this DataObject based on Form fields. fields not in both the form
	 * and the Object are ignored.
	 * 
	 * @param f
	 *            Form to use
	 */
	public void formUpdate(Form f) {
		Repository res = record.getRepository();
		boolean allow_bogus = res.setAllowBogusPut(true);
		for (Iterator<String> it = f.getFieldIterator(); it.hasNext();) {
			String key = it.next();
			record.setProperty(key, f.get(key));
		}
		res.setAllowBogusPut(allow_bogus);
	}
   
	protected final SQLContext getSQLContext() throws SQLException {
		return getDatabaseService().getSQLContext(record.getRepository().getDBTag());
	}
	/**
	 * @return
	 */
	protected final DatabaseService getDatabaseService() {
		return getContext().getService(DatabaseService.class);
	}

	/**
	 * Get the application context used to create this object.
	 * 
	 * @return AppContext used to create object
	 */
	public final AppContext getContext() {
		return record.getRepository().getContext();
	}


	/**
	 * create Hashtable of contents of Object
	 * 
	 * @return Hashtable
	 */
	public final Map<String,Object> getMap() {
		return getMap(false);
	}
	public final Map<String,Object> getMap(boolean include_null) {
		Map<String,Object> h = new HashMap<>();
		if (record != null) {
			// Want to record values for all fields even if null
			// don't want any bogus values
			for (String key : record.getRepository().getFields()) {
				if (key != null) {
					Object value = record.get(key);
					if ((value != null) || include_null) {
						h.put(key, value);
					}
				}
			}
		}
		return h;
	}

	/**
	 * Returns the unique ID for this particular object.
	 * 
	 * @return int the ID number
	 * 
	 * 
	 */
	public final int getID() {
		try {
			return record.getID();
		} catch (ConsistencyError e) {
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Identified#getIdentifier()
	 */
	public String getIdentifier(int max_length) {
		return record.getRepository().getTag()+"." + getID();
	}
	public final String getIdentifier(){
		return getIdentifier(Identified.MAX_IDENTIFIER);
	}

	protected uk.ac.ed.epcc.webapp.logging.Logger getLogger() {
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}

	protected final String getFactoryTag() {
		return record.getRepository().getTag();
	}

	/**
	 * Returns the field name for the unique ID field.
	 * 
	 * @return String of the unique ID name
	 */
	protected final String getUniqueIdName() {
		return record.getRepository().getUniqueIdName();
	}
	

	@Override
	public final int hashCode() {
		// Using the Database ID is consistent with the definition of equals
		// above.
		// two objects that equal each other will have the same hashcode.
		int id;

		id = getID();
		if (id == -1) {
			return super.hashCode();
		}
		return id;
	}


	/**
	 * extension point to allow sub-classes to add additional setup after an
	 * object has been created from a creation form.
	 * 
	 */
	protected final void postCreate() {
		return;
	}

	/** This is used like a destructor - means less connections, etc lying around 
	 * This will also clear the record from the {@link Repository} cache if this is enabled
	 * 
	 * */
	@Override
	public void release() {
		if( record != null ){
			// Note this will throw LockedRecordException if locked.
		  record.clear();
		  record = null;
		}
	}

	/** Lock record so it is unmodifiable.
	 * 
	 * This is intended for when {@link DataObject}s are being cached to reduce risk
	 * 
	 * @throws DataFault
	 */
    protected void lock() throws DataFault {
    	if( record != null ) {
    		record.lock();
    	}
    }
	protected boolean isLocked() {
		if( record != null) {
			return record.isLocked();
		}
		return false;
	}

	/**
	 * update multiple fields at once using a Map
	 * 
	 * @param m
	 *            Map containing update
	 */
	public final void setContents(Map<String,Object> m) {
		record.putAll(m);
	}


	protected void setDirty(String key, boolean value) {
		record.setDirty(key, value);
	}
	@Override
	public String toString(){
		return getClass().getCanonicalName()+" "+record.toString();
	}

	/**
	 * Use Hashtable to update Object. does not check for unchanged fields
	 * unforunatly. missing fields are removed
	 * 
	 * @param h
	 */
	public final void setMap(Map<String, Object> h) {
		record.set(h);
	}

	
	/**
	 * Utility routine to merge 2 Hashtables
	 * 
	 * @param table
	 *            Primary table to be modified
	 * @param values
	 *            additional table to be merged values here never overwrite
	 * @return reference to table.
	 */
	public static Map<String,Object> addToMap(Map<String,Object> table, Map<String,Object> values) {
		if (table != null) {
			if (values != null) {
				for(String key : values.keySet()){
					if (!table.containsKey(key)) {
						table.put(key, values.get(key));
					}
				}
			}
			return table;
		} else {
			return values;
		}
	}

	/**
	 * A utility method for putting values into hashtables in the constructors
	 * 
	 * @param table
	 * @param values
	 *            array of key value pairs to be added if not already defined
	 * @return Hashtable
	 */
	public static Map<String,Object> addToMap(Map<String,Object> table, Object[][] values) {
		if (table != null) {
			for (int i = 0; i < values.length; i++) {
				if (table.get(values[i][0]) == null) {
					table.put((String)values[i][0], values[i][1]);
				}
			}
			return table;
		} else {
			Map<String,Object> newtable = new HashMap<>();
			for (int i = 0; i < values.length; i++) {
				newtable.put((String)values[i][0], values[i][1]);
			}
			return newtable;
		}
	}

	/**
	 * A utility method for putting values into hashtables in the constructors
	 * only modifies table if key not already in use.
	 * 
	 * @param table
	 *            original Hashtable
	 * @param name
	 *            key to use
	 * @param value
	 *            added
	 * @return Hashtable
	 */
	public static Map<String,Object> addToMap(Map<String,Object> table, String name,
			Object value) {
		if (table.get(name) == null) {
			table.put(name, value);
		}
		return table;
	}

	public static boolean empty(String input) {
		return ((input == null) || (input.length() == 0));
	}
	/** A static method to locate the {@link DataObjectFactory} used to create a {@link DataObject}
	 * This will be necessary when behaviour added as {@link Composite}s is required but
	 * only the {@link DataObject} is available.
	 * 
	 * 
	 * @param conn
	 * @param obj
	 * @return
	 */
	public static <D extends DataObject> DataObjectFactory<D> getOwningFactory( D obj){
		if( obj == null ) {
			return null;
		}
		if( obj instanceof Owned) {
			return ((Owned)obj).getFactory();
		}
		try {
			return obj.getContext().makeObject(DataObjectFactory.class, obj.getFactoryTag());
		} catch (Exception e) {
			return null;
		}
	}
}