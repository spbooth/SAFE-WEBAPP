package uk.ac.ed.epcc.webapp.servlet;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
/** A {@link ServletService} that supports MultiPart form content via request methods introduced  in the Servlet3.0 spec
 * 
 * To support earlier servlet versions there is a version that uses the apache fileupload library instead
 * 
 * @see MultiPartServletService
 * @author spb
 *
 */
public class Servlet3MultiPartServletService extends DefaultServletService implements ServletService{
	private static final String REQUEST_METHOD_POST = "POST";
	private static final String CONTENT_TYPE_MULTIPART = "multipart/";
	public Servlet3MultiPartServletService(AppContext conn, ServletContext ctx,
			ServletRequest req, ServletResponse res) {
		super(conn, ctx, req, res);
	}
	
	@Override
	public Map<String,Object> makeParams(HttpServletRequest req)  {
		
		Map<String,Object> h = super.makeParams(req);
		if( isMultipartContent(req)){
			Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
			HttpServletRequest request = req;
			try {
				for(Part part : request.getParts()){
					PartStreamData sd = new PartStreamData(conn,part);
					String filename = sd.getName();
					if( filename == null ){
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						sd.write(stream);
						String encoding = request.getCharacterEncoding();
						if( encoding == null ){
							encoding = "UTF-8";
						}
						h.put(part.getName(),stream.toString(encoding));
					}else{
						h.put(part.getName(),sd);
					}
					
				}
			} catch (Exception e) {
				log.error("Error decoding multi-part form",e);
			}
			
		}
		return h;
	}
	/**
	 * @param req
	 * @return
	 */
	private boolean isMultipartContent(HttpServletRequest request) {
		 return REQUEST_METHOD_POST.equalsIgnoreCase(request.getMethod())
		            && request.getContentType() != null
		            && request.getContentType().toLowerCase().startsWith(CONTENT_TYPE_MULTIPART);
	}
	

}
