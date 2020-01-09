//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.servlet.session.token;

import javax.servlet.http.HttpServletResponse;

/** Standard Bearer token error codes
 * @author Stephen Booth
 *
 */
public enum ErrorCodes {
	invalid_request {
		@Override
		public int getCode() {
			return HttpServletResponse.SC_BAD_REQUEST;
		}
	},
	invalid_token {
		@Override
		public int getCode() {
			return HttpServletResponse.SC_UNAUTHORIZED;
		}
	},
	insufficient_scope {
		@Override
		public int getCode() {
			return HttpServletResponse.SC_FORBIDDEN;
		}
	};
	
	public abstract int getCode();
}
