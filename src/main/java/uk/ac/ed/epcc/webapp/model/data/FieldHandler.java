package uk.ac.ed.epcc.webapp.model.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/** Interface for classes that own database fields.
 * ie {@link DataObjectFormFactory} super-classes  or {@link Composite}s
 * This associates database field names with additional config tags that can be used to customise behaviour
 * on a field by field basis.
 * Normally configuration qualified by the construction tag of the factory take precedence but if these are not set
 * then the additional config tags can be consulted. This allows config settings to be inherited from super-classes or
 * set based on a composite reducing the requirement for config by code.
 * 
 * The default implementation is to use the {@link ConfigTag} to populate this from public string constants defined in the class
 * but implementing classes are free to override the method. This default behaviour is also available as a static method
 * in case a class wants to query fields in a different class
 * 
 * @see ConfigTag
 * @author Stephen Booth
 *
 */
public interface FieldHandler {
	public default void addConfigTags(Map<String,String> config_tags) throws Exception{
		Class c = getClass();
		addConfigTags(c, config_tags);
	}
	/** Default implementation is to use the {@link ConfigTag} annotation on any public string constant.
	 * 
	 * @param clazz
	 * @param config_tags
	 * @throws Exception
	 */
	public static void addConfigTags(Class clazz,Map<String,String> config_tags) throws Exception{
		for(Field f : clazz.getFields()) {
			if( f.getType() == String.class) {
				ConfigTag tag = f.getAnnotation(ConfigTag.class);
				if( tag != null && Modifier.isStatic(f.getModifiers())) {
					config_tags.put((String) f.get(null), tag.value());
				}
			}
		}
	}
}
