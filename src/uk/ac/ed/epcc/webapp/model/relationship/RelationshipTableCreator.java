// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.relationship;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.inputs.ClassInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.table.TableListResult;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NewTableInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;

/** Class to create Relationship tables
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RelationshipTableCreator.java,v 1.2 2014/09/15 14:30:34 spb Exp $")

public class RelationshipTableCreator implements FormCreator,Contexed{


	private static final String TYPE = "Type";
	private static final String TABLE = "Table";
	private static final String PEER = "Peer";
	private AppContext conn;
	public RelationshipTableCreator(AppContext c){
		this.conn=c;
	}
	public void buildCreationForm(String type_name,Form f) throws Exception {
		f.addInput(TABLE, "Name of table to create", new NewTableInput(conn));
		f.addInput(TYPE,"Table type",new ClassInput<RelationshipProvider>(conn, RelationshipProvider.class));
		f.addInput(PEER,"Table Relationship applies to", new TableInput<NameFinder>(conn, NameFinder.class));
		f.addAction("Create", new CreateAction());
	}

	public AppContext getContext() {
		return conn;
	}
	public class CreateAction extends FormAction{

	
		@Override
		public FormResult action(Form f) throws ActionException {
			try{
				String table_name=(String) f.get(TABLE);
				String type_tag = (String) f.get(TYPE);
				String peer_table =(String) f.get(PEER);
				f.addInput(PEER, "Peer relashionship applies to", new TableInput<NameFinder>(getContext(), NameFinder.class));
				
				ConfigService serv = conn.getService(ConfigService.class);
				serv.setProperty("class."+table_name, type_tag);
				if( peer_table == null ){
					throw new ConsistencyError("Peer table is null");
				}
			    
			    Relationship.makeTable(conn,table_name, peer_table);
			    	
				return new TableListResult();
			}catch(Exception e){
				conn.error(e,"Error creating table");
				throw new ActionException("Create failed");
			}
		}
		
	}

}