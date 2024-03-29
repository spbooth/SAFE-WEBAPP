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

import java.util.EnumSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorTransition;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdateTransition;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdateTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.SummaryContentProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;



public class FormFactoryProviderTransitionProvider<T> implements
		IndexTransitionProvider<FormOperations, T> ,TransitionProvider<FormOperations, T>{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((form_factory_provider == null) ? 0 : form_factory_provider
						.hashCode());
		result = prime * result
				+ ((target_name == null) ? 0 : target_name.hashCode());
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
		FormFactoryProviderTransitionProvider other = (FormFactoryProviderTransitionProvider) obj;
		if (form_factory_provider == null) {
			if (other.form_factory_provider != null)
				return false;
		} else if (!form_factory_provider.equals(other.form_factory_provider))
			return false;
		if (target_name == null) {
			if (other.target_name != null)
				return false;
		} else if (!target_name.equals(other.target_name))
			return false;
		return true;
	}
	private final FormFactoryProvider<T> form_factory_provider;
	private final AppContext c;
	private final String target_name;

	public FormFactoryProviderTransitionProvider(AppContext c, String target_name,FormFactoryProvider<T> proviser){
		this.c=c;
		this.target_name=target_name;
		this.form_factory_provider=proviser;
	}
	@Override
	public boolean allowTransition(AppContext c,T target, FormOperations name) {
		if( name.equals(FormOperations.Create) && form_factory_provider.canCreate(c.getService(SessionService.class))){
			return true;
		}else if ( (name.equals(FormOperations.Update)|| name.equals(FormOperations.Edit)) && form_factory_provider.canUpdate(c.getService(SessionService.class))){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String getID(T target) {
		return form_factory_provider.getID(target);
	}

	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,T target) {
		if(form_factory_provider instanceof SummaryContentProvider) {
			cb = ((SummaryContentProvider<T>)form_factory_provider).getSummaryContent(c, cb, target);
		}
		return cb;
	}

	@Override
	public T getTarget(String id) {
		return form_factory_provider.getTarget(c, id);
	}

	@Override
	public String getTargetName() {
		return target_name;
	}

	@Override
	public Transition<T> getTransition(T target, FormOperations key) {
		try{
			if (key.equals(FormOperations.Create)) {
				FormCreator formCreator = form_factory_provider
						.getFormCreator(c);
				if( formCreator == null){
					return null;
				}
				return new FormCreatorTransition<T>(formCreator);
			}
			FormUpdate<T> formUpdate = form_factory_provider.getFormUpdate(c);
			if (formUpdate instanceof StandAloneFormUpdate) {
				if (key.equals(FormOperations.Update)) {

					return new FormUpdateTransition<>(
							form_factory_provider.getName(), formUpdate, this,
							FormOperations.Edit);

				} else if (key.equals(FormOperations.Edit)) {
					return new StandAloneFormUpdateTransition<>(
							(StandAloneFormUpdate<T>) formUpdate);

				}
			}
		}catch(Exception e){
			c.error(e,"Error making transition");
		}
		return null;
	}

	@Override
	public Set<FormOperations> getTransitions(T target) {
		return EnumSet.allOf(FormOperations.class);
	}

	@Override
	public FormOperations lookupTransition(T target, String name) {
		return Enum.valueOf(FormOperations.class, name);
	}

	@Override
	public AppContext getContext() {
		return c;
	}
	@Override
	public FormOperations getIndexTransition() {
		return FormOperations.Update;
	}
	@Override
	public <R> R accept(TransitionFactoryVisitor<R,T, FormOperations> vis) {
		return vis.visitTransitionProvider(this);
	}

}