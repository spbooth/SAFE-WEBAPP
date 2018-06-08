//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.preferences;

import java.util.EnumSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.Transform;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.OnOffInput;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.IndexTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTargetlessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.preferences.UserSettingFactory.UserSetting;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ViewTransitionFactory} for editing {@link Preference}s and {@link Feature}s.
 * @author spb
 *
 */
public class PreferenceTransitionProvider implements ViewTransitionProvider<PreferenceAction,Feature> , IndexTransitionProvider<PreferenceAction, Feature>{

	/**
	 * @param conn
	 * @param target
	 */
	public PreferenceTransitionProvider(AppContext conn, String target) {
		super();
		this.conn = conn;
		this.target_name = target;
	}

	private final AppContext conn;
	private final String target_name;
	
	public class Linker implements UIGenerator, Comparable<Linker>{
		/**
		 * @param target
		 */
		public Linker(Feature target) {
			super();
			this.target = target;
		}

		private final Feature target;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
		 */
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			builder.addLink(conn, target.getName(), new ViewTransitionResult<Feature, PreferenceAction>(PreferenceTransitionProvider.this, target));
			return builder;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Linker o) {
			return target.getName().compareTo(o.target.getName());
		}
	}
	public class ClearPreferenceTransition extends AbstractDirectTransition<Feature>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(Feature target, AppContext c) throws TransitionException {
			UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(c);
			try {
				((Preference)target).clearPreference(c);
			} catch (DataFault e) {
				c.getService(LoggerService.class).getLogger(getClass()).error("Error clearing preference",e);
				throw new TransitionException("Internal error");
			}
			return new ViewTransitionResult<Feature, PreferenceAction>(PreferenceTransitionProvider.this, target);
		}
		
	}
	public class IndexResult extends CustomPageResult{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#getTitle()
		 */
		@Override
		public String getTitle() {
			return "${service.name} ${service.website-name} Preferences";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#addContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder)
		 */
		@Override
		public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
			cb.addHeading(2, conn.expandText(getTitle()));
			cb.addTable(getContext(), getIndexTable(conn.getService(SessionService.class)));
			return cb;
		}
		
	}
	public class IndexTransition extends AbstractDirectTargetlessTransition<Feature>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTargetlessTransition#doTransition(uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AppContext c) throws TransitionException {
			return new IndexResult();
		}
		
	}
	public class SetPreferenceTransition extends AbstractFormTransition<Feature>{

		/**
		 * 
		 */
		private static final String VALUE = "value";
		public class SetAction extends FormAction{
			/**
			 * @param pref
			 */
			public SetAction(Preference pref) {
				this.pref = pref;
			}

			private final Preference pref;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				Boolean b = (Boolean) f.getItem(VALUE);
				pref.setPreference(getContext(), b);
				return new IndexTransitionResult<Feature, PreferenceAction>(PreferenceTransitionProvider.this);
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, Feature target, AppContext conn) throws TransitionException {
			OnOffInput input = new OnOffInput();
			input.setItem(target.isEnabled(conn));
			f.addInput(VALUE, "Preferred setting", input);
			f.addAction("Update", new SetAction((Preference)target));
			
		}
		
	}
	public class SetFeatureTransition extends AbstractFormTransition<Feature>{

		/**
		 * 
		 */
		private static final String VALUE = "value";
		public class SetFeatureAction extends FormAction{
			/**
			 * @param f
			 */
			public SetFeatureAction(Feature f) {
				this.feature = f;
				setConfirm("change_feature");
			}

			private final Feature feature;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				Boolean b = (Boolean) f.getItem(VALUE);
				ConfigService serv = getContext().getService(ConfigService.class);
				serv.setProperty("service.feature."+feature.getName(), Boolean.toString(b));
				return new IndexTransitionResult<Feature, PreferenceAction>(PreferenceTransitionProvider.this);
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, Feature target, AppContext conn) throws TransitionException {
			OnOffInput input = new OnOffInput();
			input.setItem(target.isEnabled(conn));
			f.addInput(VALUE, "Feature setting", input);
			f.addAction("Update", new SetFeatureAction(target));
			
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTransitions(java.lang.Object)
	 */
	@Override
	public Set<PreferenceAction> getTransitions(Feature target) {
		return EnumSet.allOf(PreferenceAction.class);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTransition(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Transition<Feature> getTransition(Feature target, PreferenceAction key) {
		switch(key){
		case CLEAR_PREFERENCE: return new ClearPreferenceTransition();
		case SET_PREFERENCE: return new SetPreferenceTransition();
		case LIST: return new IndexTransition();
		case SET_SYSTEM_DEFAULT: return new SetFeatureTransition();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#lookupTransition(java.lang.Object, java.lang.String)
	 */
	@Override
	public PreferenceAction lookupTransition(Feature target, String name) {
		if( name == null || name.trim().length() == 0){
			return null;
		}
		for( PreferenceAction action : getTransitions(target)){
			if( name.equals(action.toString())){
				return action;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return target_name;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, Feature target, PreferenceAction key) {
		return key.allow(c.getService(SessionService.class), target);
	}
	public Table getIndexTable(SessionService sess){
		Table t = new Table();
		for(Feature f : Feature.getKnownFeatures()){
			if( canView(f, sess)){
				addTable(t, f);
			}
		}
		t.setKeyName("Feature");
		t.removeCol("Name");
		t.setKeyTransform( new Transform() {
			
			@Override
			public Object convert(Object old) {
				if( old instanceof Feature){
				return new Linker((Feature)old);
				}
				return old;
			}
		});
		t.sortRows();
		return t;
	}
	
    private String getText(boolean val){
    	return val ? "On" : "Off";
    }
	public void addTable(Table t, Feature target){
		AppContext c = getContext();
		t.put("Name", target,target.getName());
		t.put("Description", target, target.getDescription());
		t.put("Current setting",target, getText(target.isEnabled(c)));
		if( c.getService(SessionService.class).hasRole(PreferenceAction.SET_FEATURES_ROLE)){
			t.put( "Default value", target, getText(target.isDef()));
			t.setHighlight(target, target.isEnabled(c) != target.isDef());
		}
		if( target instanceof Preference){
			Preference p = (Preference) target;
			if( ! p.hasPreference(c)){
				t.put( "User preference", target, "No preference specified");
			}else{
				t.put("User preference", target, getText(p.isEnabled(c)));
			}
			t.put("System default setting", target, getText(p.defaultSetting(c)));
			t.setHighlight(target, target.isEnabled(c) != p.defaultSetting(c));
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, Feature target) {
		Table t = new Table();
		
		addTable(t, target);
		Table sum = new Table();
		
		String value = "Value";
		for( Object o : t.getCols()){
			sum.put(value, o, t.get(o,target));
		}
		sum.setKeyName("Property");
		cb.addTable(conn, sum);
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#accept(uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor)
	 */
	@Override
	public <R> R accept(TransitionFactoryVisitor<R, Feature, PreferenceAction> vis) {
		// TODO Auto-generated method stub
		return vis.visitTransitionProvider(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(Feature target, SessionService<?> sess) {
		if( sess == null || ! sess.haveCurrentUser()){
			return false;
		}
		if(sess.hasRole(PreferenceAction.SET_FEATURES_ROLE) ){
			return true;
		}
		if( target instanceof Preference){
			return ((Preference)target).canView(sess);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getTopContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, Feature target, SessionService<?> sess) {
		return cb;
	}
	@Override
	public <X extends ContentBuilder> X getBottomContent(X cb, Feature target, SessionService<?> sess) {
		return cb;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getLogContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, Feature target, SessionService<?> sess) {
		return getSummaryContent(sess.getContext(), cb, target);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getHelp(java.lang.Object)
	 */
	@Override
	public String getHelp(PreferenceAction key) {
		return key.getHelp();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getText(java.lang.Object)
	 */
	@Override
	public String getText(PreferenceAction key) {
		return key.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public Feature getTarget(String id) {
		return Feature.findFeatureByName(id);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public String getID(Feature target) {
		return target.getName();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory#getIndexTransition()
	 */
	@Override
	public PreferenceAction getIndexTransition() {
		return PreferenceAction.LIST;
	}

}
