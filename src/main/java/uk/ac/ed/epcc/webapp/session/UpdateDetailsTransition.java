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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdateProducerTransition;

/**
 * @author Stephen Booth
 *
 */
public class UpdateDetailsTransition<A extends AppUser> extends StandAloneFormUpdateProducerTransition<A>
{

	private final AppUserTransitionProvider provider;
	

	/**
	 * @param name
	 * @param fac
	 */
	protected UpdateDetailsTransition(AppUserTransitionProvider provider, AppUserFactory<A> fac) {
		super("Details", fac);
		this.provider=provider;
	}
	
	public AppUserFactory<A> getAppUserFactory(){
		return (AppUserFactory<A>) getFormUpdateProducer();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, A target) {
		AppUserFactory<A> fac = getAppUserFactory();
		boolean my_details = ((SessionService<A>)op).isCurrentPerson(target);
		if( my_details && fac.needDetailsUpdate(target)) {
			// This will only be a forced update if the current user.
			// We may allow other users to udpdate via a role
			try {
				ContentBuilder div = cb.getPanel();
				div.addHeading(2, "Update required");
				PreDefinedContent content = new PreDefinedContent(op.getContext(), "person_update_required");
				div.addObject(content);
				div.addParent();
			}catch(MissingResourceException e) {
				// optional
			}
		}
		fac.addUpdateNotes(cb, target);
		Date last = target.getLastTimeDetailsUpdated();
		if( last != null) {
			DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			cb.addHeading(3, "Last updated: "+df.format(last));
		}
		AppContext c = op.getContext();
		String privacy_policy=c.getExpandedProperty("service.url.privacypolicy");
	    if( privacy_policy != null && ! privacy_policy.isEmpty() ){ 
	    	ExtendedXMLBuilder text = cb.getText();
	    	text.open("small");
	    	text.clean(c.expandText("All information supplied is held and processed in accordance with the ${service.name} Personal Data and Privacy Policy.\n" + 
	    			"You can find full details "));
	    	text.open("a");
	    		text.attr("href",privacy_policy);
	    		text.attr("target", "_blank");
	    		text.clean("here");
	    	text.close();
	    	text.clean(".");
	    	text.close();
	    	text.appendParent();
	    }
	    super.getExtraHtml(cb, op, target);

		return cb;
	}

	

}
