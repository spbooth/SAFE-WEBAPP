// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.swing;

import javax.swing.JComponent;
import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.CustomFormContent;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition;
import uk.ac.ed.epcc.webapp.session.SessionService;
@uk.ac.ed.epcc.webapp.Version("$Id: SwingTransitionVisitor.java,v 1.5 2014/09/15 14:30:22 spb Exp $")


public class SwingTransitionVisitor<K,T> extends AbstractTransitionVisitor<K,T> {
	private JFrame parent;
	public SwingTransitionVisitor(AppContext conn, K key,
			TransitionFactory<K, T> tp, T target, JFrame parent) {
		super(conn,key,tp,target);
		this.parent=parent;
	}

	

	public FormResult doFormTransition(FormTransition<T> t)
			throws TransitionException {
		
		return doTransition(t);
	}

	public FormResult doValidatingFormTransition(ValidatingFormTransition<T> t)
			throws TransitionException {
		
		return doTransition(t);
	}

	public FormResult doTargetLessTransition(TargetLessTransition<T> t)
			throws TransitionException {
		
		return doTransition(t);
	}

	private FormResult doTransition(Transition<T> t) throws TransitionException{
		String action = tag.toString();
	    String type=provider.getTargetName();
	    String type_title = conn.getInitParameter("transition_title."+type,type);
	    SessionService session_service = conn.getService(SessionService.class);
	    if( type==null) type="";
		String page_title = action+" "+type_title;
		BaseForm base = new BaseForm(conn);
		JFormDialog dialog = new JFormDialog(conn, parent);
		SwingContentBuilder builder = dialog.getContentBuilder();
		boolean validate=false;
		if( t instanceof FormTransition ){
			FormTransition<T> ft = (FormTransition<T>) t;
			ft.buildForm(base,target,conn);
		}else if( t instanceof ValidatingFormTransition ){
				ValidatingFormTransition<T> ft = (ValidatingFormTransition<T>) t;
				ft.buildForm(base,target,conn);
				validate=true;
		}else if( t instanceof TargetLessTransition ){
			TargetLessTransition<T> tlt = (TargetLessTransition<T>) t;
			tlt.buildForm(base,conn);
		}
		
		if(target != null ){
			provider.getSummaryContent(getContext(), builder.getPanel("summary"), target).addParent();
		} 
	
		if( t instanceof ExtraFormTransition ){
			((ExtraFormTransition<T>) t).getExtraHtml(builder.getPanel("extra"), session_service, target).addParent();
		} 
		if( t instanceof CustomFormContent){
			((CustomFormContent)t).addFormContent(builder.getPanel("form"), session_service, base, target).addParent();
		}else{
			SwingContentBuilder f = (SwingContentBuilder) builder.getPanel("form");
			// default table formatting
			f.addFormTable(conn, base,validate);
			f.addActionButtons(base);
			f.addParent();
		}
		dialog.setTitle(page_title);
		return dialog.showForm(base);
	}
}