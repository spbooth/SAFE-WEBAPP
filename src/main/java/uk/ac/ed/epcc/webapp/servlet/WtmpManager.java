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
package uk.ac.ed.epcc.webapp.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.DateTransform;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.ValueResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * Table to track application logins
 * 
 * @author spb
 * 
 */


public class WtmpManager extends DataObjectFactory<WtmpManager.Wtmp> implements AnonymisingFactory{
	/**
	 * 
	 */
	private static final String END_COL = "End";

	/**
	 * 
	 */
	private static final String START_COL = "Start";

	/**
	 * 
	 */
	private static final String BROWSER_COL = "Browser";

	/**
	 * 
	 */
	private static final String NAME_COL = "Name";

	/**
	 * 
	 */
	private static final String HOST_COL = "Host";

	/**
	 * 
	 */
	private static final String REAL_PERSON_COL = "Real person";

	/**
	 * 
	 */
	private static final String PERSON_COL = "Person";

	private static final String END_TIME = "EndTime";

	private static final String START_TIME = "StartTime";
	private static final String BROWSER = BROWSER_COL;

	private static final String HOST = HOST_COL;

	private static final String PERSON_ID = "PersonID";
	private static final String SUPER_PERSON_ID = "SuperPersonID";
	public class DateFilter extends SQLAndFilter<Wtmp> {

		public DateFilter(Date point) {
			super(WtmpManager.this.getTarget());
			addFilter(new TimeFilter(START_TIME,MatchCondition.LT,point));
			addFilter(new TimeFilter(END_TIME,MatchCondition.GT,point));
		}

	}

	public final class Wtmp extends DataObject {


		

		public Wtmp(Repository.Record res) {
			super(res);
		}

		public String getBrowser() {
			return record.getStringProperty(BROWSER);
		}



		public Date getEndTime() {
			return record.getDateProperty(END_TIME);
		}

		public String getHost() {
			return record.getStringProperty(HOST);
		}

		/** return text DNS name if known
		 * @return DNS name or null
		 * 
		 */
		public String getDNSName() {
			try {
				String h = getHost();
				InetAddress a = InetAddress.getByName(h);
				String name = a.getHostName();
				if( name != null && ! name.equals(h)) {
					return name;
				}
			} catch (UnknownHostException e) {
				return null;
			}
			return null;
		}


		public AppUser getPerson()  {
			SessionService serv = getContext().getService(SessionService.class);

			if( serv != null ){
				try {
					return (AppUser) serv.getLoginFactory().find(
							record.getNumberProperty(PERSON_ID));
				} catch (Exception e) {
					return null;
				}
			}
			return null;
		}
		public AppUser getSuperPerson()  {
			SessionService serv = getContext().getService(SessionService.class);
			Number id = record.getNumberProperty(SUPER_PERSON_ID);
			if( serv != null && id != null ){


				return (AppUser) serv.getLoginFactory().find(
						id);

			}
			return null;
		}


		public Date getStartTime() {
			return record.getDateProperty(START_TIME);
		}

		public void setBrowser(String h) {
			record.setProperty(BROWSER, h);
		}

		public void setEndTime(Date d) {
			record.setProperty(END_TIME, d);
		}

		public void setHost(String h) {
			record.setProperty(HOST, h);
		}

		public void setPerson(AppUser p) {
			record.setProperty(PERSON_ID, p.getID());
		}
		public void setSuperPerson(AppUser p) {
			if( p == null) {
				return;
			}
			record.setOptionalProperty(SUPER_PERSON_ID, p.getID());
		}
		public void setStartTime(Date d) {
			record.setProperty(START_TIME, d);
		}

		/** update the Wtmp entry
		 * 
		 * @return true if modified
		 * @throws DataFault
		 */
		public boolean update() throws DataFault {
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Date n = time.getCurrentTime();
			if (n.after(getEndTime())) {
				setEndTime(new Date(n.getTime() + Window));
				return commit();
			}
			return false;
		}

		public void logout() throws DataFault {
			CurrentTimeService cts = getContext().getService(CurrentTimeService.class);
			setEndTime(cts.getCurrentTime());
			for(LogoutListener l : WtmpManager.this.getComposites(LogoutListener.class)){
				l.logout(this);
			}
			commit();
		}

	}

	public static final String WTMP_SESSION_KEY = "Wtmp";
	public static final String WTMP_DATE_SESSION_KEY="WtmpDate";


	private static final long Window = 1800000L; // 30 min

