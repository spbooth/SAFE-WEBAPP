package uk.ac.ed.epcc.webapp.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.FileItemStreamData;
/** A {@link ServletService} that supports MultiPart form content via tha apache fileupload
 * libraries. It will therefore work in a servlet 2 container.
 * For servlet 3 there are equivalent methods in httprequest and we can remove this dependency by using that
 * @see Servlet3MultiPartServletService
 * @see FileUploadWebappContextListener
 * 
 * @author spb
 *
 */
public class MultiPartServletService extends DefaultServletService implements ServletService{

	public MultiPartServletService(AppContext conn, ServletContext ctx,
			ServletRequest req, ServletResponse res) {
		super(conn, ctx, req, res);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("deprecation")
	@Override
	public Map<String,Object> makeParams(HttpServletRequest req)  {
		
		Map<String,Object> h = super.makeParams(req);
		if( FileUploadBase.isMultipartContent(req)){
			FileItemFactory factory = new DiskFileItemFactory();
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			log.debug("Processing multipart form");
			//		 Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items;
			try {
				items = upload.parseRequest(req);
				String characterEncoding = req.getCharacterEncoding();
				for( Iterator it=items.iterator(); it.hasNext();){
					FileItem i = (FileItem) it.next();
					String name = i.getFieldName();
			
					if( i.isFormField()){
						log.debug("add parameter <"+name+":"+i.getString()+">");
						try {
							if( characterEncoding == null ){
								h.put(name, i.getString());
							}else{
								h.put(name, i.getString(characterEncoding));
							}
						} catch (UnsupportedEncodingException e) {
							h.put(name, i.getString());
						}
					}else{
						if( i.getSize() > 0 ){
						  log.debug("add file "+name+":"+i.getName()+" length:"+i.getSize()+" type:"+i.getContentType());
						  h.put(name, new FileItemStreamData(conn,i));
						}
					}
				}
			} catch (FileUploadException e) {
				throw new ConsistencyError("Error parsing multipart form",e);
			}
		}
		return h;
	}


}
