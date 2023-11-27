package uk.ac.ed.epcc.webapp.model.data;

import java.util.function.UnaryOperator;

import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.table.*;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.model.data.forms.CheckboxSelector;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
/** A {@link FieldTypeVisitor} that generates a {@link Selector} for the database field.
 * 
 * It should only generate Selectors for cases where the correct input type can't be derived from the 
 * Repository 
 * 
 */
public class MakeSelectorVisitor implements FieldTypeVisitor {

	private final Repository res;
	private Selector sel=null;
	private final String field;
	public MakeSelectorVisitor(Repository res,String field) {
		this.res=res;
		this.field=field;
	}
	public Selector getSelector() {
		return sel;
	}

	@Override
	public void visitStringFieldType(StringFieldType stringFieldType) {

	}

	@Override
	public <N extends Number> void visitNumberFieldType(NumberFieldType<N> numberFieldType) {

	}

	@Override
	public void visitDateFieldType(DateFieldType dateFieldType) {
		if( dateFieldType.isTruncate()) {
			sel = new DateSelector(res.getResolution());
		}else {
			sel = new TimeStampSelector(res.getResolution());
		}
		

	}

	@Override
	public void visitBooleanFieldType(BooleanFieldType booleanFieldType) {
		if( res.getInfo(field).isString()) {
			sel = new CheckboxSelector();
		}else {
			sel = BooleanInput::new;
		}
	}

	@Override
	public void visitBlobType(BlobType blobType) {
		

	}

	@Override
	public void visitAutoIncrement() {
		

	}

	@Override
	public void visitIndex(UnaryOperator<String> name_map, Index i) {
		

	}

	@Override
	public void visitFullTextIndex(UnaryOperator<String> name_map, FullTextIndex i) {
		

	}

	@Override
	public void visitPlaceHolderFieldType(PlaceHolderFieldType p) {
		
	}

	@Override
	public void additions(boolean create) {


	}

	@Override
	public boolean useIndex(IndexType i) {
		
		return false;
	}

	@Override
	public void visitForeignKey(String name, String prefix,ReferenceFieldType referenceField) {
		
	}

}
