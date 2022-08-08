package uk.ac.ed.epcc.webapp.mock;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class MockFilterConfig implements FilterConfig {

	public MockFilterConfig(ServletContext ctx, String name) {
		super();
		this.ctx = ctx;
		this.name = name;
	}

	public final ServletContext ctx;
	public final String name;
	@Override
	public String getFilterName() {
		return name;
	}

	@Override
	public ServletContext getServletContext() {
		return ctx;
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public String nextElement() {
				return null;
			}
		};
	}

}
