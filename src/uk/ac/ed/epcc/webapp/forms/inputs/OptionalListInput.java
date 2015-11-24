package uk.ac.ed.epcc.webapp.forms.inputs;


public interface OptionalListInput<V,T> extends OptionalInput, ListInput<V, T> {
	public String getUnselectedText();
	public void setUnselectedText(String text);
}
