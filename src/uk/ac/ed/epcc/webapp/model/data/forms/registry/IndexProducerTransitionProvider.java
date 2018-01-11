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
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorProducer;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorProducerTransition;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdateProducer;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdateProducerTransition;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdateProducerTransition;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** TrasnitionProvider that provides Create/edit forms for a IndexProducer
 * 
 * @author spb
 *
 * @param <T>
 */


public class IndexProducerTransitionProvider<T extends Indexed> extends SimpleTransitionProvider<T,TransitionKey<T>> {

	private final FormPolicy policy;
	private final TransitionKey<T> update;
	private final TransitionKey<T> create;
	private final TransitionKey<T> edit;
	
	private final SummaryContentProvider<T> prov;
	@SuppressWarnings("unchecked")
	public IndexProducerTransitionProvider(AppContext c,IndexedProducer<T> fac,FormPolicy policy,String target_name) {
		super(c,fac,target_name);
		this.policy=policy;
		
		if( fac instanceof FormCreatorProducer){
			create = new TransitionKey<T>(fac.getTarget(), "Create");
			addTransition(create, new FormCreatorProducerTransition<T>(target_name,(FormCreatorProducer) fac));
		}else{
			create=null;
		}
		
		if( fac instanceof FormUpdateProducer){
			edit = new TransitionKey<T>(fac.getTarget(), "Edit");
			update = new TransitionKey<T>(fac.getTarget(), "Update");
			addTransition(update, new FormUpdateProducerTransition<TransitionKey<T>, T>(target_name, (FormUpdateProducer<T>) fac, this, edit));
			addTransition(edit, new StandAloneFormUpdateProducerTransition<T>(target_name,(FormUpdateProducer<T>) fac));
		}else{
			edit=null;
			update=null;
		}
		if( fac instanceof SummaryContentProvider) {
			prov = (SummaryContentProvider)fac;
		}else {
			prov=null;
		}
	}

	
	public boolean allowTransition(AppContext c,T target, TransitionKey<T> name) {
		if( create != null && name.equals(create) && policy.canCreate(c.getService(SessionService.class))){
			return true;
		}
		if( ((update != null && name.equals(update)) || (edit != null && name.equals(edit))) && policy.canUpdate(c.getService(SessionService.class))){
			return true;
		}
		return false;
	}

	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,T target) {
		if( prov != null) {
			cb = prov.getSummaryContent(c, cb, target);
		}
		return cb;
	}

	
	
	

}