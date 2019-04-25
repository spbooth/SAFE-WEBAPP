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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;



/**
 * LabeledTransform Variant of Transform where the Transform also specifies
 * the labels for the sets. This is intended for situations where the
 * LabeledTransform builds a list of labels based on the data-stream passed
 * to it and is then queried at the end to in order to add the observed
 * labels to the plot.
 * 
 * @author spb
 * @param <T> type of object being mapped
 * 
 */
public interface LabelledSetRangeMapper<T> extends SetRangeMapper<T>,Labelled {
	
}