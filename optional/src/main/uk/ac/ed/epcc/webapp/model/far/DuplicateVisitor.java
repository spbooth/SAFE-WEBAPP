//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.far;


import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;

/** A visitor to perform a recursive duplication of a part
 * @author spb
 *
 */
public class DuplicateVisitor implements PartVisitor<PartManager.Part> {

	private PartOwner owner;
	/**
	 * @param owner {@link PartOwner} to own the duplicate.
	 * 
	 */
	public DuplicateVisitor(PartOwner owner) {
		this.owner=owner;
	}
	
	/** duplicate all children of a {@link PartOwner} into the current cached owner.
	 * 
	 * @param manager
	 * @param original
	 * @throws DataException
	 */
	public <O extends PartOwner,P extends Part<O>> void visitOwner(PartOwnerFactory<O> manager,O original) throws DataException{
		PartManager<O, P> child_manager = manager.getChildManager();
		if( child_manager != null ){
			// duplicate children ino the current owner
			for(P child : child_manager.getParts(original)){
				visitPart(child);
			}
		}
	}
	
	private <O extends PartOwner,P extends Part<O>> P visitPart(P original) throws DataException{
		PartManager<O, P> manager = (PartManager<O, P>) original.getFactory();
		P duplicate = manager.duplicate((O)owner, original);
		duplicate.commit();
		
		PartOwner parent = owner;
		owner=duplicate;
		visitOwner(manager, original);
		owner=parent;
		
		// duplicate config if we have any
		PartConfigFactory<O, P> config = manager.getConfigFactory();
		if( config != null ){
			config.setValues(duplicate, config.getValues(original));
		}
		return duplicate;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public Part visitPage(Page p) {
		try {
			return visitPart(p);
		} catch (DataException e) {
			getLogger(p).error("Error in duplate", e);
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public Part visitSection(Section p) {
		try {
			return visitPart(p);
		} catch (DataException e) {
			getLogger(p).error("Error in duplate", e);
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public Part visitQuestion(Question p) {
		try {
			return visitPart(p);
		} catch (DataException e) {
			getLogger(p).error("Error in duplate", e);
			return null;
		}
	}

	Logger getLogger(Part p){
		return Logger.getLogger(p.getContext(),getClass());
	}
}
