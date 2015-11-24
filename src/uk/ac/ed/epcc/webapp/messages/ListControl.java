// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/** A {@link ResourceBundle.Control} that allows multiple bundles to 
 * be specified as a comma separated list. contents will be merged 
 * into a single bundle.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ListControl.java,v 1.3 2015/06/23 15:29:14 spb Exp $")
public class ListControl extends ResourceBundle.Control{

	/**
	 * 
	 */
	private static final String LIST = "list";

	@Override
	public List<String> getFormats(String baseName) {
		if( baseName != null && baseName.contains(",")){
			return Arrays.asList(LIST);
		}
		return super.getFormats(baseName);
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if(format.equals(LIST)){
			Properties props = null;
			for(String name : baseName.split(",")){
				String bundleName = toBundleName(name, locale);
				String resourceName = toResourceName(bundleName, "properties");
				InputStream stream = loader.getResourceAsStream(resourceName);
				if( stream != null){
					if( props == null){
						props=new Properties();
					}else{
						props = new Properties(props);
					}
					props.load(stream);
				}
			}
			if( props == null){
				// empty bundle needed not null
				props = new Properties();
			}
			return new PropResourceBundle(props);
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}

	

}
