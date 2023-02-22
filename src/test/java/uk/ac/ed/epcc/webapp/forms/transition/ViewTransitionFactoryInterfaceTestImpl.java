//| Copyright - The University of Edinburgh 2012                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import uk.ac.ed.epcc.webapp.TestDataHelper;
import uk.ac.ed.epcc.webapp.apps.CheckClassProperties;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;
import uk.ac.ed.epcc.webapp.session.SessionService;
/**
 * @author spb
 * @param <T> target type
 * @param <K> key type 
 * @param <X> type of {@link ViewTransitionProvider}
 *
 */

public class ViewTransitionFactoryInterfaceTestImpl<T,K,X extends ViewTransitionFactoryDataProvider<K,T>> implements ViewTransitionFactoryInterfaceTest<T,K,X> {

	protected final X provider;
	/**
	 * 
	 */
	public ViewTransitionFactoryInterfaceTestImpl(X provider) {
		this.provider=provider;
	}
	
	public void testCanView() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			assertTrue(fac.canView(target, provider.getAllowedUser(target)));
			// This is not strictly necessary but for robustness allowTransition
			// should be compatible with canView and not throw exceptions
			assertTrue(fac.allowTransition(provider.getContext(), target, null));
			SessionService<?> forbiddenUser = provider.getForbiddenUser(target);
			if( forbiddenUser != null ){
				assertFalse(fac.canView(target, forbiddenUser));
				assertFalse(fac.allowTransition(provider.getContext(), target, null));
			}
		}
	}

	
	
	public void testGetTopContent() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			builder.setValidXML(true);
			builder.open("top");
			fac.getTopContent(builder, target, provider.getAllowedUser(target));
			builder.close();
			String top = builder.toString();
			String expected_name = provider.getTopContentExpected(target);
			if( expected_name != null) {
				checkContent(null, expected_name, top);
			}
		}
	}
	
	public void testGetLogContent() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			builder.setValidXML(true); // we want to apply transforms
			builder.open("log");
			SessionService<?> user = provider.getAllowedUser(target);
			fac.getLogContent(builder, target, user);
			builder.close();
			String log = builder.toString();
			String expected_name = provider.getLogContentExpected(target);
			if( expected_name != null) {
				checkContent(null, expected_name, log);
			}
		}
	}
	
	
	public void testGetHelp() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			for(K key : fac.getTransitions(target)){
				// Just checking for exceptions
				fac.getHelp(key);
			}
		}
	}
	
	public void checkContent(String normalize_transform, String expected_xml, String content)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory tfac = TransformerFactory.newInstance();
		 Transformer tt;
		 if( normalize_transform == null ){
			 normalize_transform="/normalize.xsl";
		 }
			 Source source = XMLDataUtils.readResourceAsSource(provider.getClass(), normalize_transform);
			 assertNotNull(expected_xml,source);
			 tt = tfac.newTransformer(source);
		 
		 assertNotNull(tt);
		 
		String result = XMLDataUtils.transform(tt, content);
		
		 String expected = XMLDataUtils.transform(tt,provider.getClass(), expected_xml);
		 
		 String differ = TestDataHelper.diff(expected, result);
		 boolean same = differ.trim().length()==0;
		 if( ! same ){
			 System.out.println(content);
//			 try {
//				TestDataHelper.writeFile(new File(expected_xml), content);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		 }
		assertEquals("Unexpected result:"+expected_xml+"\n"+differ,expected,result);
	}
}