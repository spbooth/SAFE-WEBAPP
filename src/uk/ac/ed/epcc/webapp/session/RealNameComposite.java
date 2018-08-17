package uk.ac.ed.epcc.webapp.session;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;


public class RealNameComposite<AU extends AppUser> extends AppUserComposite<AU, RealNameComposite> implements AnonymisingComposite<AU>, SortNameContributor<AU> ,NameComposite<AU>,HistoryFieldContributor{

	public static final String TITLE = "Title";
	public static final String FIRSTNAME = "Firstname";
	public static final String INITIALS = "Initials";
	public static final String LASTNAME = "Lastname";

	public RealNameComposite(AppUserFactory<AU> fac) {
		super(fac);
	}

	@Override
	protected Class<? super RealNameComposite> getType() {
		return RealNameComposite.class;
	}
	
	public String getLastname(AU target) {
		return getRecord(target).getStringProperty(RealNameComposite.LASTNAME);
	}
	public void setFirstName(AU target,String first){
		getRecord(target).setProperty(RealNameComposite.FIRSTNAME, first);
	}
	public void setLastName(AU target,String last){
		getRecord(target).setProperty(RealNameComposite.LASTNAME, last);
	}
	public void setInitials(AU target,String initials){
		getRecord(target).setOptionalProperty(RealNameComposite.INITIALS, initials);
	}
	public String getTitle(AU target) {
		return getRecord(target).getStringProperty(RealNameComposite.TITLE);
	}
	public void setTitle(AU target,String title){
		getRecord(target).setOptionalProperty(RealNameComposite.TITLE, title);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(RealNameComposite.TITLE, new StringFieldType(true, null, 32));
		spec.setField(RealNameComposite.FIRSTNAME, new StringFieldType(true, null, 32));
		//TODO make optional but have override preserve field order
		spec.setField(RealNameComposite.INITIALS, new StringFieldType(true, null, 3));
		spec.setField(RealNameComposite.LASTNAME, new StringFieldType(true, null, 32));
		return spec;
	}
	public String getInitials(AU target) {
		return getRecord(target).getStringProperty(RealNameComposite.INITIALS);
	}
	public String getFirstname(AU target) {
		return getRecord(target).getStringProperty(RealNameComposite.FIRSTNAME);
	};
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addTranslations(java.util.Map)
	 */
	@Override
	public Map<String, String> addTranslations(Map<String, String> labels) {
		labels.put(RealNameComposite.FIRSTNAME, "First Name");
		labels.put(RealNameComposite.LASTNAME, "Last Name");
		labels.put(RealNameComposite.TITLE, "Title (Mr,Mrs,Dr etc.)");
		return labels;
	}

	@Override
	public void anonymise(AU target) {
		setTitle(target,null);
		setFirstName(target,null);
		setInitials(target, null);
		setLastName(target,null);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		optional.add(TITLE);
		optional.add(INITIALS);
		return optional;
	}

	@Override
	public boolean addSortName(AU target,StringBuilder name) {
		boolean inserted = false;
		String seperator=" ";
		String lastname = getLastname(target);
		if (lastname != null) {
			name.append(lastname);
			seperator=","; // add comma after lastname if present
			inserted=true;
		}
		String title = getTitle(target);
		if (title != null) {
			if( inserted){
				name.append(seperator);
				seperator=" ";
			}
			name.append(title);
			inserted = true;
		}
		String firstname = getFirstname(target);
		if (firstname != null) {
			if (inserted){
				name.append(seperator);
				seperator=" ";
			}
			name.append(firstname);
			inserted = true;
		}
		String initials = getInitials(target);
		if (initials != null) {
			if (inserted){
				name.append(seperator);
				seperator=" ";
			}
			name.append(initials);
			inserted = true;
		}
		return inserted;
	}

	@Override
	public void addOrder(List<OrderClause> order) {
		order.add(getRepository().getOrder(RealNameComposite.LASTNAME, false));
		order.add(getRepository().getOrder(RealNameComposite.FIRSTNAME, false));
	}

	@Override
	public Comparator<AU> getComparator() {
		return new RealNameComparator();
	}
	private class RealNameComparator implements Comparator<AU>{

		@Override
		public int compare(AU p1, AU p2) {
			String last1 = getLastname(p1);
			String last2 = getLastname(p2);
			int res=0;
			if( last1 != null && last2 != null ){
				
				res = last1.compareToIgnoreCase(last2);
			}
			String first1 = getFirstname(p1);
			String first2 = getFirstname(p2);
			if (res == 0 && first1 != null && first2 != null) {
				
				res = first1.compareToIgnoreCase(first2);
			}
			return 0;
		}
		
	}
	@Override
	public String getPresentationName(AU target) {
		StringBuilder name = new StringBuilder();
		String title = getTitle(target);
		boolean inserted = false;
		if (title != null) {
			name.append(title);
			inserted = true;
		}
		String firstname = getFirstname(target);
		if (firstname != null) {
			if (inserted)
				name.append(' ');
			name.append(firstname);
			inserted = true;
		}
		String initials = getInitials(target);
		if (initials != null) {
			if (inserted)
				name.append(' ');
			name.append(initials);
			inserted = true;
		}
		String lastname = getLastname(target);
		if (lastname != null) {
			if (inserted){
				name.append(' ');
			}
			name.append(lastname);
		}else{
			// Abort if no last name
			return null;
		}
		return name.toString();
	}

	@Override
	public void addToHistorySpecification(TableSpecification spec) {
		modifyDefaultTableSpecification(spec, null);
		
	}
	
}
