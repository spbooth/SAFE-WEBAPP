// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** This represents a node in the navigational menu.
 * 
 * Its a lightweight representation of the information built by the {@link NavigationMenuService}.
 * It can be sub-classed to support different matching methods.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.7 $")
public abstract class Node extends NodeContainer implements Externalizable{
	
	public Node() {
		super();
	}

	private  String target_url;
	private  String menu_text;
	private String image;
	private String display_class;
    /* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if( target_url == null ){
			out.writeObject("");
		}else{
			out.writeObject(target_url);
		}
		if( menu_text == null ){
			out.writeObject("");
		}else{
			out.writeObject(menu_text);
		}
		if( image == null ){
			out.writeObject("");
		}else{
			out.writeObject(image);
		}
		if( display_class == null){
			out.writeObject("");
		}else{
			out.writeObject(display_class);
		}
		super.writeExternal(out);
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setTargetPath((String)in.readObject());
		setMenuText((String)in.readObject());
		setImage((String) in.readObject());
		setDisplayClass((String)in.readObject());
		super.readExternal(in);
		
	}

	/** get the location (url not including the context path) the menu item should navigate to.
	 * @return
	 */
	public String getTargetPath() {
		return target_url;
	}

	/** get the full url for the target location
	 * 
	 * @param request
	 * @return
	 */
	public String getTargetURL(ServletService servlet_service) {
		 
		return servlet_service.encodeURL(getTargetPath());
	}
	/** get the menu text
	 * 
	 * @return
	 */
	public String getMenuText(AppContext conn) {
		return menu_text;
	}
	
	public String getDisplayClass(AppContext conn){
		return display_class;
	}
	/** set the location (notmally not including the context path the menu item should navigate to.
	 * 
	 * @param target
	 */
	public void setTargetPath(String target) {
		if( target != null && target.length() == 0){
			target=null;
		}else{
			target_url=target;
		}
	}

	public void setMenuText(String text) {
		if( text != null && text.length() == 0){
			menu_text=null;
		}else{
			menu_text=text;
		}
	}
	public void setDisplayClass(String text) {
		if( text != null && text.length() == 0){
			display_class=null;
		}else{
			display_class=text;
		}
	}
	/** does the original requested page match the target space
	 * 
	 * @param serv
	 * @return
	 */
	public abstract boolean matches(ServletService serv);

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		if( image != null && image.length() == 0){
			image=null;
		}else{
			this.image = image;
		}
	}
}
