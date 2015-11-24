// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A {@link DumpParser} that looks for existing records unchanged from an initial baseline dump
 * and marks them as seen in an enclosed {@link Dumper} so they will be excluded from a subsequently generated dump generated by
 * this {@link Dumper}. This dump will therefore only contain the entries needed to update the baseline state to the new state.
 * <p>
 * 
 * 
 * The dump being compared against should ideally be a newly generated dump from an earlier state of the
 * <em>same</em> database as this code cannot work out the correct id to use if the
 * record has changed and has to use the id from the parse. 
 * 
 * Note this relies on {@link Record#findDuplicate(int)} to determine if a record is unchanged.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.6 $")
public class DiffParser extends DumpParser {

	Map<String,Set<String>> ignore;
	private final Dumper dumper;
	public Dumper getDumper() {
		return dumper;
	}


	/**
	 * @param conn
	 */
	public DiffParser(AppContext conn,Dumper dumper) {
		super(conn,false);
		this.dumper=dumper;
		ignore=new HashMap<String, Set<String>>();
	}

	
	public void ignore(String table, String field){
		String key = table.toLowerCase(Locale.ENGLISH);
		Set<String> fields = ignore.get(key);
		if( fields == null ){
			fields=new HashSet<String>();
			ignore.put(key,fields);
		}
		fields.add(field);
	}
	
	private Set<String> getIgnore(String table){
		return ignore.get(table.toLowerCase(Locale.ENGLISH));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#processRecord(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	public int processRecord(int parse_id,Record rec) throws ConsistencyError, DataFault {
		
		// would this even work without assuming parsed-ids are correct.
		// if the id-s are remapped 
		Repository rep = rec.getRepository();
		LinkedHashSet<String> fields = new LinkedHashSet<String>(rep.getFields());
		Set<String> skip = getIgnore(rep.getTag());
		if( skip != null ){
			fields.removeAll(skip);
		}
		int id = rec.findDuplicate(parse_id,fields);
		
		// If the record has changed the id will be <= 0 and only the table will be makrd as seen.
		dumper.markSeen(rec.getRepository().getTag(), id);
		
		// This is a known record that has changed so return its real id
		// Here we assume the dump is from the SAME database we are currently looking at.
		return parse_id;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#processSpecification(java.lang.String, uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification)
	 */
	@Override
	public void processSpecification(String table, TableSpecification spec)
			throws DataFault {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#skipSpecification(java.lang.String)
	 */
	@Override
	public boolean skipSpecification(String table) {
		return true;
	}

}
