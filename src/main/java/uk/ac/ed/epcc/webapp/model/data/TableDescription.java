//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.model.data;



import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.IndexInfo;

/** Class to show database info about a {@link Repository}
 * @author Stephen Booth
 *
 */
public class TableDescription implements UIGenerator {

	public TableDescription(DataObjectFactory fac) {
		this(fac.res);
	}
	/**
	 * @param res
	 */
	public TableDescription(Repository res) {
		super();
		this.res = res;
	}
	private final Repository res;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder cb) {
		ContentBuilder builder = cb.getDetails( "Database table "+res.getTable());
		res.getIndexNames(); // This will ensure unique info from indexes is present
		Table fields = new Table();
		for( String field : res.getFields()) {
			FieldInfo f = res.getInfo(field);
			fields.put("Type",field,Repository.typeName(f.getType()));
			fields.put("Unique",field,f.isUnique());
			fields.put("Nullable",field,f.getNullable());
			fields.put("Size",field,f.getMax());
			String t = f.getReferencedTable();
			if( t != null) {
				fields.put("References",field,t);
			}
			String fk = f.getForeignKeyName();
			if( fk != null ) {
				fields.put("Fk",field,fk);
			}
		}
		fields.setKeyName("Field");
		builder.addTable(res.getContext(), fields);
		Table indexes = new Table();
		for( String name : res.getIndexNames()) {
			IndexInfo i = res.getIndexInfo(name);
			indexes.put("Unique",name,i.getUnique());
			indexes.put("Fields",name,String.join(",", i.getCols()));
		}
		indexes.setKeyName("Indexes");
		if( indexes.hasData()) {
			builder.addHeading(5, "Indexes");
			builder.addTable(res.getContext(), indexes);
		}
		builder.closeDetails();
		return cb;
	}

}
