//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.session;


import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.ConcatSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.ConstExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterAdd;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;



/**
 * @author spb
 * @param <T> 
 *
 */

public class DatabasePasswordComposite<T extends AppUser> extends PasswordAuthComposite<T> implements  AnonymisingComposite<T>{
	protected static class PasswordStatus extends BasicType<PasswordStatus.Value> {
	    class Value extends BasicType.Value {
			private Value(String tag, String name) {
				super(PasswordStatus.this,tag, name);
			}
		}

		private PasswordStatus() {
			super("PasswordStatus");
		}
	}
	protected static final Feature JAVA_HASH = new Feature("java_password_hash",true,"process password hashes in java");
	protected static final Feature SALT_FIRST_FEATURE = new Feature("salt_first", false, "Salt comes first in password hash");
	public static final Feature NON_RANDOM_PASSWORD = new Feature("password.non-random",false,"Force randomly chosen passwords to be a series of x's (for bootstapping without email access)");
	public static final Feature LOG_RANDOM_PASSWORD = new Feature("password.log-random",false,"Log randomly generated passwords (for bootstrapping without email access)");
	public static final Feature NOTIFY_PASSWORD_LOCK = new Feature("password.notify-lock",true,"Notify be email if maximum password attempts are exceeded");
	/** config parameter name for maximum age of password.
	 * 
	 */
	private static final String PASSWORD_EXPIRY_DAYS = "password.expiry_days";
	public static final String SALT="Salt";
	public static final String ALG="Alg";
	public static final String PASSWORD = "Password";
	public static final String PASSWORD_FAILS = "PasswordFails";
	public static final String PASSWORD_CHANGED = "PasswordChanged";
	/** Number of characters generated randomly for initial passwords
	 * If ('a'-'z') + ('A'-'Z') + ('0'-'9') = 62 ~= 64 ~= 6 bits,
	 * 16 characters gives nearly 96 bits
	 * */
	public static final int GENERATED_PASSWORD_LENGTH = 16;
	static PasswordStatus p_status = new PasswordStatus();
	public static final PasswordStatus.Value VALID = DatabasePasswordComposite.p_status.new Value("V",
	"Valid");
	public static final PasswordStatus.Value INVALID = DatabasePasswordComposite.p_status.new Value("I",
	"Invalid");
	public static final PasswordStatus.Value FIRST = DatabasePasswordComposite.p_status.new Value("F",
	"First-use");
	
	
	/** return filer that matches all entries with the specified password
	 * 
	 * @param password
	 * @return
	 */
	protected BaseFilter<T> getPasswordFilter(String password){
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		if( DatabasePasswordComposite.JAVA_HASH.isEnabled(getContext())){
			log.debug("Using java hash");
			return new AcceptHashFilter(password);
		}
		try{
			Hash default_hash = Hash.getDefault(getContext());
			if( getRepository().hasField(DatabasePasswordComposite.ALG)){
				log.debug("Has alg field");
				SQLOrFilter<T> of = new SQLOrFilter<T>(getFactory().getTarget());
				AppContext conn = getContext();
				for(Hash h : Hash.values()){
					if( h.equals(default_hash) || h.isEnabled(conn)){
						SQLAndFilter<T> clause = new SQLAndFilter<T>(getFactory().getTarget());
						
						clause.addFilter(new SQLValueFilter<T>(getFactory().getTarget(),getRepository(), DatabasePasswordComposite.ALG, h.ordinal()));
						clause.addFilter(new SQLHashFilter(getContext(),h, password));
						of.addFilter(clause);
					}
				}
				return of;
			}else{
				
				log.debug("SQL filter for default hash "+default_hash.name());
				return new SQLHashFilter(getContext(),default_hash, password);
			}
		}catch(CannotUseSQLException e){
			return new AcceptHashFilter(password);
		}
	}
	/** An {@link AcceptFilter} that selects records that match a password.
	 * 
	 * This should be combined with a filter to select by username.
	 * 
	 * @author spb
	 *
	 */
	public class AcceptHashFilter implements AcceptFilter<T>{
		Logger log;
        public AcceptHashFilter(String password) {
			super();
			log = getContext().getService(LoggerService.class).getLogger(getClass());
			this.password = password;
		}

