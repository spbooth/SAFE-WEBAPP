//| Copyright - The University of Edinburgh 2018                            |
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;


import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Image;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.ImageInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/**
 * @author Stephen Booth
 *
 */
public class AvatarComposite<AU extends AppUser> extends AppUserComposite<AU, AvatarComposite>implements AppUserTransitionContributor,SummaryContributer<AU>, AnonymisingComposite<AU>{
	
	public static final String AVATAR="Avatar";
	//public static final String AVATAR_MIME="AvatarType";
	public static final String VIEW_AVATAR_RELATIONSHIP="ViewAvatar";
	public static final CurrentUserKey REMOVE_AVATAR = new CurrentUserKey("RemoveAvatar","Remove avatar","Clear the avatar image") {

		@Override
		public boolean allowState(AppUser user, SessionService op) {
			AppUserFactory<?> fac = op.getLoginFactory();
			AvatarComposite comp = fac.getComposite(AvatarComposite.class);
			return comp != null && comp.hasAvatar(op, user);
		}
		
		
	};
	public class RemoveAvatarTransition extends AbstractDirectTransition<AppUser>{

		/**
		 * @param provider
		 */
		public RemoveAvatarTransition(AppUserTransitionProvider provider) {
			super();
			this.provider = provider;
		}
		private final AppUserTransitionProvider provider;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AppUser target, AppContext c) throws TransitionException {
			removeAvatar((AU)target);
			return provider.new ViewResult(target);
		}
		
	}
	public static CurrentUserKey ADD_AVATAR = new CurrentUserKey("AddAvatar", "Add Avatar", "Upload your picture");
	
	public class AddAvatarTransition extends AbstractFormTransition<AppUser>{
		public class AddAvatarAction extends FormAction{
			/**
			 * @param target
			 */
			public AddAvatarAction(AppUser target) {
				super();
				this.target = target;
			}
			private final AppUser target;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				getRecord((AU)target).setProperty(AVATAR, f.get(AVATAR));
				try {
					target.commit();
				} catch (DataFault e) {
					throw new ActionException("Internal error", e);
				}
				return provider.new ViewResult(target);
			}
			
		}
		/**
		 * @param provider
		 */
		public AddAvatarTransition(AppUserTransitionProvider provider) {
			super();
			this.provider = provider;
		}
		private final AppUserTransitionProvider provider;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppUser target, AppContext conn) throws TransitionException {
			ImageInput input = new ImageInput();
			input.setOptional(false);
			f.addInput(AVATAR, "Avatar image", input);
			f.addAction("Attach", new AddAvatarAction(target));
		}
		
	}
	/**
	 * @param fac
	 */
	public AvatarComposite(AppUserFactory<AU> fac) {
		super(fac);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected final Class<? super AvatarComposite> getType() {
		return AvatarComposite.class;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(AVATAR, new BlobType());
		//spec.setField(AVATAR_MIME, new StringFieldType(true, null, 64));
		return spec;
	}
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(AVATAR);   // need to use transitions to edit
		//suppress.add(AVATAR_MIME);
		return suppress;
	}
	
	
	public BufferedImage getImage(AU user) throws DataFault {
		StreamData data = getData(user);
		if( data != null) {
			try {
				return ImageIO.read(data.getInputStream());
			} catch (IOException e) {
				throw new DataFault("Bad image", e);
			}
		}
		return null;
	}

	/**
	 * @param user
	 * @return
	 * @throws DataFault
	 */
	public StreamData getData(AU user) throws DataFault {
		return getRecord(user).getStreamDataProperty(AVATAR);
	}
	public boolean hasAvatar(SessionService<AU> viewer,AU user) {
		if( forbidden(user, viewer)) {
			return false;
		}
		try {
			return getData(user) != null;
		} catch (DataFault e) {
			getLogger().error("Error checking for avatar", e);
			return false;
		}
	}

	/**
	 * @param user
	 * @param viewer
	 * @return
	 */
	private boolean forbidden(AU user, SessionService<AU> viewer) {
		return viewer != null && ! viewer.isCurrentPerson(user) && ! viewer.hasRelationship(getFactory(), user, VIEW_AVATAR_RELATIONSHIP,true);
	}
	public Image getAvatar(SessionService<AU> viewer,AU user) {
		if( ! hasAvatar(viewer, user)) {
			return null;
		}
		LinkedList<String> path = new LinkedList<>();
		path.add(Integer.toString(user.getID()));
		
		return new Image(getContext(),new ServeDataResult(new AvatarServeDataProducer<>(getContext()), path),
				user.getIdentifier(),user.getIdentifier(),100,100);
	}

	public void removeAvatar(AU user) {
		try {
			getRecord(user).setProperty(AVATAR, null);
			user.commit();
		}catch(Throwable t) {
			getLogger().error("Error removing avatar", t);
		}
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor#getTransitions(uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider)
	 */
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider provider) {
		Map<AppUserKey, Transition<AppUser>> map = new LinkedHashMap<>();
		if( getRepository().hasField(AVATAR)) {
			map.put(REMOVE_AVATAR, new RemoveAvatarTransition(provider));
			map.put(ADD_AVATAR, new AddAvatarTransition(provider));
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		Image image = getAvatar(getContext().getService(SessionService.class), target);
		if( image != null) {
			attributes.put("Picture", image);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingComposite#anonymise(uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void anonymise(AU target) {
		// TODO Auto-generated method stub
		
	}

}
