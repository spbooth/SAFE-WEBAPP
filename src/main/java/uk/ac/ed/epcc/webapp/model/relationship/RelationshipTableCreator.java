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
package uk.ac.ed.epcc.webapp.model.relationship;



import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
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
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NewTableInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;

/** Class to create Relationship tables
 * 
 * @author spb
 *
 */


public class RelationshipTableCreator extends AbstractContexed implements FormCreator{


	private static final String TYPE = "Type";
	private static final String TABLE = "Table";
	private static final String PEER = "Peer";
	public RelationshipTableCreator(AppContext c){
		super(c);
	}
	@Override
	public void buildCreationForm(String type_name,Form f) throws Exception {
		f.addInput(TABLE, "Name of table to create", new NewTableInput(conn));
		f.addInput(TYPE,"Table type",new ClassInput<>(conn, RelationshipProvider.class));
		f.addInput(PEER,"Table Relationship applies to", new TableInput<>(conn, NameFinder.class));
		f.addAction("Create", new CreateAction());
	}

	public class CreateAction extends FormAction{

	
		@Override
		public FormResult action(Form f) throws ActionException {
			try{
				String table_name=(String) f.get(TABLE);
				String type_tag = (String) f.get(TYPE);
				String peer_table =(String) f.get(PEER);
				f.addInput(PEER, "Peer relashionship applies to", new TableInput<>(getContext(), NameFinder.class));
				
				ConfigService serv = conn.getService(ConfigService.class);
				serv.setProperty("class."+table_name, type_tag);
				if( peer_table == null ){
					throw new ConsistencyError("Peer table is null");
				}
			    if( ! DataObjectFactory.AUTO_CREATE_TABLES_FEATURE.isEnabled(conn)){
			    	// make a relationship table if class won't auto create
			    	Relationship.makeTable(conn,table_name, peer_table);
			    }else {
			    	// Need reference tag for constructor to work
			    	serv.setProperty(Relationship.getReferenceProp(table_name), peer_table);
			    }
			    	
				return new TableListResult();
			}catch(Exception e){
				conn.error(e,"Error creating table");
				throw new ActionException("Create failed");
			}
		}
		
	}

}