//| Copyright - The University of Edinburgh 2019                            |
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

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.TemplateContributor;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;
import uk.ac.ed.epcc.webapp.servlet.RemoteAuthListener;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** Class to record request attributes (e.g. generated from shibboleth authentication)
 * generated during external authentication.
 * <p>
 * Config parameters:
 * <ul>
 * <li> <b><i>tag</i>.attributes</b> - list of attribute names<li>
 * <li> <b><i>tag</i>.realm<b> - authentication realms to listen to (comma seperated list)</li>
 * <li> <b><i>attribute-name</i>.label</b> - alternative descriptive text for attr</li>
 * </ul>
 * 
 * @author Stephen Booth
 *
 */
public class ShibAttributeListener<AU extends AppUser> extends AppUserComposite<AU,ShibAttributeListener> 
implements RemoteAuthListener<AU>,
SummaryContributer<AU>,
TemplateContributor<AU>,
HistoryFieldContributor
{

	/**
	 * 
	 */
	private static final String AUTHENTICATED_SUFFIX = "Authenticated";
	private final String tag;
	private final Set<String> target_realms;
	private final String attr[];
	/**
	 * @param fac
	 */
	public ShibAttributeListener(AppUserFactory fac,String tag) {
		super(fac);
		this.tag=tag;
		this.attr=fac.getContext().getInitParameter(tag+".attributes", "").split("\\s*,\\s*");
		String list = fac.getContext().getInitParameter(tag+".realm", WebNameFinder.WEB_NAME);
		this.target_realms=new HashSet<String>();
		for(String t : list.split("\\s*,\\s*")) {
			target_realms.add(t);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.RemoteAuthListener#authenticated(java.lang.String, uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void authenticated(String realm, AU user) {
		if( target_realms.contains(realm)) {
			ServletService serv = getContext().getService(ServletService.class);
			if( serv==null) {
				return;
			}
			Record record = getRecord(user);
			Date now = getContext().getService(CurrentTimeService.class).getCurrentTime();
			Date update=null;
			for(String a : attr) {
				Object res = serv.getRequestAttribute(a);
				if( res != null) {
					update=now;
					record.setOptionalProperty(a, res.toString());
				}else {
					record.setOptionalProperty(a, null); // remove if not seen
				}
			}
			record.setOptionalProperty(tag+AUTHENTICATED_SUFFIX, update);
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class getType() {
		return ShibAttributeListener.class;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(tag+AUTHENTICATED_SUFFIX, new DateFieldType(true, null));
		addToHistorySpecification(spec);
		return spec;
	}

	@Override
	public Set addSuppress(Set suppress) {
		suppress.add(tag+AUTHENTICATED_SUFFIX);
		for(String a : attr) {
			suppress.add(a);
		}
		return suppress;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		Record rec = getRecord(target);
		for(String a : attr) {
			String v = rec.getStringProperty(a);
			if( v != null ) {
				attributes.put(getContext().getInitParameter(a+".label", a), v);
			}
		}
		Date d = rec.getDateProperty(tag+AUTHENTICATED_SUFFIX);
		if( d != null) {
			attributes.put("Attributes validated", d);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TemplateContributor#setTemplateContent(uk.ac.ed.epcc.webapp.content.TemplateFile, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void setTemplateContent(TemplateFile template, String prefix, AU target) {
		Record rec = getRecord(target);
		for(String a : attr) {
			String v = rec.getStringProperty(a);
			if( v != null ) {
				template.setProperty(prefix+a, v);
			}
		}
		Date d = rec.getDateProperty(tag+AUTHENTICATED_SUFFIX);
		if( d != null) {
			template.setProperty(prefix+tag+AUTHENTICATED_SUFFIX, d);
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor#addToHistorySpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification)
	 */
	@Override
	public void addToHistorySpecification(TableSpecification spec) {
		for(String a : attr) {
			spec.setField(a, new StringFieldType(true, null,128));
		}
	}

}
