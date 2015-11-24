// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.jdbc.table;

/** A Null {@link FieldType} used to reserve the position of a field.
 * 
 * If a fields with the same name is added later it will replace the placeholder in the same position.
 * If left in the specification a {@link PlaceHolderFieldType} has no affect.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class PlaceHolderFieldType extends FieldType<Object> {

	
	public PlaceHolderFieldType() {
		super(Object.class, true, null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldType#accept(uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor)
	 */
	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitPlaceHolderFieldType(this);
		
	}

}
