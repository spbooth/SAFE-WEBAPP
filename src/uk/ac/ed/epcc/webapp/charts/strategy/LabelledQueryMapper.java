// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;



/**
 * LabeledQueryMapper Variant of QueryMapper where the QueryMapper also specifies
 * the labels for the sets. This is intended for situations where the
 * LabeledTransform builds a list of labels based on the data-stream passed
 * to it and is then queried at the end to in order to add the observed
 * labels to the plot.
 * 
 * @author spb
 * 
 * @param <F> type of object being mapped
 */
public interface LabelledQueryMapper<F> extends QueryMapper<F>,Labelled {
	
}