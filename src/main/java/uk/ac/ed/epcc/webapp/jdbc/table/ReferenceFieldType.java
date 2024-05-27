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
package uk.ac.ed.epcc.webapp.jdbc.table;



public class ReferenceFieldType extends IntegerFieldType {
   private final String remote_table;
   private final boolean want_fk;
   /** Constructor for the default nullable reference
    * 
    * @param remote
    */
   public ReferenceFieldType(String remote){
	   this(true,remote);
   }
   /** Constructor that allows the reference to be defined
    * as non-null. When adding foreign keys to these our convention
    * is to cascade delete operations.
    * 
    * @param allow_null
    * @param remote
    */
   public ReferenceFieldType(boolean allow_null,String remote){
	   this(allow_null,remote,false);
   }
   public ReferenceFieldType(boolean allow_null,String remote,boolean want_fk){
	   this(allow_null,remote,null,want_fk);
   }
   public ReferenceFieldType(boolean allow_null,String remote,Integer def,boolean want_fk){
	   super(allow_null,def);
	   remote_table=remote;
	   this.want_fk=want_fk;
   }
   public String getRemoteTable(){
	   return remote_table;
   }
   
   public boolean wantForeighKey() {
	   return want_fk;
   }
   @Override
   public void accept(FieldTypeVisitor vis) {

	   vis.visitReferenceFieldType(this);
   }
}