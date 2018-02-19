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

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FuncExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLFunc;
import uk.ac.ed.epcc.webapp.jdbc.expr.ValueResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
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
	private static final String END_TIME = "EndTime";

	private static final String START_TIME = "StartTime";
	private static final String BROWSER = "Browser";

	private static final String HOST = "Host";

	private static final String PERSON_ID = "PersonID";
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



		public AppUser getPerson()  {
			SessionService serv = getContext().getService(SessionService.class);

			if( serv != null ){
				try {
					return (AppUser) serv.getLoginFactory().find(
							record.getNumberProperty(PERSON_ID));
				} catch (Throwable e) {
					return null;
				}
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

		public void setStartTime(Date d) {
			record.setProperty(START_TIME, d);
		}

		/** update the Wtmp entry
		 * 
		 * @return true if modified
		 * @throws DataFault
		 */
		public boolean update() throws DataFault {
			Date n = new Date();
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
	public Iterator getCurrent() throws DataFault {
		return new FilterIterator(new DateFilter(new Date()));
	}

	

	@Override
	protected DataObject makeBDO(Repository.Record res) throws DataFault {
		return new Wtmp(res);
	}

	public Wtmp create(AppUser p, HttpServletRequest req) throws DataFault{
		Wtmp w =  makeBDO();

		w.setPerson(p);
		w.setHost(req.getRemoteHost());
		w.setBrowser(req.getHeader("user-agent"));
		Date s = new Date();
		Date e = new Date(s.getTime() + Window);
		w.setStartTime(s);
		w.setEndTime(e);
		w.commit();
		return w;
	}
	


	public String getHTMLHeader() {
		return "<tr><th>Person</th><th>Host</th><th>Browser</th><th>Start</th><th>End</th></tr>\n";
	}
	public String getHTML(Wtmp w,SessionService viewer) {
		DateFormat df = DateFormat.getDateTimeInstance();
		StringBuilder buff = new StringBuilder();
		buff.append("<tr><td><small>");
		buff.append(w.getPerson().getIdentifier());
		buff.append("</small></td><td><small>");
		buff.append(w.getHost());
		buff.append("</small></td><td><small>");
		buff.append(w.getBrowser());
		buff.append("</small></td><td><small>");
		buff.append(df.format(w.getStartTime()));
		buff.append("</small></td><td><small>");
		buff.append(df.format(w.getEndTime()));
		buff.append("</small></td></tr>\n");
		return buff.toString();
	}
	@Override
	public Class<? super Wtmp> getTarget() {
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
		anonymise(new ReferenceFilter<Wtmp, AppUser>(this, PERSON_ID, person));
	}
	public void anonymise(SQLFilter<Wtmp> fil) throws DataFault {
		FilterUpdate<Wtmp> update = new FilterUpdate<>(res);
		update.update(res.getStringExpression(getTarget(), HOST), "Removed", fil);
		
	}
	public class LoginFinder extends AbstractFinder<Date>{
		public LoginFinder() {
			Class<? super Wtmp> target = WtmpManager.this.getTarget();
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
		return finder.find(new ReferenceFilter<WtmpManager.Wtmp, AppUser>(WtmpManager.this, PERSON_ID, person),true);
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