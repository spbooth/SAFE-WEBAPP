package uk.ac.ed.epcc.webapp.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;

public class ServiceInitTag extends TagSupport implements Tag{

	@Override
	public int doStartTag() throws JspException {
		try {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		HttpServletResponse response = (HttpServletResponse) page.getResponse();
		AppContext conn = ErrorFilter.retrieveAppContext(request, response);
		String web_path = request.getContextPath();
		// path of the diretory above the css dir
		String template_path = "";	
		String service_name  = "";
		String website_name = "";
		// error-pages do NOT go throught he filter first so we may have a null context here
		if( conn != null ){
			template_path = request.getContextPath()+conn.getInitParameter("template.path","");	
			service_name = conn.getInitParameter("service.name","");
			website_name = conn.getInitParameter("service.website-name","");
			pageContext.setAttribute("conn", conn);
			
		}
		pageContext.setAttribute("template_path", template_path);
		pageContext.setAttribute("service_name", service_name);
		pageContext.setAttribute("website_name", website_name);
		pageContext.setAttribute("web_path", web_path);
		return SKIP_BODY;
		}catch(Exception e) {
			throw new JspException("Error getting AppContext", e);
		}
	}

}
