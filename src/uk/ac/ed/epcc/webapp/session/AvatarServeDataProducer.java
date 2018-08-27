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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;

/**
 * @author Stephen Booth
 *
 */
public class AvatarServeDataProducer<AU extends AppUser> implements ServeDataProducer {
	/**
	 * @param conn
	 */
	public AvatarServeDataProducer(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public final AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Tagged#getTag()
	 */
	@Override
	public final String getTag() {
		return "Avatar";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer#getData(uk.ac.ed.epcc.webapp.session.SessionService, java.util.List)
	 */
	@Override
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception {
		AppUserFactory<AU> fac = user.getLoginFactory();
		AvatarComposite<AU> comp = fac.getComposite(AvatarComposite.class);
		if( comp != null && path !=null && path.size() > 1) {
			AU target = fac.find(Integer.parseInt(path.remove(0)));
			if( target != null ) {
				if( ! comp.hasAvatar(user, target)) {
					return null;
				}
				BufferedImage image = comp.getImage(target);
				if( image==null) {
					return null;
				}
				ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
				msd.setMimeType("image/png");
				msd.setName("avatar.png");
				OutputStream s = msd.getOutputStream();
				ImageIO.write(image, "png", s);
				s.close();
				return msd;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer#getDownloadName(uk.ac.ed.epcc.webapp.session.SessionService, java.util.List)
	 */
	@Override
	public String getDownloadName(SessionService user, List<String> path) throws Exception {
		return "avatar.png";
	}

}
