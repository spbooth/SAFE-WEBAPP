//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public abstract class AbstractStreamData implements StreamData {

	public final void read(InputStream in) throws DataFault, IOException {
		OutputStream out = getOutputStream();
		if( out == null){
			throw new DataFault("Cannot get output stream");
		}
		int i;
		
			while((i=in.read())!=-1){
				out.write(i);
			}
			in.close();
			out.close();
		
	
	}

	public final void write(OutputStream out) throws DataFault, IOException {
		InputStream in = getInputStream();
		if( in == null){
			throw new DataFault("Cannot get input stream");
		}
		int i;
		
			while((i=in.read())!=-1){
				out.write(i);
			}
			in.close();
			out.close();
		
		
	}

	
}