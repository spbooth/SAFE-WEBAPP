//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.mock;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;



/**
 * @author spb
 *
 */

public class MockDirContext  implements DirContext {

	/**
	 * @param principal
	 * @param password
	 */
	public MockDirContext(String principal, String password) {
		super();
		this.principal = principal;
		this.password = password;
	}

	private final String principal;
	private final String password;
	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(javax.naming.Name)
	 */
	@Override
	public Object lookup(Name name) throws NamingException {
		throw new NamingException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(java.lang.String)
	 */
	@Override
	public Object lookup(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void bind(Name name, Object obj) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void bind(String name, Object obj) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void rebind(String name, Object obj) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(javax.naming.Name)
	 */
	@Override
	public void unbind(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(java.lang.String)
	 */
	@Override
	public void unbind(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
	 */
	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
	 */
	@Override
	public void rename(String oldName, String newName) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#list(javax.naming.Name)
	 */
	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#list(java.lang.String)
	 */
	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#listBindings(javax.naming.Name)
	 */
	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#listBindings(java.lang.String)
	 */
	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
	 */
	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#destroySubcontext(java.lang.String)
	 */
	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#createSubcontext(javax.naming.Name)
	 */
	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#createSubcontext(java.lang.String)
	 */
	@Override
	public Context createSubcontext(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookupLink(javax.naming.Name)
	 */
	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookupLink(java.lang.String)
	 */
	@Override
	public Object lookupLink(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameParser(javax.naming.Name)
	 */
	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameParser(java.lang.String)
	 */
	@Override
	public NameParser getNameParser(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
	 */
	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
	 */
	@Override
	public String composeName(String name, String prefix)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
	 */
	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getEnvironment()
	 */
	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#close()
	 */
	@Override
	public void close() throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameInNamespace()
	 */
	@Override
	public String getNameInNamespace() throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name)
	 */
	@Override
	public Attributes getAttributes(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String)
	 */
	@Override
	public Attributes getAttributes(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name, java.lang.String[])
	 */
	@Override
	public Attributes getAttributes(Name name, String[] attrIds)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String, java.lang.String[])
	 */
	@Override
	public Attributes getAttributes(String name, String[] attrIds)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name, int, javax.naming.directory.Attributes)
	 */
	@Override
	public void modifyAttributes(Name name, int mod_op, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String, int, javax.naming.directory.Attributes)
	 */
	@Override
	public void modifyAttributes(String name, int mod_op, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name, javax.naming.directory.ModificationItem[])
	 */
	@Override
	public void modifyAttributes(Name name, ModificationItem[] mods)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String, javax.naming.directory.ModificationItem[])
	 */
	@Override
	public void modifyAttributes(String name, ModificationItem[] mods)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#bind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
	 */
	@Override
	public void bind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#bind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
	 */
	@Override
	public void bind(String name, Object obj, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#rebind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
	 */
	@Override
	public void rebind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#rebind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
	 */
	@Override
	public void rebind(String name, Object obj, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#createSubcontext(javax.naming.Name, javax.naming.directory.Attributes)
	 */
	@Override
	public DirContext createSubcontext(Name name, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#createSubcontext(java.lang.String, javax.naming.directory.Attributes)
	 */
	@Override
	public DirContext createSubcontext(String name, Attributes attrs)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchema(javax.naming.Name)
	 */
	@Override
	public DirContext getSchema(Name name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchema(java.lang.String)
	 */
	@Override
	public DirContext getSchema(String name) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(javax.naming.Name)
	 */
	@Override
	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(java.lang.String)
	 */
	@Override
	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, javax.naming.directory.Attributes, java.lang.String[])
	 */
	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, javax.naming.directory.Attributes, java.lang.String[])
	 */
	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, javax.naming.directory.Attributes)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, javax.naming.directory.Attributes)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	private class NamingEnumerationWrapper<E> implements NamingEnumeration<E>{
		/**
		 * @param e
		 */
		public NamingEnumerationWrapper(Iterator<E> e) {
			super();
			this.e = e;
		}

		private final Iterator<E> e;

		/* (non-Javadoc)
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return e.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public E nextElement() {
			return e.next();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#next()
		 */
		@Override
		public E next() throws NamingException {
			return e.next();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#hasMore()
		 */
		@Override
		public boolean hasMore() throws NamingException {
			return e.hasNext();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#close()
		 */
		@Override
		public void close() throws NamingException {
			
		}
	}
	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, java.lang.String, javax.naming.directory.SearchControls)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			SearchControls cons) throws NamingException {
		HashSet<SearchResult> res = new HashSet<SearchResult>();
		
		String target_name = "cn=testuser,ou=users,dc=fortissimo-openstack,dc=localnet";
		if( principal.equals(target_name) && password.equals("testpassword")){
			res.add(new SearchResult(target_name, null, new BasicAttributes()));
		}
		
		return new NamingEnumerationWrapper(res.iterator());
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, java.lang.String, javax.naming.directory.SearchControls)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			SearchControls cons) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
			Object[] filterArgs, SearchControls cons) throws NamingException {
		throw new NamingException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
	 */
	@Override
	public NamingEnumeration<SearchResult> search(String name,
			String filterExpr, Object[] filterArgs, SearchControls cons)
			throws NamingException {
		throw new NamingException("Not implemented");
		
	}


	
}