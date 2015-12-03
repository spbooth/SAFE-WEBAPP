//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.PieDataset;

import uk.ac.ed.epcc.webapp.charts.GenericSetPlot;

/**
 * @author spb
 *
 */

public class JFreeSetPlot extends GenericSetPlot implements PieDataset {
	DatasetGroup group;
	HashSet<DatasetChangeListener> listeners = new HashSet<DatasetChangeListener>();
	/**
	 * @param i
	 */
	public JFreeSetPlot(int i) {
		super(i);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.KeyedValues#getIndex(java.lang.Comparable)
	 */
	public int getIndex(Comparable arg0) {
		if( labels != null ){
			for(int i=0 ;i< labels.length;i++){
				if( labels[i].equals(arg0)){
					return i;
				}
			}
			return -1;
		}
		return Integer.parseInt((String)arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.KeyedValues#getKey(int)
	 */
	public Comparable getKey(int arg0) {
		if( labels != null ){
			return labels[arg0];
		}
		return Integer.toString(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.KeyedValues#getKeys()
	 */
	public List getKeys() {
		LinkedList<String> list = new LinkedList<String>();
		if( labels != null){
			for(String s : labels){
				list.add(s);
			}
		}else{
			for(int i=0 ; i< getNumSets();i++){
				list.add(Integer.toString(i));
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.KeyedValues#getValue(java.lang.Comparable)
	 */
	public Number getValue(Comparable arg0) {
		return get(getIndex(arg0));
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.Values#getItemCount()
	 */
	public int getItemCount() {
		return getNumSets();
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.Values#getValue(int)
	 */
	public Number getValue(int arg0) {
		return get(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#addChangeListener(org.jfree.data.general.DatasetChangeListener)
	 */
	public void addChangeListener(DatasetChangeListener arg0) {
		listeners.add(arg0);
		
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#getGroup()
	 */
	public DatasetGroup getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#removeChangeListener(org.jfree.data.general.DatasetChangeListener)
	 */
	public void removeChangeListener(DatasetChangeListener arg0) {
		listeners.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#setGroup(org.jfree.data.general.DatasetGroup)
	 */
	public void setGroup(DatasetGroup arg0) {
		this.group=arg0;
		
	}

	
}