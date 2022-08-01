package uk.ac.ed.epcc.webapp.logging.log4j2;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.CloseableThreadContext;

import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
/** A {@link Filter} to add ThreadContext information for use by
 * Log4J logging.
 * 
 * This needs to be called after the {@link ErrorFilter} so needs to be
 * installed using an XML config file
 * 
 * @author Stephen Booth
 *
 */
@WebFilter(filterName="Log4JInfoFilter")
public class Log4JContextFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if( request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			try( CloseableThreadContext.Instance ctc = CloseableThreadContext.put("Address",request.getRemoteAddr())){
				HttpSession sess = req.getSession(false);
				if( sess != null ) {
					// Copy useful attributes from the session
					// Don't want to trigger the creation of an AppContext here so
					// on balance better to expose the tag strings directly
					for(String att : new String[] {AbstractSessionService.person_tag,AbstractSessionService.auth_time_tag, AbstractSessionService.auth_type_tag, ServletSessionService.WTMP_ID}) {
						Object o = sess.getAttribute(att);
						if( o != null) {
							ctc.put(att, o.toString());
						}
					}
				}
				chain.doFilter(request, response);
			}
		}else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		

	}

}
