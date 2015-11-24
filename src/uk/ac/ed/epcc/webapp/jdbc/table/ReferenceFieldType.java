// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: ReferenceFieldType.java,v 1.3 2014/09/15 14:30:26 spb Exp $")


public class ReferenceFieldType extends IntegerFieldType {
   private final String remote_table;
   public ReferenceFieldType(String remote){
	   this(true,remote);
   }
   public ReferenceFieldType(boolean allow_null,String remote){
	   super(allow_null,null);
	   remote_table=remote;
   }
   public String getRemoteTable(){
	   return remote_table;
   }
}