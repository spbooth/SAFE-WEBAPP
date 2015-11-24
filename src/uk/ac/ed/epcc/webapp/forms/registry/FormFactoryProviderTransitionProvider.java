// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.registry;

import java.util.EnumSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorTransition;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdateTransition;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdateTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;
@uk.ac.ed.epcc.webapp.Version("$Id: FormFactoryProviderTransitionProvider.java,v 1.3 2015/03/12 18:45:29 spb Exp $")


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
	public boolean allowTransition(AppContext c,T target, FormOperations name) {
		if( name.equals(FormOperations.Create) && form_factory_provider.canCreate(c.getService(SessionService.class))){
			return true;
		}else if ( (name.equals(FormOperations.Update)|| name.equals(FormOperations.Edit)) && form_factory_provider.canUpdate(c.getService(SessionService.class))){
			return true;
		}else{
			return false;
		}
	}

	public String getID(T target) {
		return form_factory_provider.getID(target);
	}

	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,T target) {
		return cb;
	}

	public T getTarget(String id) {
		return form_factory_provider.getTarget(c, id);
	}

	public String getTargetName() {
		return target_name;
	}

	public Transition<T> getTransition(T target, FormOperations key) {
		try{
			if (key.equals(FormOperations.Create)) {
				return new FormCreatorTransition<T>(form_factory_provider.getName(),form_factory_provider
						.getFormCreator(c));
			}
			FormUpdate<T> formUpdate = form_factory_provider.getFormUpdate(c);
			if (formUpdate instanceof StandAloneFormUpdate) {
				if (key.equals(FormOperations.Update)) {

					return new FormUpdateTransition<FormOperations, T>(
							form_factory_provider.getName(), formUpdate, this,
							FormOperations.Edit);

				} else if (key.equals(FormOperations.Edit)) {
					return new StandAloneFormUpdateTransition<T>(form_factory_provider.getName(),
							(StandAloneFormUpdate<T>) formUpdate);

				}
			}
		}catch(Exception e){
			c.error(e,"Error making transition");
		}
		return null;
	}

	public Set<FormOperations> getTransitions(T target) {
		return EnumSet.allOf(FormOperations.class);
	}

	public FormOperations lookupTransition(T target, String name) {
		return Enum.valueOf(FormOperations.class, name);
	}

	public AppContext getContext() {
		return c;
	}
	public FormOperations getIndexTransition() {
		return FormOperations.Update;
	}
	public <R> R accept(TransitionFactoryVisitor<R,T, FormOperations> vis) {
		return vis.visitTransitionProvider(this);
	}

}