		private final String password;
        
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		public boolean accept(T o) {
			Handler handler = getHandler(o);
			Hash h = handler.getAlgorithm();
			if( h == null){
				getLogger().error("No hash algorithm for"+o.getIdentifier());
				return false;
			}
			log.debug("Checking "+o.getName());;
			log.debug("hash type is "+h.name());
			String check = password;
			
			if( useSalt()){
				log.debug("using salt");
				if( DatabasePasswordComposite.SALT_FIRST_FEATURE.isEnabled(getContext())){
					log.debug("salt is first");
					check= handler.getSalt()+check;
				}else{
					check=check+handler.getSalt();
				}
			}
			
			try {
				String crypt = handler.getCryptPassword();
				if( crypt == null ){
					log.error("Null crypt password for "+o.getName());
					return false;
				}
				String hash = h.getHash(check);
				boolean result = crypt.equals(hash);
				log.debug("result="+result);
				return result;
			} catch (NoSuchAlgorithmException e) {
				getLogger().error("bad hash",e);
				return false;
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
			return vis.visitAcceptFilter(this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<? super T> getTarget() {
			return getFactory().getTarget();
		}

		
	}
	/** Filter to select from a hashed (possibly salted) string.
	 * 
	 * @author spb
	 *
	 */
	public class SQLHashFilter implements SQLFilter<T>,PatternFilter<T>{
        
		private final Hash hash;
        private final String password;
        private final SQLExpression<String> check_value;
        public SQLHashFilter(AppContext conn,Hash hash,String password) throws CannotUseSQLException{
        	this.hash=hash;
        	this.password=password;
        	SQLExpression<String> c = new ConstExpression<String, String>(String.class, password,false);
        	if( useSalt()){
        		if( DatabasePasswordComposite.SALT_FIRST_FEATURE.isEnabled(getContext())){
					c = new ConcatSQLExpression(getRepository().getStringExpression(getTarget(),DatabasePasswordComposite.SALT),c);
				}else{
					c = new ConcatSQLExpression(c,getRepository().getStringExpression(getTarget(),DatabasePasswordComposite.SALT));
				}
        	}
        	try {
				SQLContext ctx = conn.getService(DatabaseService.class).getSQLContext();
				check_value=ctx.hashFunction(hash,c);
			} catch (SQLException e) {
				throw new CannotUseSQLException("Error getting context",e);
			}
        	
        }
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			return check_value.getParameters(list);
		}

		
		
		
		public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
			getRepository().getInfo(DatabasePasswordComposite.PASSWORD).addName(sb, qualify, false);
			sb.append("=");
			check_value.add(sb, qualify);
			return sb;
		}
		public void accept(T o) {
			
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((hash == null) ? 0 : hash.hashCode());
			result = prime * result
					+ ((password == null) ? 0 : password.hashCode());
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
			SQLHashFilter other = (SQLHashFilter) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (hash != other.hash)
				return false;
			if (password == null) {
				if (other.password != null)
					return false;
			} else if (!password.equals(other.password))
				return false;
			return true;
		}
		private AppUserFactory getOuterType() {
			return (AppUserFactory) getFactory();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
			return vis.visitPatternFilter(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<? super T> getTarget() {
			return getFactory().getTarget();
		}
    	
    }
	
	 public boolean useSalt(){
	    	boolean hasField = getRepository().hasField(DatabasePasswordComposite.SALT);
			return hasField;
	 }
	
	 /** The label for the LoginName input in the login form.
	     * 
	     * @return String label
	     */
	    public String getLoginNameLabel(){
	    	return "Email";
	    }	
		public Handler getHandler(T user){
			return new Handler(getRecord(user),user);
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.AppUser.Factory#getDefaults()
		 */
		@Override
		public Map<String, Object> addDefaults(Map<String,Object> h) {
			h.put(DatabasePasswordComposite.p_status.getField(), DatabasePasswordComposite.INVALID.getTag());
			h.put(PASSWORD,"locked");
			return h;
		}
		public String nonRandomString(int length) {
			StringBuilder sb = new StringBuilder();
			for( int i = 0 ; i < length ; i++){
				sb.append('x');
			}
			return sb.toString();
		}
		
		/** get the fail threshold at which account is locked
		 * @return
		 */
		private int getFailThreshold() {
			return getContext().getIntegerParameter("max_password_fails",0);
		} 
		/** A handler class for any database fields specific to the composite.
		 * 
		 * @author spb
		 *
		 */
		public class Handler {
			
			/**
			 * @param record
			 * @param user
			 */
			protected Handler(Record record, T user) {
				super();
				this.record = record;
				this.user = user;
			}
			private final Record record;
			private final T user;
			 public String getSalt(){
			    	return record.getStringProperty(DatabasePasswordComposite.SALT, "");
			    }
			 private void setPasswordStatus(PasswordStatus.Value v) {
					record.setProperty(DatabasePasswordComposite.p_status, v);
				}
			 
			 /**
				 * Sets a new SAF password for this person.
				 * 
				 * @param new_password
				 *            unencrypted value of new password.
				 * @throws DataFault
				 * 
				 * 
				 */
				public void setPassword(String new_password) throws DataFault {
					String salt="";
					AppContext conn = getContext();
					Hash h = Hash.getDefault(conn);
					if( h == null ){
						throw new DataFault("bad hash algorithm");
					}
					Repository res = record.getRepository();
					boolean use_salt = res.hasField(DatabasePasswordComposite.SALT);

					if( use_salt ){
						RandomService serv = getContext().getService(RandomService.class);
						salt=serv.randomString(res.getInfo(DatabasePasswordComposite.SALT).getMax());
						if( DatabasePasswordComposite.SALT_FIRST_FEATURE.isEnabled(conn)){
							new_password=salt+new_password;
						}else{
							new_password=new_password+salt;
						}
					}
					if( DatabasePasswordComposite.JAVA_HASH.isEnabled(conn)){
						try {
							setCryptPassword(h,salt,h.getHash(new_password));
						} catch (NoSuchAlgorithmException e) {
							throw new DataFault("bad hash algorithm", e);
						}
					}else{
						try {
							
							SQLContext sqlContext = getRepository().getSQLContext();
							// setProperty("Password", new_password);
							// Update the database directly using the Password field
							StringBuilder sb = new StringBuilder();
							sb.append("UPDATE ");

							res.addTable(sb, false);

							sb.append(" SET ");

							if( use_salt ){
								res.getInfo(DatabasePasswordComposite.SALT).addName(sb, false, false);
								sb.append("=? , ");
							}
							if( res.hasField(DatabasePasswordComposite.ALG) ){
								res.getInfo(DatabasePasswordComposite.ALG).addName(sb, false, false);
								sb.append("=? , ");
							}
							if( res.hasField(DatabasePasswordComposite.PASSWORD_CHANGED)){
								res.getInfo(DatabasePasswordComposite.PASSWORD_CHANGED).addName(sb, false, false);
								sb.append("=? , ");
							}
							res.getInfo(DatabasePasswordComposite.PASSWORD).addName(sb, false, false);
							SQLExpression<String> crypt = sqlContext.hashFunction(h, new ConstExpression<String, AppUser>(String.class,new_password , false));
							sb.append("=");
							crypt.add(sb, false);
							sb.append(" WHERE ");
							res.addUniqueName(sb, false, false);
							sb.append("=?");

							
							PreparedStatement stmt = sqlContext.getConnection().prepareStatement(
									sb.toString());
							int pos=1;
							if( use_salt ){
								stmt.setString(pos++, salt);

							}
							if( res.hasField(DatabasePasswordComposite.ALG)){
								stmt.setInt(pos++, h.ordinal());
							}
							if( res.hasField(DatabasePasswordComposite.PASSWORD_CHANGED)){
								CurrentTimeService cts = getContext().getService(CurrentTimeService.class);
								stmt.setLong(pos++,(cts.getCurrentTime().getTime())/res.getResolution() );
							
							}
							List<PatternArgument> list = crypt.getParameters(new LinkedList<PatternArgument>());
							for(PatternArgument arg : list){
								arg.addArg(stmt, pos++);
							}
							stmt.setInt(pos++, user.getID());
							// Don't want to log cleartext
							// getLogger().debug("Update = " + query);
							stmt.executeUpdate();
							stmt.close();
						} catch (CannotUseSQLException e) {
							getLogger().error("Error setting password by sql",e);
							try {
								setCryptPassword(h,salt,h.getHash(new_password));
							} catch (NoSuchAlgorithmException e2) {
								throw new DataFault("bad hash algorithm", e2);
							}
						} catch ( SQLException e2) {
							throw new DataFault("Error setting password",e2);
						}
					}
					setPasswordStatus(DatabasePasswordComposite.VALID);
					record.setOptionalProperty(DatabasePasswordComposite.PASSWORD_FAILS, 0);
					// actually we are setting these twice if dpoing the hash in SQL but it ensures the object
					// is valid after update.
					record.setOptionalProperty(DatabasePasswordComposite.SALT, salt);
					record.setOptionalProperty(DatabasePasswordComposite.ALG, h.ordinal());
					commit();
				}
				
				public boolean passwordFailsExceeded(){
					int fails= getFailCount();
					if( fails == 0 ){
						return false;
					}
					if( fails % getContext().getIntegerParameter("notify_password_fails", 20) == 0){
						// report as error
						getLogger().error("Large number of password fails for "+user.getIdentifier()+" "+fails);
					}
					int target = getFailThreshold();
					if( target > 0 && fails > target){
						
						return true;
					}
					return false;
				}
				
				/** get number of times password attempts have failed
				 * @return
				 */
				private int getFailCount() {
					return record.getIntProperty(DatabasePasswordComposite.PASSWORD_FAILS, 0);
				}
			    public void resetPasswordFails(){
			    	try{
			    	record.setOptionalProperty(DatabasePasswordComposite.PASSWORD_FAILS, 0);
			    	record.commit();
			    	}catch(DataException e){
			    		getLogger().error("Error resetting password fails",e);
			    	}
			    }
				

				private PasswordStatus.Value getPasswordStatus() {
					return record.getProperty(DatabasePasswordComposite.p_status);
				}
			    public String getCryptPassword(){
			    	return record.getStringProperty(DatabasePasswordComposite.PASSWORD,"");
			    }
			    
			    public Date getPasswordChanged(){
			    	return record.getDateProperty(PASSWORD_CHANGED);
			    }
			    /** method to set the encrypted password.
			     * This will only be used when importing hashes from
			     * an external system or when hashes are calculated in java.
			     * 
			     * @param salt
			     * @param crypt
			     */
			    public void setCryptPassword(Hash h,String salt,String crypt){
			    	record.setOptionalProperty(ALG, h.ordinal());
			    	record.setOptionalProperty(DatabasePasswordComposite.SALT, salt);
			    	record.setProperty(DatabasePasswordComposite.PASSWORD, crypt);
			    	record.setOptionalProperty(DatabasePasswordComposite.PASSWORD_FAILS, 0);
			    	record.setOptionalProperty(PASSWORD_CHANGED, getContext().getService(CurrentTimeService.class).getCurrentTime());
			    	setPasswordStatus(DatabasePasswordComposite.VALID);
			    }
			    public Hash getAlgorithm(){
			    	Repository res = record.getRepository();
			    	Hash default_hash = Hash.getDefault(getContext());
			    	if(res.hasField(DatabasePasswordComposite.ALG)){
			    		int ord = record.getIntProperty(DatabasePasswordComposite.ALG, -1);
			    		for(Hash h : Hash.values()){
			    			if( ord == h.ordinal()){
			    				if( default_hash.equals(h)){
			    					return h;
			    				}
			    				if(! h.isEnabled(getContext())){
			    					return null;
			    				}
			    				return h;
			    			}
			    		}
			    	}
			    	
					return default_hash;
			    }
			    
			   
				/**
				 * Is this user required to reset their password
				 * 
				 * @return boolean
				 */
				public boolean mustChangePassword() {
				
					PasswordStatus.Value v = getPasswordStatus();
					if (v == DatabasePasswordComposite.INVALID || v == DatabasePasswordComposite.FIRST) {
						return true;
					}
					Date last_changed = getPasswordChanged();
					if( last_changed != null ){
						int max_age_days = getContext().getIntegerParameter(PASSWORD_EXPIRY_DAYS, 0);
						if( max_age_days > 0 ){
							Calendar last = Calendar.getInstance();
							last.setTime(last_changed);
							last.add(Calendar.DAY_OF_YEAR, max_age_days); // date password expires
							CurrentTimeService cts = getContext().getService(CurrentTimeService.class);
							Date now = cts.getCurrentTime();
							if(now.after(last.getTime())){
								return true;
							}
						}
						
					}
					return false;
				}

			
				/**
				 * should the user get the welcome message.
				 * 
				 * @return boolean
				 */
				public boolean doWelcome() {
					return getPasswordStatus() == FIRST;
				}
				private void commit() throws DataFault{
					user.commit();
				}

		}
	/**
	 * @param fac
	 */
	public DatabasePasswordComposite(AppUserFactory<T> fac) {
		super(fac);
	}

	@Override
	public Set<String> addSuppress(Set<String> supress) {
		supress.add(DatabasePasswordComposite.p_status.getField());
		supress.add(DatabasePasswordComposite.PASSWORD);
		supress.add(DatabasePasswordComposite.PASSWORD_FAILS);
		supress.add(DatabasePasswordComposite.SALT);
		supress.add(DatabasePasswordComposite.ALG);
		supress.add(DatabasePasswordComposite.PASSWORD_CHANGED);
		return supress;
	}


	@Override
	public TableSpecification modifyDefaultTableSpecification(
			TableSpecification s, String table) {
		AppContext ctx=getContext();
		s.setField(DatabasePasswordComposite.PASSWORD_FAILS, new IntegerFieldType(false, 0));
		s.setField(DatabasePasswordComposite.p_status.getField(), DatabasePasswordComposite.p_status.getFieldType(DatabasePasswordComposite.INVALID));
		s.setField(DatabasePasswordComposite.SALT, new StringFieldType(false, "", ctx.getIntegerParameter("password.salt_length", 16)));
		Hash h = Hash.getDefault(ctx);
		s.setField(DatabasePasswordComposite.ALG, new IntegerFieldType(false, h.ordinal()));
		int length=512;
		try{
			length = h.getHash("text").length();
		}catch(NoSuchAlgorithmException e){
			getLogger().error("Default hash algorithm not supported",e);
		}
		s.setField(DatabasePasswordComposite.PASSWORD, new StringFieldType(false, "locked", length));
		s.setOptionalField(PASSWORD_CHANGED, new LongFieldType(true, null));
		return s;
	}
	/**
	 * Check if a string matches this persons password.
	 * 
	 * @param u
	 *            AppUser to check
	 * @param password
	 *            unencrypted password to check.
	 * @return true if password matches.
	 * 
	 */
	public boolean checkPassword(T u, String password) {
		if( u == null) {
			return false;
		}
		// route all password checks through the same code
		try {
			AndFilter<T> fil = new AndFilter<T>(getFactory().getTarget());
			fil.addFilter(getFactory().getFilter(u));
			fil.addFilter(getPasswordFilter(password));
			T temp = getFactory().find(fil,true);
			if (temp == null) {
				return false;
			}
			// is this the same person (paranoid check)
			return (temp.getID() == u.getID());
		} catch (DataException e) {
			return false;
		}
	}
	public final T findByLoginNamePassword(String email, String password)
			throws DataException {
				return findByLoginNamePassword(email, password, true);
				
			}
			public T findByLoginNamePassword(String email, String password,boolean check_fail_count)
					throws DataException {
				Logger log=getLogger();
				AndFilter<T> fil = new AndFilter<T>(getFactory().getTarget());
				SQLFilter<T> nameFilter = getLoginFilter(email);
				fil.addFilter(nameFilter);
				fil.addFilter(getPasswordFilter(password));
				T u = getFactory().find(fil,true);
				if( u == null){
					log.debug("password match returns null for "+email);
					//NO match
					if( check_fail_count && getRepository().hasField(DatabasePasswordComposite.PASSWORD_FAILS)){
						try {
							
							// atomic update via SQL
							FilterAdd<T> adder = new FilterAdd<T>(getRepository());
							adder.update(getRepository().<Integer,T>getNumberExpression(getFactory().getTarget(),Integer.class, DatabasePasswordComposite.PASSWORD_FAILS), Integer.valueOf(1), nameFilter);
							u = getFactory().find(nameFilter,true);
							if( u != null ) {
								int fails = getHandler(u).getFailCount();
								int thresh = getFailThreshold();
								if( thresh > 0 && fails == thresh+1 && NOTIFY_PASSWORD_LOCK.isEnabled(getContext())) {
									// This is the failure that crossed the threshold
									// count as at limit and we just incremented it.

									try {
										Emailer m = new Emailer(getContext());
										m.passwordFailsExceeded(u);
									} catch (Exception e) {
										getLogger().error("Failed to notify of password lock", e);
									}
								}
							}
						} catch (DataFault e) {
							getLogger().error("Error updating password fails",e);
						}
						
					}
					return null;
				}
				if (!u.canLogin()) {
					log.warn("user " + email + " not permitted to login");
					return null;
				}
				if( check_fail_count && getRepository().hasField(DatabasePasswordComposite.PASSWORD_FAILS)){
					DatabasePasswordComposite<T>.Handler handler = getHandler(u);
					if( handler.passwordFailsExceeded()){
						log.warn("user " + email + " exceeded max failed passwords");
						return null;
					}
					// successful login resets the fail count
					// provided the count has not been exceeded
					handler.resetPasswordFails();
				}
				log.debug("password success for "+email);
				return u;
			}

			/**
			 * @param email
			 * @return
			 */
			private SQLFilter<T> getLoginFilter(String email) {
				return ((AppUserFactory)getFactory()).getStringFinderFilter(email,true);
			}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#canResetPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean canResetPassword(T user) {
		if( user != null && user instanceof PasswordTarget){
			return ((PasswordTarget)user).canResetPassword();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#setPassword(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setPassword(T user, String password) throws DataFault {
		getHandler(user).setPassword(password);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#newPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void newPassword(T user) throws Exception {
		Handler h = getHandler(user);	
		
		Emailer m = new Emailer(getContext());
		m.newPassword(user, this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#newSignup(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public String randomisePassword(T user) throws DataFault{
		return randomisePassword(getHandler(user));
		
	}
	/**
	 * Sets the password to a random value and mark as requiring change
	 * 
	 * @return unencrypted password
	 * @throws DataFault
	 */
	private String randomisePassword(Handler h) throws DataFault{
		RandomService serv = getContext().getService(RandomService.class);
		String new_password = serv.randomString(DatabasePasswordComposite.GENERATED_PASSWORD_LENGTH);
		if( DatabasePasswordComposite.NON_RANDOM_PASSWORD.isEnabled(getContext())){
			new_password=nonRandomString(DatabasePasswordComposite.GENERATED_PASSWORD_LENGTH);
			getLogger().debug("Non random password "+new_password);
		}
		if( DatabasePasswordComposite.LOG_RANDOM_PASSWORD.isEnabled(getContext())){
			getLogger().debug("Person "+h.user.getIdentifier()+" password randomised to "+new_password);
		}
		if (!canResetPassword(h.user)) {
			// hack to give some indication if person removed
			new_password = "Account is disabled";
		} else {
			h.setPassword(new_password.trim());
			h.setPasswordStatus(DatabasePasswordComposite.INVALID);
			h.commit();
		}
		return new_password;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#newSignup(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public String firstPassword(T user) throws DataFault{
		Handler r = getHandler(user);
		RandomService serv = getContext().getService(RandomService.class);
		String new_password = serv.randomString(DatabasePasswordComposite.GENERATED_PASSWORD_LENGTH);
		if( DatabasePasswordComposite.NON_RANDOM_PASSWORD.isEnabled(getContext())){
			new_password=nonRandomString(DatabasePasswordComposite.GENERATED_PASSWORD_LENGTH);
			getLogger().debug("Non random initial password "+new_password);
		}
		if( DatabasePasswordComposite.LOG_RANDOM_PASSWORD.isEnabled(getContext())){
			getLogger().debug("Person "+r.user.getIdentifier()+" initial password randomised to "+new_password);
		}
		r.setPassword(new_password.trim());
		r.setPasswordStatus(DatabasePasswordComposite.FIRST);
		r.commit();
		return new_password;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#newSignup(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void lockPassword(T user) {
		getHandler(user).setCryptPassword(Hash.getDefault(getContext()),"", "Locked");
	}

	@Override
	public boolean doWelcome(T person) {
		return getHandler(person).doWelcome();
	
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#mustResetPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean mustResetPassword(T user) {
		return getHandler(user).mustChangePassword();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingComposite#anonymise(uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void anonymise(T target) {
		lockPassword(target);
		
	}
}