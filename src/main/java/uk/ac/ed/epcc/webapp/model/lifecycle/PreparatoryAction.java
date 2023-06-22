package uk.ac.ed.epcc.webapp.model.lifecycle;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
/** An adaptor that converts an {@link ActionListener}
 * into a {@link LifeCycleListener} triggering in the prepare stage.
 * 
 * The construction tag for the nested {@link ActionListener} is specified
 * in the property <b><i>tag</i>.action</b>
 * 
 * @author Stephen Booth
 *
 * @param <R>
 */
public class PreparatoryAction<R> implements LifeCycleListener<R>, Contexed {

	private final ActionListener<R> action;
	private final AppContext conn;
	public PreparatoryAction(AppContext conn,String tag) {
		this.conn=conn;
		action = conn.makeObject(ActionListener.class, conn.getInitParameter(tag+".action"));
	}
	@Override
	public Class<R> getTarget() {
		return action.getTarget();
	}
	@Override
	public boolean allow(R target, boolean throw_reason) throws LifeCycleException {
		return action.allow(target, throw_reason);
	}
	@Override
	public Object getWarning(R target) {
		return action.getWarning(target);
	}
	@Override
	public void prepare(R target) throws Exception {
		action.action(target);
	}
	@Override
	public AppContext getContext() {
		return conn;
	}

}
