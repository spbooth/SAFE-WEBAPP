package uk.ac.ed.epcc.webapp.forms.transition;


/** Visitor for different (incompatible) variants of {@link TransitionFactory}
 * 
 * @author spb
 *
 * @param <R>
 * @param <T>
 * @param <K>
 */
public interface TransitionFactoryVisitor<R,T,K> {
  public  R visitTransitionProvider(TransitionProvider<K,T> prov);
  
  public  R visitPathTransitionProvider(PathTransitionProvider<K,T> prov);
}
