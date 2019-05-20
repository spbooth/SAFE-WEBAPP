//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A {@link TransitionFactoryCreator} that creates an
 * {@link EmailTransitionProvider} from the tag of the {@link MessageHandlerFactory}.
 * @author spb
 *
 */

public class EmailTransitionFactoryCreator extends AbstractContexed implements TransitionFactoryCreator<EmailTransitionProvider>{
	/**
	 * 
	 */
	public static final String MESSAGE_PROVIDER_FACTORY_TAG_PREFIX = "MessageProviderFactory";
	public EmailTransitionFactoryCreator(AppContext conn){
		super(conn);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator#getTransitionProvider(java.lang.String)
	 */
	public EmailTransitionProvider getTransitionProvider(String tag) {
		MessageHandlerFactory fac;
		int index = tag.indexOf(MessageHandlerFactoryCreator.TYPE_SEPERATOR);
		if( index > 0){
			MessageHandlerFactoryCreator creator = conn.makeObject(MessageHandlerFactoryCreator.class, "MessageProviderFactoryCreator."+tag.substring(0, index));
			fac = creator.getMessageHandlerFactory(tag.substring(index+1));
		}else{
			fac = conn.makeObjectWithDefault(MessageHandlerFactory.class,null, MESSAGE_PROVIDER_FACTORY_TAG_PREFIX,tag);
			if( fac == null ){
				fac = conn.makeObjectWithDefault(MessageHandlerFactory.class,null,tag);
			}
			if( fac == null ){
				conn.getService(LoggerService.class).getLogger(getClass()).error("Cannot find MessageHandler with tag "+tag);
				return null;
			}
		}
		return new EmailTransitionProvider(fac);
	}
	

}