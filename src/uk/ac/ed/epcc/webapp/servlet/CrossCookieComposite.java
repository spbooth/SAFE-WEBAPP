//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLIdFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager.Wtmp;
import uk.ac.ed.epcc.webapp.session.RandomService;

/** A composite added to a {@link WtmpManager} that records cookie values for cross application SSO
 *
 * The cookie data should be of the format
 * <b><i>id</i>-<i>random-data</i></b> where id is the id of the Wtmp record.
 * Only the random-data part is actually stored in the data field.
 * @author spb
 *
 */
public class CrossCookieComposite extends Composite<WtmpManager.Wtmp,CrossCookieComposite> {

	private static final String COOKIE_DATA_FIELD="CookieData";
	private static final int DATA_LEN=64;
	/**
	 * @param fac
	 */
	public CrossCookieComposite(WtmpManager fac) {
		super(fac);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super CrossCookieComposite> getType() {
		return CrossCookieComposite.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(COOKIE_DATA_FIELD, new StringFieldType(true, null, DATA_LEN));
		return super.modifyDefaultTableSpecification(spec, table);
	}

	/** get a
	 * 
	 * @param fulldata
	 * @return
	 */
	public BaseFilter<WtmpManager.Wtmp> getFilter(String fulldata){
		Class<? super Wtmp> target = getFactory().getTarget();
		Repository res = getRepository();
		
		int pos = fulldata.indexOf("-");
		// Important to check length to not match blank data wtmp.
		if( pos < 1 || (fulldata.length()-pos) != DATA_LEN || ! res.hasField(COOKIE_DATA_FIELD)){
			return new FalseFilter<WtmpManager.Wtmp>(target);
		}
		AndFilter fil = new AndFilter<>(target);
		Integer id = Integer.parseInt(fulldata.substring(0, pos));
		
		fil.addFilter(new SQLIdFilter<>(target, res, id));
		String data = fulldata.substring(pos+1);
		fil.addFilter(new SQLValueFilter<>(target, res, COOKIE_DATA_FIELD, data));
		return fil;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(COOKIE_DATA_FIELD);
		return suppress;
	}

	/** return the full-data to be stored in the cookie.
	 * 
	 * @param w
	 * @return
	 * @throws DataFault 
	 */
	public String getFullData(Wtmp w) throws DataFault{
		Record r = getRecord(w);
		String rand = r.getStringProperty(COOKIE_DATA_FIELD);
		if( rand == null){
			RandomService rs = getContext().getService(RandomService.class);
			rand = rs.randomString(DATA_LEN);
			r.setProperty(COOKIE_DATA_FIELD, rand);
			w.commit();
		}
		int id=w.getID();
		return Integer.toString(id)+"-"+rand;
	}
	
	public void invalidate(Wtmp w){
		getRecord(w).setProperty(COOKIE_DATA_FIELD, "");
	}
	

}
