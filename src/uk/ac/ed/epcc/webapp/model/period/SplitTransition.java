package uk.ac.ed.epcc.webapp.model.period;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampMultiInput;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public class SplitTransition<T extends TimePeriod,K> extends AbstractFormTransition<T>{
	public class SplitAction extends FormAction{
		
		private final boolean nav_first;
		private final T target;
		public SplitAction(T target,boolean nav_first){
			this.nav_first=nav_first;
			this.target=target;
		}
		@Override
		public String getHelp() {
			if( nav_first ){
				return "Split period and edit earier section";
			}else{
				return "Split period and edit later section";
			}
		}

		@Override
		public ViewTransitionResult<T, K> action(Form f) throws ActionException {
			Date split = (Date) f.get("Date");
			try {
				T later = fac.split(target, split);
				if( later == null ){
					throw new TransitionException("Split failed");
				}
				if( nav_first){
					return new ViewTransitionResult<T, K>(tp, target);
				}else{
					return new ViewTransitionResult<T, K>(tp, later);
				}
			} catch (Exception e) {
				throw new ActionException("Internal error in split", e);
			}
		}
		@Override
		public String getConfirm(Form f) {
			
			String result = super.getConfirm(f);
			if( result != null ){
				return result;
			}
			Date split = (Date) f.get("Date");
			Date e = fac.getEditMarker();
			if( split != null && e != null && split.before(e)){
				return Confirmations.PAST_EDIT_CONFIRM;
			}
			return null;
		}
		
	}
	public class SplitValidator implements FormValidator{
		/**
		 * @param target
		 */
		public SplitValidator(T target) {
			super();
			this.target = target;
		}
		private final T target;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FormValidator#validate(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		public void validate(Form f) throws ValidateException {
			Date split = (Date) f.get("Date");
			fac.canSplit(target, split);
		}
		
	}
	public SplitTransition(ViewTransitionFactory<K,T> tp,SplitManager<T> fac) {
		super();
		this.tp=tp;
		this.fac = fac;
	}


	private final SplitManager<T> fac;
	private final ViewTransitionFactory<K, T> tp;
	
	public void buildForm(Form f, T target, AppContext conn)
			throws TransitionException {
		Date start=target.getStart();
		Date end = target.getEnd();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.add(Calendar.MONTH,1);
		TimeStampMultiInput input = fac.getDateInput();
		input.setValue(cal.getTime());
		f.addInput("Date", "Split date", input );
		
		f.addValidator(new RangeValidator("Date", 
					start,
					end));
		f.addValidator(new SplitValidator(target));
		
		f.addAction("<<Split", new SplitAction(target,  true));
		f.addAction("Split>>", new SplitAction(target,  false));
	}
	
}