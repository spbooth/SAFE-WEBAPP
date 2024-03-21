package uk.ac.ed.epcc.webapp.content;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.*;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An {@link ObjectMapper} for {@link Link} or {@link Button} objects.
 * 
 */
public class FormResultMapper extends AbstractContexed implements ObjectMapper<FormResultWrapper> {

	public static final String CONSTRUCTION_TAG="Frm";
	public FormResultMapper(AppContext conn) {
		super(conn);
	}

	@Override
	public String getTag() {
		return CONSTRUCTION_TAG;
	}

	@Override
	public String[] encode(FormResultWrapper target) {
		LinkedList<String> list = new LinkedList<>();
		ChainedTransitionResult r = (ChainedTransitionResult) target.result;
		if( target instanceof Link) {
			Link l = (Link) target;
			list.add("L");
			list.add(l.isNewWindow()?"N":"I");
		}else if ( target instanceof Button) {
			list.add("B");
		}else {
			return null;
		}
		list.add(r.getProvider().getTargetName());
		Object key = r.getTransition();
		if( key == null ) {
			list.add("");
		}else {
			list.add(key.toString());
		}
		String text = target.text;
		if( text == null) {
			text="";
		}
		list.add(text);
		String title = target.help;
		if( title == null) {
			title="";
		}
		list.add(title);
		Object o = r.getTarget();
		
		r.getProvider().accept(new TransitionFactoryVisitor() {

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

	public class DecodeVisitor<X,K> implements TransitionFactoryVisitor<X, X, K>{
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
	public FormResultWrapper decode(String[] args) {
		try {
			LinkedList<String> list = new LinkedList<>();
			for(String s : args) {
				list.add(s);
			}
			String type = list.pop();
			boolean new_window = false;
			if( type.equals("L")) {
				String win = list.pop();
				if( win.equals("N")) {
					new_window=true;
				}
			}
			String tag = list.pop();
			TransitionFactory fac = (TransitionFactory) new TransitionFactoryFinder(conn).getProviderFromName(tag);
			String key = list.pop();
			if( key.isEmpty()) {
				key=null;
			}
			String text = list.pop();
			if( text.isEmpty()) {
				text = null;
			}
			String title = list.pop();
			if( title.isEmpty()) {
				title = null;
			}
			Object o =  fac.accept(new DecodeVisitor(list));
			Object k = null;
			if( key != null) {
				k = fac.lookupTransition(o, key);
			}
			ChainedTransitionResult r = new ChainedTransitionResult(fac, o, k);
					
			if( type.equals("L")) {
				Link l = new Link(getContext(), text, title,r);
				l.setNewWindow(new_window);
				return l;
			}else if( type.equals("B")) {
				return new Button(getContext(), text, title, r);
			}
			return null;
		}catch(Exception e) {
			getLogger().error("Error decoding",e);
			return null;
		}
	}

	@Override
	public boolean allowAccess(SessionService sess, FormResultWrapper target) {
		if( sess == null || ! sess.haveCurrentUser()) {
			return false;
		}
		if( target.result instanceof ChainedTransitionResult) {
			return ((ChainedTransitionResult)target.result).allow(sess);
		}
		return false;
	}

	@Override
	public boolean isMine(Object target) {
		return (target != null) && 
				( target instanceof Link || target instanceof Button) &&
				((FormResultWrapper)target).result instanceof ChainedTransitionResult;
	}

}
