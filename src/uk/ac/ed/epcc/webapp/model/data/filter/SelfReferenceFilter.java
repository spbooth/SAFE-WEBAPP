// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Filter to select by IndexedReference
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SelfReferenceFilter.java,v 1.8 2015/04/09 22:00:46 spb Exp $")

public class SelfReferenceFilter<T extends DataObject> implements SQLFilter<T> , PatternFilter<T>{

	private final Class<? super T> target;
	private final IndexedReference<T> ref;
	private final Repository res;
	private final boolean exclude;
	/** Filter that matches an {@link IndexedReference} 
	 * 
	 * @param target   factory target
	 * @param res  {@link Repository}
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<? super T> target,Repository res, IndexedReference<T> ref){
		this(target,res,false,ref);
	}
	/** 
	 * 
	 * @param target factory target Class
	 * @param res {@link Repository}
	 * @param exclude if true matches everything but reference
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<? super T> target,Repository res, boolean exclude ,IndexedReference<T> ref){
		this.target=target;
		this.res=res;
		this.exclude=exclude;
		this.ref=ref;
	}
	
	public void accept(T o) {
		
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new PatternArgument() {
			
			public String getField() {
				return res.addUniqueName(new StringBuilder(), false, false).toString();
			}
			
			public Object getArg() {
				return ref;
			}
			
			public boolean canLog() {
				return true;
			}
			
			public void addArg(PreparedStatement stmt, int pos) throws SQLException {
				stmt.setInt(pos, ref.getID());	
			}
		});
		return list;
	}

	public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
		res.addUniqueName(sb, qualify, true);
		if( exclude ){
			sb.append(" != ?");
		}else{
			sb.append("=?");
		}
		return sb;
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
		return target;
	}
	
	
}