	public WtmpManager(AppContext ctx, String homeTable) {
		 setContext(ctx, homeTable);
	}
	@Override
	  public TableSpecification getDefaultTableSpecification(AppContext ctx,
   			String homeTable) {
		  TableSpecification spec = new TableSpecification();
		  spec.setField(PERSON_ID, new ReferenceFieldType(false, ctx.getService(SessionService.class).getLoginFactory().getTag()));
		  spec.setOptionalField(SUPER_PERSON_ID, new IntegerFieldType());
		  spec.setField(START_TIME, new DateFieldType(true, null));
		  spec.setField(END_TIME, new DateFieldType(true, null));
		  spec.setField(BROWSER, new StringFieldType(true, null, 512));
		  spec.setField(HOST, new StringFieldType(true, null, 128));
		  try {
			  Index i = spec.new Index("login_index", false, PERSON_ID, END_TIME);
		  } catch (InvalidArgument e) {
			  ctx.getService(LoggerService.class).getLogger(getClass()).error("Cannot make index",e);
		  } 
		  return spec;
	  }
	public FilterResult<Wtmp> getCurrent() throws DataFault {
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		return getResult(new DateFilter(time.getCurrentTime()));
	}

	public FilterResult<Wtmp> getLoginHistory(AppUser person) throws DataFault{
		OrFilter<Wtmp> fil = new OrFilter<WtmpManager.Wtmp>(getTarget(), this);
		fil.addFilter(new ReferenceFilter<>(WtmpManager.this, PERSON_ID, person));
		if( res.hasField(SUPER_PERSON_ID)) {
			fil.addFilter(new ReferenceFilter<>(WtmpManager.this, SUPER_PERSON_ID, person));
		}
		return getResult(fil);
	}

	@Override
	protected Wtmp makeBDO(Repository.Record res) throws DataFault {
		return new Wtmp(res);
	}

	public Wtmp create(AppUser p, AppUser real,HttpServletRequest req) throws DataFault{
		Wtmp w =  makeBDO();

		w.setPerson(p);
		w.setSuperPerson(real);
		w.setHost(req.getRemoteHost());
		w.setBrowser(req.getHeader("user-agent"));
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		Date s = time.getCurrentTime();
		Date e = new Date(s.getTime() + Window);
		w.setStartTime(s);
		w.setEndTime(e);
		w.commit();
		return w;
	}
	

	
	public void addTable(Table t,Wtmp w,SessionService viewer) {
		t.put(PERSON_COL,w,w.getPerson());
		
		AppUser real = w.getSuperPerson();
		if( real != null) {
			t.put(REAL_PERSON_COL,w,real);
		}
		t.put(HOST_COL,w,w.getHost());
		String dns = w.getDNSName();
		if( dns != null && ! dns.isEmpty()) {
			t.put(NAME_COL,w,dns);
		}
		t.put(BROWSER_COL,w,w.getBrowser());
		t.put(START_COL,w,w.getStartTime());
		t.put(END_COL,w,w.getEndTime());
		
		
	}
	public void formatTable(Table t) {
		if( t.hasCol(REAL_PERSON_COL)) {
			t.setColAfter(PERSON_COL, REAL_PERSON_COL);
		}
		if( t.hasCol(NAME_COL)) {
			t.setColAfter(HOST_COL,NAME_COL);
		}
		DateFormat df = DateFormat.getDateTimeInstance();
		t.setColFormat(START_COL, new DateTransform(df));
		t.setColFormat(END_COL, new DateTransform(df));
	}
	@Override
	public Class<Wtmp> getTarget() {
		return Wtmp.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingFactory#anonymise()
	 */
	@Override
	public void anonymise() throws DataFault {
		anonymise(null);
	}
	public void anonymiseAppUser(AppUser person) throws DataFault {
		anonymise(new ReferenceFilter<>(this, PERSON_ID, person));
	}
	public void anonymise(SQLFilter<Wtmp> fil) throws DataFault {
		FilterUpdate<Wtmp> update = new FilterUpdate<>(res);
		update.update(res.getStringExpression(getTarget(), HOST), "Removed", fil);
		
	}
	public class LoginFinder extends AbstractFinder<Date>{
		public LoginFinder() {
			Class<Wtmp> target = WtmpManager.this.getTarget();
			setMapper(new ValueResultMapper<Date>(res.getDateExpression(target, START_TIME)){
				public String getTarget(){
					StringBuilder sb = new StringBuilder();
					sb.append("MAX(");
					sb.append(super.getTarget());
					sb.append(")");
					return sb.toString();
				}
			});
		}
	}
	public Date lastLogin(AppUser person) throws DataException {
		LoginFinder finder = new LoginFinder();
		return finder.find(new ReferenceFilter<>(WtmpManager.this, PERSON_ID, person),true);
	}
	/** Get a filter for {@link AppUser}s who have logged in since
	 * a target date.
	 * 
	 * @param d
	 * @return
	 */
    public SQLFilter<AppUser> getActiveFilter(Date d){
    	AppUserFactory<AppUser> login = getContext().getService(SessionService.class).getLoginFactory();
    	
    	return (SQLFilter<AppUser>) convertToDestinationFilter(login, PERSON_ID, new TimeFilter(START_TIME,MatchCondition.GT, d) );
    }
}