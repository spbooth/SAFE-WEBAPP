package uk.ac.ed.epcc.webapp.session;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractConstructedObjectList;
import uk.ac.ed.epcc.webapp.AppContext;

public class RequiredPageProviderList<AU extends AppUser> extends AbstractConstructedObjectList<RequiredPageProvider<AU>> implements RequiredPageProvider<AU>{

	public RequiredPageProviderList(AppContext conn, String tag) {
		super(conn, tag);
	}

	@Override
	public Set<RequiredPage<AU>> getRequiredPages() {
		LinkedHashSet<RequiredPage<AU>> set = new LinkedHashSet<>();
		for(RequiredPageProvider<AU> p : this) {
			set.addAll(p.getRequiredPages());
		}
		return set;
	}

	@Override
	protected Class<? super RequiredPageProvider<AU>> getTemplate() {
		return RequiredPageProvider.class;
	}

}
