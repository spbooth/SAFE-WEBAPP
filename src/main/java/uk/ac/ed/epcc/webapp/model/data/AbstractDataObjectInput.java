package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Labeller;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;

/** A form Input used to select objects produced by the owning factory.
 * 
 * @author spb
 *
 */
public abstract class AbstractDataObjectInput<BDO extends DataObject> extends AbstractSuggestedInput<BDO> implements PreSelectInput<Integer,BDO>{
	
	
	public AbstractDataObjectInput(DataObjectFactory<BDO> dataObjectFactory, BaseFilter<BDO> f) {
		this(dataObjectFactory, null,f);
	}
	private int max_identifier=DataObject.MAX_IDENTIFIER;
	boolean allow_pre_select=true;
	public AbstractDataObjectInput(DataObjectFactory<BDO> dataObjectFactory, BaseFilter<BDO> view_fil,BaseFilter<BDO> restrict_fil) {
		super(dataObjectFactory,view_fil, restrict_fil);
		AppContext con = getContext();
		max_identifier = con.getIntegerParameter(dataObjectFactory.getConfigTag()+".maxIdentifier", con.getIntegerParameter("DataObject.MaxIdentifier", DataObject.MAX_IDENTIFIER));
		allow_pre_select = con.getBooleanParameter(dataObjectFactory.getConfigTag()+".allowPreSelect", true);
	}
	
	
	private Labeller<? super BDO, String> labeller=null;

	@Override
	public String getPrettyString(Integer val) {
		// don't apply validation for getPrettyString
		// we want to be able to format an invalid value for 
		String res =  getText(getItembyValue(val));
		if( res == null ){
			res = "Not Selected";
		}
		return res;
	}

	@Override
	public String getTagByValue(Integer id) {
		return id.toString();
	}

	@Override
	public String getText(BDO obj) {
		if( obj == null ){
			return null;
		}
		if( labeller != null ) {
			return labeller.getLabel(getContext(), obj);
		}
		String result = obj.getIdentifier(max_identifier);
		
		if ( result != null && result.length() > max_identifier) {
			result = result.substring(0, max_identifier);
		}
		if( result == null || result.trim().length() == 0 ){
			return "Un-named object "+obj.getID();
		}
		return result;
	}
	
	
	@Override
	public String getTagByItem(BDO item) {
		return Integer.toString(getValueByItem(item));
	}
	
	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	@Override
	public boolean allowPreSelect() {
		return allow_pre_select;
	}
	@Override
	public void setPreSelect(boolean value) {
		allow_pre_select=value;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(BDO item) {
		if(item == null) {
			return false;
		}
		try {
			validate(item.getID());
		} catch (FieldException e) {
			return false;
		}
		return true;
	}
	public int getMaxIdentifier() {
		return max_identifier;
	}
	public void setMaxIdentifier(int max_identifier) {
		this.max_identifier = max_identifier;
	}
	public Labeller<? super BDO, String> getLabeller() {
		return labeller;
	}
	public void setLabeller(Labeller<? super BDO, String> labeller) {
		this.labeller = labeller;
	}
}