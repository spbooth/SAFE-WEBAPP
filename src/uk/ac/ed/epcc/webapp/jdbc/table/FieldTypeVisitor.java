// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
/** An visitor to create parts of the table creation syntax.
 * 
 * @author spb
 *
 */
public interface FieldTypeVisitor {
	public void visitStringFieldType(StringFieldType stringFieldType);
	public <N extends Number> void visitNumberFieldType(NumberFieldType<N> numberFieldType);
	public void visitDateFieldType(DateFieldType dateFieldType);
    public void visitBooleanFieldType(BooleanFieldType booleanFieldType);
    public void visitBlobType(BlobType blobType);
    public void visitAutoIncrement();
    public void visitIndex(Index i);
    public void visitFullTextIndex(FullTextIndex i);
    public void visitPlaceHolderFieldType(PlaceHolderFieldType p);
   
    /** Do we use this type of index
     * 
     * @param i
     * @return
     */
    public boolean useIndex(IndexType i);
    
    /** Add a foreign key definition for a reference field. 
     * 
     * @param referenceField
     */
    public void visitForeignKey(String name,ReferenceFieldType referenceField);
}