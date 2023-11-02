package uk.ac.ed.epcc.webapp;

public interface ContextProvider {

	// we could provide a default method that calls AppContext#getContext()
	// but this prevents us being able to create an appcontext as 
	AppContext getContext();

}