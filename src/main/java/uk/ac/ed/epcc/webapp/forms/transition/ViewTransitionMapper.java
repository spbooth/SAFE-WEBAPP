package uk.ac.ed.epcc.webapp.forms.transition;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ObjectMapper;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class ViewTransitionMapper<X> extends AbstractContexed implements ObjectMapper<ViewTransitionGenerator<X>> {

	public static final String CONSTRUCTION_TAG = "ViewTransitionMapper";

	public ViewTransitionMapper(AppContext conn) {
		super(conn);
	}

	@Override
	public String getTag() {
		return CONSTRUCTION_TAG;
	}

	@Override
	public String[] encode(ViewTransitionGenerator<X> target) {
		LinkedList<String> list = new LinkedList<>();
		list.add(target.getFactory().getTargetName());
		String text = target.getText();
		if( text == null) {
			text="";
		}
		list.add(text);
		String title = target.getTitle();
		if( title == null) {
			title="";
		}
		list.add(title);
		X o = target.getTarget();
		target.getFactory().accept(new TransitionFactoryVisitor() {

			@Override
			public Object visitTransitionProvider(TransitionProvider prov) {
				list.add(prov.getID(o));
				return null;
			}

			@Override
			public Object visitPathTransitionProvider(PathTransitionProvider prov) {
				list.addAll(prov.getID(o));
				return null;
			}
		});
		return list.toArray(new String[list.size()]);
	}

	public class DecodeVisitor<K> implements TransitionFactoryVisitor<X, X, K>{
		public DecodeVisitor(LinkedList<String> list) {
			super();
			this.list = list;
		}

		private final LinkedList<String> list;
		
		@Override
		public X visitTransitionProvider(TransitionProvider<K, X> prov) {
			return prov.getTarget(list.pop());
		}

		@Override
		public X visitPathTransitionProvider(PathTransitionProvider<K, X> prov) {
			return prov.getTarget(list);
		}
		
	}
	@Override
	public ViewTransitionGenerator decode(String[] args) {
		try {
			LinkedList<String> list = new LinkedList<>();
			for(String s : args) {
				list.add(s);
			}
			String tag = list.pop();
			ViewTransitionFactory fac = (ViewTransitionFactory) new TransitionFactoryFinder(conn).getProviderFromName(tag);
			String text = list.pop();
			if( text.isEmpty()) {
				text = null;
			}
			String title = list.pop();
			if( title.isEmpty()) {
				title = null;
			}
			X o = (X) fac.accept(new DecodeVisitor(list));
			return new ViewTransitionGenerator<X>(getContext(), tag, o,text,title);
		}catch(Exception e) {
			getLogger().error("Error decoding",e);
			return null;
		}
	}

	@Override
	public boolean allowAccess(SessionService sess, ViewTransitionGenerator target) {
		
		return sess != null && sess.haveCurrentUser() && target.getFactory().canView(target.getTarget(), sess);
	}

	@Override
	public boolean isMine(Object target) {
		return target != null && target instanceof ViewTransitionGenerator;
	}

}
