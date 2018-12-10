//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Servlet to generate a QRCode
 * This is not authenticated but may use the HttpSession 
 * @author Stephen Booth
 *
 */
@WebServlet(name="QRServlet", urlPatterns={"/QRCode/*"})
public class QRServlet extends WebappServlet {

	/**
	 * 
	 */
	private static final String QRCODE_ATTR_PREFIX = "QRCODE";
	/**
	 * 
	 */
	private static final String COUNTER_ATTR = "QrcodeCounter";

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.WebappServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn)
			throws ServletException, IOException {
		ServletService serv = conn.getService(ServletService.class);
		serv.noCache();
		
		int pos=0;
		String img = req.getParameter("img");
		if( img == null || img.isEmpty()) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid argument");
			return;
		}
		
		pos = Integer.parseInt(img);
		
		SessionService sess = conn.getService(SessionService.class);
		String text=(String) sess.getAttribute(QRCODE_ATTR_PREFIX+Integer.toString(pos));
		if( text == null || text.isEmpty()) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid argument");
			return;
		}
		res.setContentType("image/png");
		try {
			ServletOutputStream out = res.getOutputStream();
			createQRImage(out, text, 300, "png");
			out.close();
		} catch (WriterException e) {
			throw new ServletException(e);
		}
	}
	private static void createQRImage(OutputStream stream, String qrCodeText, int size, String fileType)
			throws WriterException, IOException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ImageIO.write(image, fileType, stream);
	}

	public static String getImageURL(AppContext conn, String image_name, String text) throws UnsupportedEncodingException {
		SessionService sess = conn.getService(SessionService.class);
		Integer counter = (Integer) sess.getAttribute(COUNTER_ATTR);
		if( counter == null ) {
			counter = Integer.valueOf(0);
		}
		sess.setAttribute(COUNTER_ATTR, Integer.valueOf(counter.intValue()+1));
		int pos=counter.intValue()%50; // max 50 images
		sess.setAttribute(QRCODE_ATTR_PREFIX+pos, text);
		return "/QRCode/"+image_name+"?img="+Integer.toString(pos);
	}
}
