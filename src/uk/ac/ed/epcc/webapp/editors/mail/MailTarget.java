//| Copyright - The University of Edinburgh 2015                            |
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

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Target object for {@link EmailTransitionProvider}
 * This not only identifies the email message but also a location within the message.
 * 
 * The mssage hash is also stored explicitly so we can detect if the has sent by the user is the same as
 * the hash of the stored object. If they differ the user has started from an out of date state. 
 * 
 * @author spb
 *
 */
public class MailTarget {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MailTarget [handler=" + handler + ", hash=" + hash + ", path=" + path + "]";
	}

	public static final Feature IGNORE_HASH_FEATURE = new Feature("mail.ignore_hash",false,"Treat all message hashes as zero. This it to allow repeatable tests do not use in production");
	/** create a MailTarget with the hash derived from the handler.
	 * This is appropriate when the target is created directly for example
	 * as the result of a creation operation. 
	 * The path is always null referring to the entire message
	 * 
	 * @param handler
	 * @throws Exception
	 */
	public MailTarget(MessageHandler handler) throws Exception{
		this(handler,handler.getMessageProvider().getMessageHash(),null);
	}

	/** create a MailTarget with an explicit path and hash from the user.
	 * 
	 * @param handler
	 * @param hash
	 * @param path
	 */
	public MailTarget(MessageHandler handler,int hash, List<String> path) {
		super();
		this.handler = handler;
		this.hash = hash;
		if( path == null){
			this.path=new LinkedList<>();
		}else{
			this.path = path;
		}
	}
	
	private final MessageHandler handler;
	private final int hash;
	private final List<String> path;
	public MessageHandler getHandler() {
		return handler;
	}
	public List<String> getPath() {
		return path;
	}
	public int getMessageHash(){
		if( handler instanceof Contexed && IGNORE_HASH_FEATURE.isEnabled(((Contexed)handler).getContext())){
			return 0;
		}
		return hash;
	}
	public boolean canView(SessionService<?> operator){
		return handler.canView(path, operator);
	}
	public boolean canEdit(SessionService<?> operator){
		if( handler instanceof MessageComposer){
			return ((MessageComposer)handler).canEdit(path, operator);
		}
		return false;
	}
	public boolean hashMatches() throws Exception{
		if( handler instanceof Contexed && IGNORE_HASH_FEATURE.isEnabled(((Contexed)handler).getContext())){
			return true;
		}
		return hash == handler.getMessageProvider().getMessageHash();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + hash;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailTarget other = (MailTarget) obj;
		if (handler == null) {
			if (other.handler != null)
				return false;
		} else if (!handler.equals(other.handler))
			return false;
		if (hash != other.hash)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}