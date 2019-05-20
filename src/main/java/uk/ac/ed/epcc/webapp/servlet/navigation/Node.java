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
package uk.ac.ed.epcc.webapp.servlet.navigation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** This represents a node in the navigational menu.
 * 
 * Its a lightweight representation of the information built by the {@link NavigationMenuService}.
 * It can be sub-classed to support different matching methods.
 * @author spb
 *
 */

public abstract class Node extends NodeContainer implements Externalizable{
	public static final Feature AUTO_LANDING_FEATURE = new Feature("navigation_menu.auto_landing_page", true, "Auto-generate a landing page if one not specified.");
	public Node() {
		super();
	}

	private  String target_url;
	private  String menu_text;
	private String image;
	private String display_class;
	private String help_text;
	private char access_key=0;
	private NodeContainer parent;
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
		if( help_text == null ){
			out.writeObject("");
		}else{
			out.writeObject(help_text);
		}
		out.writeChar(access_key);
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
		setHelpText((String)in.readObject());
		setAccessKey(in.readChar());
		super.readExternal(in);
		
	}

	/** get the location (url not including the context path) the menu item should navigate to.
	 * @return
	 */
	public String getTargetPath(AppContext conn) {

		if( useLandingPage(conn)){
			return "/scripts/landingpage.jsp?MenuNode="+getID();
		}

		return target_url;
	}
	public String getTargetAttr() {
		return null;
	}

	/**
	 * @param conn
	 * @return
	 */
	public boolean useLandingPage(AppContext conn) {
		return target_url == null && AUTO_LANDING_FEATURE.isEnabled(conn);
	}

	/** get the full url for the target location
	 * 
	 * @param request
	 * @return
	 */
	public String getTargetURL(ServletService servlet_service) {
		 
		return servlet_service.encodeURL(getTargetPath(servlet_service.getContext()));
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
	public char getAccessKey(AppContext conn){
		return access_key;
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
	public void setAccessKey(char key){
		access_key=key;
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

	public void setParent(NodeContainer parent){
		this.parent=parent;
	}
	public NodeContainer getParent(){
		return parent;
	}
	@Override
	public void accept(Visitor vis) {
		vis.visitNode(this);
	}
	@Override
	public String toString(){
		if( menu_text != null ){
			return menu_text;
		}
		return super.toString();
	}

	/**
	 * @return the help_text
	 */
	public String getHelpText() {
		return help_text;
	}

	/**
	 * @param help_text the help_text to set
	 */
	public void setHelpText(String help_text) {
		if( help_text != null && help_text.isEmpty()){
			help_text = null;
		}else{
			this.help_text = help_text;
		}
	}
}