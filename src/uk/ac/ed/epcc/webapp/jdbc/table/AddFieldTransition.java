// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;


import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NewFieldInput;

public abstract class AddFieldTransition<T extends TableStructureTransitionTarget>
		extends AbstractFormTransition<T> {
	
	/**
	 * 
	 */
	static final String ADD_ACTION = "Add";
	public static final String FIELD = "Field";
	protected final Repository res;

	public AddFieldTransition(Repository res){
		this.res=res;
	}
	public final void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		f.addInput(FIELD, "New Field Name", new NewFieldInput(res));
		addFormParams(f, c);
		f.addAction(ADD_ACTION, new AddFieldAction<T>(target));

	}

	protected abstract void addFormParams(Form f, AppContext c);

	protected abstract FieldType getFieldType(Form f);
@uk.ac.ed.epcc.webapp.Version("$Id: AddFieldTransition.java,v 1.5 2014/12/10 15:43:29 spb Exp $")


	public class AddFieldAction<T extends TableStructureTransitionTarget> extends FormAction {

		private final T target;
		public AddFieldAction(T target) {
			this.target=target;
		}

		@Override
		public FormResult action(Form f)
				throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
			try {
				SQLContext sql = res.getSQLContext();
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				query.append(" ADD ");
				String name = (String) f.get(FIELD);
				sql.quote(query,name);
				query.append(" ");
				List<Object> args = new LinkedList<Object>();
				FieldType fieldType = getFieldType(f);
				fieldType.accept(sql.getCreateVisitor(query, args));
				java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString());
				int pos=1;
				for(Object o: args){
					stmt.setObject(pos++, o);
				}
				stmt.execute();
				stmt.close();
				if( fieldType instanceof ReferenceFieldType){
					ReferenceFieldType ref = (ReferenceFieldType) fieldType;
					ConfigService serv = res.getContext().getService(ConfigService.class);
					try{
    					serv.setProperty("reference."+res.getTag()+"."+name, ref.getRemoteTable());
    				}catch(UnsupportedOperationException e){
    					
    				}
				}
				target.resetStructure();
				Repository.reset(res.getContext(), res.getTag());
			} catch (Exception e) {
				throw new ActionException("Update failed",e);
			}
			return new ViewTableResult(target);
		}

	}
}