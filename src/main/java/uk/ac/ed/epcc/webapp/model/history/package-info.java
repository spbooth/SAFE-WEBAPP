/** Stores time history information about the state of model classes.
<p>
The <code>HistoryFactory</code> class is abstract and implements the general functionality. 
Subclasses of <code>HistoryFactory</code> 
track the state of particular model objects (the <b>peer</b> object).
</p>
<p>
A single record is used to store the entire period that a peer object remains unchanged so 
that objects that change infrequently can be tracked efficiently. It is possible to add a history factory to a 
<code>DataObjectFactory</code> so that the history is automatically updated when the <code>comit</code> method is called
or history can be tracked by making explicit calls to the <code>update</code> method on the <code>HistoryFactory</code>
 </p>
 <p>
 Any database field in the peer object is automatically tracked if a field of the same name exists in the history table.
 </p>
 <p>
 As History objects are only manipulated via the factory (they are logically part of a series rather than 
 making sense on their own) then we make the History object an inner class of HistoryFactory. This allows us to
 avoid some duplication of methods
 </p>
 <p>
 It is really difficult to make LinkHistory generic in any meaningful way, (its actually very 
 difficult to get it to compile). We therfore do not use generic classes for History. This is very little loss as 
 History typically works with the repository not the API of the class.
</p>
 * 
 */
package uk.ac.ed.epcc.webapp.model.history;

