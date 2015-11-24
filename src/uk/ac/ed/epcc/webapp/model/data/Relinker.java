// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Enumeration;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** Class for relinking references.
 * 
 * 
 * This works at the {@link Repository} level and identifies references via properties. It attempts to find all fields that
 * point to a particular record and rewrite them to point to a different record instead. The operation is intended for database refactoring (specifically merge operations)
 * rather than production use.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class Relinker{

	/**
	 * 
	 */
	public Relinker() {
		// TODO Auto-generated constructor stub
	}

	
	public void relink(Repository res, Record src, Record dest) throws DataFault, ConsistencyError{
		String home = res.getTag();
		
		AppContext conn = res.getContext();
		String alt_tag = res.getTable();
		
		// references have reference.param_name.field=remote  parameters
		// rolled tables with param_name != tag_name won't be found
		ConfigService config = conn.getService(ConfigService.class);
		Properties props = config.getServiceProperties();
		Enumeration<?> p = props.propertyNames();
		while( p.hasMoreElements()){
			String name = p.nextElement().toString();
			if( name.startsWith(Repository.REFERENCE_PREFIX) ){
				String prop = props.getProperty(name);
				if(prop.equals(home) || prop.equals(alt_tag)){

					// found one
					String val = name.substring(Repository.REFERENCE_PREFIX.length());
					int pos = val.indexOf(".");
					if( pos < 0 ){
						continue;
					}
					String field=val.substring(pos+1);
					String src_tag=val.substring(0, pos);
					Repository src_rep = Repository.getInstance(conn, src_tag);
					FilterUpdate update = new FilterUpdate(src_rep);
					if( ! src_rep.hasField(field)){
						continue;
					}
					FieldExpression<Number,DataObject>expr = src_rep.getNumberExpression(DataObject.class, Number.class, field);
					update.update(expr, dest.getID(), new SQLValueFilter<DataObject>(DataObject.class, src_rep, field, src.getID()));
				}
			}
		}
	}
}
