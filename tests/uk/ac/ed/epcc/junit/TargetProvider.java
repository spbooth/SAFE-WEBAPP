/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;
/** Interface to be implemented by a Test case that
 * wishes to support an interface test.
 * 
 * This interface is needed by {@link ExampleInterfaceTest} to retreive the
 * task under test but is sufficiently general that it could also be uses
 * for unrelated interface tests.
 * 
 * @author spb
 *
 * @param <T> Type of interface to be tested
 */
public interface TargetProvider<T> {
  T getTarget();
}
