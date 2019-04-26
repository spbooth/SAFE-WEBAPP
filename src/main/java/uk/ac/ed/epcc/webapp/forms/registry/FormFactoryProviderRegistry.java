//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.registry;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.servlet.navigation.Node;
import uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker;
import uk.ac.ed.epcc.webapp.servlet.navigation.ParentNode;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Factory class for a set of FormFactoryProviders.
 * 
 * This implements {@link UIGenerator} to emit a set of edit/update buttons
 * and {@link NodeMaker} to expose the same operations as a navigation menu.
 * 
 * @author spb
 *
 * @param <T>
 */
public abstract class FormFactoryProviderRegistry<T extends FormFactoryProvider> extends AbstractContexed implements UIGenerator,TransitionFactoryCreator<TransitionProvider>, NodeMaker{
	public static final char TAG_SEPERATOR = ':';
	
	public FormFactoryProviderRegistry(AppContext conn){
		super(conn);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public TransitionProvider getTransitionProvider(String name){
		T provider = getMap().get(name);
		if( provider != null ){
		    return new FormFactoryProviderTransitionProvider(conn, makeTag(getGroup(), name), provider);
		}
		return null;
	}
	
	
	public TransitionProvider<FormOperations,T> getTransitionProvider(FormFactoryProvider<T> provider){
	    return new FormFactoryProviderTransitionProvider<>(conn, getTag(provider), provider);
		
	}
	
	
	/** get a map from the cleaned {@link #cleanType(String)} tag
	 * to the {@link FormFactoryProvider}.
	 * This is abstract to allow the map to be declared static.
	 * 
	 * @return
	 */
	protected abstract Map<String,T> getMap();
	/** get the tag used to produce this class via AppContext.getFactory
	 * 
	 * @return String group used to identify this class 
	 */
	public abstract String getGroup();
	/** Subject are to use in form title
     * @return String subject
	 */
	public abstract String getTitle();
	
/** Return all FormTypes.
 * This is for use in unit tests
 * @return Iterator<T>
 */
public Iterator<T> getTypes(){
	return getMap().values().iterator();
}
/** Get all FormTypes that a person can update
 * 
 * @param p Person
 * @return Iterator<T>
 */
public  Iterator<T> getUpdaters(SessionService p){
	Set<T> l = new TreeSet<>();
	for(Iterator<T> it=getTypes();it.hasNext();){
		T f = it.next();
		if( f.canUpdate(p)){
			l.add(f);
		}
	}
	return l.iterator();
}
/** Get all FormTypes that a person can create
 * 
 * @param p Person
 * @return Iterator<FormType>
 */
public  Iterator<T> getCreators(SessionService p){
	Set<T> l = new TreeSet<>();
	for(Iterator<T> it=getTypes();it.hasNext();){
		T f = it.next();
		if( f.canCreate(p)){
			l.add(f);
		}
	}
	return l.iterator();
}


public  T find(String name){
	return getMap().get(name);
}
public String getTag(FormFactoryProvider e){
	String type = cleanType(e.getName());
	if( find(type) != e ){
		return null;
	}
	return makeTag(getGroup(),type);
}
/** map a tag string to something that won't break the TransitionServlet
 * 
 * @param type
 * @return modified type string
 */
public static String cleanType(String type){
	return type.replace("/", "-");
}
public static String makeTag(String group,String type){
	return group+TAG_SEPERATOR+cleanType(type);
}
public static String getGroup(String tag){
	int sep=tag.indexOf(TAG_SEPERATOR);
	if( sep < 1 ){
		return null;
	}
	return tag.substring(0, sep);
}
public static String getType(String tag){
	int sep=tag.indexOf(TAG_SEPERATOR);
	if( sep < 1 ){
		return null;
	}
	return tag.substring(sep+1);
}

@Override
public ContentBuilder addContent(ContentBuilder builder) {
	ContentBuilder content = builder.getPanel("block");
	content.addHeading(2, getTitle()+" Create/Edit actions");
	ContentBuilder buttons = content.getPanel("action_buttons");
	boolean seen=false;
	SessionService<?> session_service = getContext().getService(SessionService.class);
	for(Iterator<T> it=getTypes(); it.hasNext(); ){
		T t =  it.next();
		if( ! seen && (t.canCreate(session_service) || t.canUpdate(session_service))){
			seen=true;
		}
		if( t.canCreate(session_service) ){
			FormResult res = new ChainedTransitionResult<T,FormOperations>(getTransitionProvider(t),null,FormOperations.Create){

				@Override
				public boolean useURL() {
					return true;
				}
				
			};
			buttons.addButton(conn, "Create New "+t.getName(), res);
		}
	}
	for(Iterator<T> it=getTypes(); it.hasNext(); ){
		T t=  it.next();
		if( t.canUpdate(session_service) ){
			FormResult res = new ChainedTransitionResult<T,FormOperations>(getTransitionProvider(t),null,FormOperations.Update){

				@Override
				public boolean useURL() {
					return true;
				}
				
			};
			buttons.addButton(conn, "Update "+t.getName(), res);
		}
	}
	if( seen){
		buttons.addParent();
		content.addParent();
	}
	return builder;
}

@Override
public Node makeNode(String name, FilteredProperties props) {
	ParentNode node = new ParentNode();
	node.setMenuText(getTitle());
	SessionService<?> session_service = getContext().getService(SessionService.class);
	for(Iterator<T> it=getTypes(); it.hasNext(); ){
		T t =  it.next();
		if( t.canCreate(session_service) || t.canUpdate(session_service)){
			return node;
		}
	}
	return null;
}

@Override
public void addChildren(Node parent, String name, FilteredProperties props) {
	SessionService<?> session_service = getContext().getService(SessionService.class);
	for(Iterator<T> it=getTypes(); it.hasNext(); ){
		T t =  it.next();
		
		if( t.canCreate(session_service) ){
			Node n = new ParentNode();
			n.setMenuText("Create "+t.getName());
			n.setTargetPath(TransitionServlet.getURL(conn, getTransitionProvider(t), null,FormOperations.Create));
			parent.addChild(n);
		}
		if( t.canUpdate(session_service) ){
			Node n = new ParentNode();
			n.setMenuText("Update "+t.getName());
			n.setTargetPath(TransitionServlet.getURL(conn, getTransitionProvider(t), null,FormOperations.Update));
			parent.addChild(n);
		}
	}
	
}
}