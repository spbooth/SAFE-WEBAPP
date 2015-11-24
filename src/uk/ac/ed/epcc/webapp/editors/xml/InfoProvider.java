package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.TypeInfo;

public interface InfoProvider {

	public abstract Map<LinkedList<String>, Set<String>> getErrors();

	public Set<String> getError(LinkedList<String> path);
	
	public abstract Map<LinkedList<String>, TypeInfo> getTypes();

	public TypeInfo getTypeInfo(LinkedList<String> path);
}