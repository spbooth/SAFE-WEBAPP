//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.period;

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.BoundedDateInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public class MoveDateTransition<T extends TimePeriod,K> extends AbstractFormTransition<T>{
	public class MoveDateAction extends FormAction{
		
		
		private final T target;
		private final boolean move_start;
		public MoveDateAction(boolean move_start,T targ){
			target=targ;
			this.move_start=move_start;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			Date d = (Date) f.get(MoveDateTransition.DATE_FIELD);
			Boolean move_split = (Boolean) f.get(MoveDateTransition.MOVE_SPLIT_FIELD);
		
			T next_seq = fac.getNextInSequence(target, ! move_start);
			try{
			if( move_start ){
				fac.setStart(target, d);
			}else{
				fac.setEnd(target, d);
			}
			if( move_split != null && move_split.booleanValue()){
				if( next_seq != null){
					if( move_start ){
						fac.setEnd(next_seq, d);
					}else{
						fac.setStart(next_seq, d);
					}
				}
			}
			}catch(Exception e){
				tp.getContext().error(e,"Internal error");
				throw new ActionException("Internal error");
			}
			return new ViewTransitionResult<>(tp, target);
		}
		@Override
		public String getConfirm(Form f) {
			String old= super.getConfirm(f);
			if( old != null ){
				return old;
			}
			Date d = (Date) f.get(MoveDateTransition.DATE_FIELD);
			Date e = fac.getEditMarker();
			if( e != null && d.before(e)){
				return Confirmations.PAST_EDIT_CONFIRM;
			}
			return null;
		}
		
	}
	public class MoveValidator implements FormValidator{
		private final T current;
    	private final T bound;
    	private final boolean move_start;
		public MoveValidator(boolean move_start,T current,T next_seq) {
			this.move_start=move_start;
			this.current=current;
			bound = next_seq;
		}

		
		@Override
		public void validate(Form f) throws ValidateException {
			
			Boolean move = (Boolean) f.get(MoveDateTransition.MOVE_SPLIT_FIELD);
			Date d = (Date) f.get(MoveDateTransition.DATE_FIELD);
			if( move_start){
				fac.canChangeStart(current, d);
			}else{
				fac.canChangeEnd(current, d);
			}
			if( ! move ){
				if( (move_start && d.before(bound.getEnd())) ||
					((! move_start) && d.after(bound.getStart()))){
			
				throw new ValidateException(DATE_FIELD,"New date impacts on neighbour");
			
				}
			}
		}
    	
    }
	private final boolean move_start;
	private final ViewTransitionFactory<K, T> tp;
	private final SequenceManager<T> fac;
	private static final String MOVE_SPLIT_FIELD = "MoveSplit";
	private static final String DATE_FIELD = "Date";
	public MoveDateTransition(ViewTransitionFactory<K,T> tp,SequenceManager<T> fac,boolean  move_start){
		this.tp=tp;
		this.fac=fac;
		this.move_start=move_start;
	}
	@Override
	public void buildForm(Form f, T target, AppContext conn)
			throws TransitionException {
		Date def;
		String label;
		boolean move_split=false;
		Date min_date=null;
		Date max_date=null;
		T next_seq = null;
		if( fac.noOverlapps(target)) {
			next_seq = fac.getNextInSequence(target, !move_start);
		}
		if( move_start ){
			def = target.getStart();
			label="Start Date";
			max_date=new Date(target.getEnd().getTime()-1); // don't allow zero length
			if( next_seq != null ){
				min_date=next_seq.getEnd();
				if(next_seq.getEnd().equals(target.getStart())){
					min_date = next_seq.getStart();
					move_split=true;
				}
			}
		}else{
			def = target.getEnd();
			label="End Date";
			min_date=new Date(target.getStart().getTime()+1); // don't allow zero
			if( next_seq != null ){
				max_date=next_seq.getStart();
				if(next_seq.getStart().equals(target.getEnd())){
					max_date = next_seq.getEnd();
					move_split=true;
				}
			}
		}
		
		BoundedDateInput input = fac.getDateInput();
		input.setValue(def);
		input.setMin(min_date);
		input.setMax(max_date);
		f.addInput(MoveDateTransition.DATE_FIELD, label, input);
		if( move_split ){
			f.addInput(MoveDateTransition.MOVE_SPLIT_FIELD,"Move boundary with neighbour",new BooleanInput());
			f.addValidator(new MoveValidator(move_start,target,next_seq));
		}
		f.addAction("Change", new MoveDateAction(move_start, target));
		Date e = fac.getEditMarker();
		if( e != null && def.before(e)){
			f.setConfirm("Change", Confirmations.PAST_EDIT_CONFIRM);
		}
		
	}
	
}