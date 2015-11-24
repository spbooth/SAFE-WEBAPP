// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;

/**
 * @author spb
 * @param <T> type of field data
 * @param <X> type of owning DataObject
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface FieldSQLExpression<T,X> extends FieldValue<T, X> , SQLExpression<T>{

}
