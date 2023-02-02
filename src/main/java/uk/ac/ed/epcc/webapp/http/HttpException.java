//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.http;

import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/**
 * @author spb
 *
 */
public class HttpException extends Exception {
    private int error_code;
    private MimeStreamData content=null;
    
	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	/**
	 * 
	 */
	public HttpException() {
	
	}

	/**
	 * @param message
	 */
	public HttpException(String message) {
		super(message);
		
	}

	/**
	 * @param cause
	 */
	public HttpException(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpException(String message, Throwable cause) {
		super(message, cause);
		if( cause instanceof HttpException) {
			HttpException h = (HttpException) cause;
			error_code = h.getError_code();
			content = h.getMimeContent();
		}
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public String getContent() {
		if( content == null) {
			return null;
		}
		return content.toString();
	}
	public MimeStreamData getMimeContent() {
		return content;
	}

	public void setContent(MimeStreamData content) {
		this.content = content;
	}

}
