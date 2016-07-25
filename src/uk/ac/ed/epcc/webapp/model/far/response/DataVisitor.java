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
package uk.ac.ed.epcc.webapp.model.far.response;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PageManager;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.PartOwner;
import uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory;
import uk.ac.ed.epcc.webapp.model.far.PartVisitor;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** A {@link PartVisitor} that builds a multi-level map of response data
 * @author spb
 *
 */
public class DataVisitor<D extends DynamicForm,R extends Response<D>> implements PartVisitor<Object>{

	private final R response;
	private final Logger log;
	/**
	 * 
	 */
	public DataVisitor(R response) {
		this.response=response;
		log = response.getContext().getService(LoggerService.class).getLogger(getClass());
	}

	public Map<String,Object> visitForm(D form){
		return visitOwner(form.getFactory(), form);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public Map<String, Object> visitPage(Page p) {
		return visitOwner((PageManager)p.getFactory(), p);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public Map<String, Object> visitSection(Section s) {
		return visitOwner(((SectionManager)s.getFactory()), s);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public Object visitQuestion(Question q) {
		ResponseData<Object, ? extends Response<D>,D> wrapper;
		try {
			wrapper = response.getWrapper(q);
			
			if(wrapper != null &&  wrapper.hasData()){
				return wrapper.getData();
			}
			
		} catch (Exception e) {
			log.error("Error checking completness", e);
		
		}
		
		return null;
	}
	public <O extends PartOwner> Map<String,Object> visitOwner(PartOwnerFactory<O> my_manager, O owner)  {
		
		PartManager<O,?> manager = my_manager.getChildManager();
		if( manager != null ){
			try{
				Map<String,Object> result = new LinkedHashMap<String,Object>();
				for(Part child : manager.getParts(owner)){
					Object dat = child.visit(this);
					if( dat != null ){
						result.put(child.getName(), dat);
					}
				}
				return result;
			}catch(Exception e){
				log.error("Error getting response data", e);
			}
		}
		return null;
	}
}
