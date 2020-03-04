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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** {@link FileInput} for image files
 * @author Stephen Booth
 *
 */
public class ImageInput extends FileInput implements ItemInput<StreamData,BufferedImage> {

	/**
	 * 
	 */
	public ImageInput() {
		super();
		setAccept("image/*");
		addValidator(new FieldValidator<StreamData>() {
			
			@Override
			public void validate(StreamData data) throws FieldException {
				try {
					BufferedImage image = getBufferedImage(data);
					if( image == null) {
						throw new ValidateException("Bad image");
					}
					if(max_x >0 && image.getWidth()>max_x) {
						throw new ValidateException("Image too wide max="+max_x);
					}
					if(max_y >0 && image.getHeight()>max_y) {
						throw new ValidateException("Image too high max="+max_y);
					}
				} catch (IOException e) {
					throw new ValidateException(e);
				}
				
			}
		});
	}

	private int max_x=0;
	private int max_y=0;
	

	

	/**
	 * @param data
	 * @return {@link BufferedImage}
	 * @throws IOException
	 */
	public BufferedImage getBufferedImage(StreamData data) throws IOException {
		return ImageIO.read(data.getInputStream());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
	 */
	public void setBufferedImage(BufferedImage item) throws DataFault, TypeError {
		if( item == null) {
			setValue(null);
		}
		
		setValue(new ByteArrayMimeStreamData(((DataBufferByte)item.getData().getDataBuffer()).getData()));
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#getItem()
	 */
	@Override
	public BufferedImage getItembyValue(StreamData data) {
		try {
			return getBufferedImage(data);
		} catch (IOException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
	 */
	@Override
	public void setItem(BufferedImage item) {
		try {
			setBufferedImage(item);
		} catch (DataFault | TypeError e) {
			setValue(null);
		}
		
	}

	/**
	 * @return the max_x
	 */
	public int getMaxX() {
		return max_x;
	}

	/**
	 * @param max_x the max_x to set
	 */
	public void setMaxX(int max_x) {
		this.max_x = max_x;
	}

	/**
	 * @return the max_y
	 */
	public int getMaxY() {
		return max_y;
	}

	/**
	 * @param max_y the max_y to set
	 */
	public void setMaxY(int max_y) {
		this.max_y = max_y;
	}
	


}
