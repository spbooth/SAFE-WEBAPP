// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;

/** A SQLAccessor for a field with a {@link TypeProducer}
 * This accessor returns the produced object.
 * 
 * @author spb
 * @param <T> type of owning object
 * @param <O> type of produced object
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TypeProducerFieldValue.java,v 1.2 2014/09/15 14:30:29 spb Exp $")

public class TypeProducerFieldValue<T extends DataObject,O,D> implements FieldValue<O,T>,SQLValue<O> {
	private final Repository repository;
	private final TypeProducer<O,D> producer;
	public TypeProducerFieldValue(Repository repository, TypeProducer<O,D> producer) {
		this.repository=repository;
		this.producer=producer;
		
	}
	
	public O getValue(T r) {
		return r.record.getProperty(producer);
	}
	public TypeProducer<O,D> getProducer(){
		return producer;
	}
	public int add(StringBuilder sb, boolean qualify) {
		repository.getInfo(producer.getField()).addName(sb, qualify, true);
		return 1;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	public O makeObject(ResultSet rs, int pos)
			throws DataException {
		try {
			return producer.find((D) rs.getObject(pos));
		
		} catch (SQLException e) {
			  throw new DataFault("Error making IndexedReferencefield result",e);
		}
	}
	
	
	public void setValue(T r, O value) {
		if( value == null ){
			r.record.setProperty(producer.getField(), null);
		}else{
			r.record.setProperty(producer, value);
		}
		
	}
	public void setObject(PreparedStatement stmt, int pos,
			O value) throws SQLException {
		stmt.setObject(pos,producer.getIndex(value));
		
	}
	public Class<? super O> getTarget() {
		return producer.getTarget();
	}
	
	
	
	public String getFieldName() {
		return producer.getField();
	}
	
	

	public boolean canSet() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return null;
	}

	

}