package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;

public class ViewXMLTargetResult extends ViewTransitionResult<XMLTarget, XMLKey> {
	public ViewXMLTargetResult(XMLTarget target){
		super(new DomTransitionProvider(target.getContext()),target);
	}
	
	public ViewXMLTargetResult(AppContext conn,LinkedList<String> path){
		this(DomTransitionProvider.makeXMLTarget(path, conn));
	}
}
