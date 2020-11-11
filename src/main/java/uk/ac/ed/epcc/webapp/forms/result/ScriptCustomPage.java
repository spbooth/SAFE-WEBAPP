package uk.ac.ed.epcc.webapp.forms.result;

import java.util.Set;

public interface ScriptCustomPage extends CustomPage {
	public Set<String> getAdditionalCSS();
	public Set<String> getAdditionalScript();
}
