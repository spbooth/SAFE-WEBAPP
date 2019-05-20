/** This package implements the data layer that connects the SQL
database with the Java model. 

<P>As far as is reasonably possible we try to encapsulate any
dependency on the JDBC classes within this package. We also aim to
concentrate the appearance of SQL fragments to be only within this package as much
as is reasonably practical. Though it is very unlikely that we will
ever want to move away from using JDBC for data persistence this
encapsulation makes it easier for us to refactor the implementation. 
 Database tables and records are abstracted into the {@link uk.ac.ed.epcc.webapp.model.data.Repository} and
{@link uk.ac.ed.epcc.webapp.model.data.Repository.Record} classes Select conditions are abstracted into the
classes and interfaces in the <B>uk.ac.ed.epcc.webapp.jdbc.filter</B>
package. 
</P>
<P>The data model is that every database table has a corresponding
Java class. Instances of these classes correspond to rows in the
database. 
</P>
<P>At lowest level of the API are the <code>Repository</code> and
<code>Repository.Record</code> classes. These are final classes that represent a
database table and a table record respectively. This package also
contains the abstract {@link uk.ac.ed.epcc.webapp.model.data.DataObject} and {@link uk.ac.ed.epcc.webapp.model.data.DataObjectFactory} classes. These
classes are sub-classed to produce the table specific model classes. 
</P>
<P>There is a very close parallel between <code>Repository</code>/<code>Record</code> and
<code>DataObjectFactory</code>/<code>DataObject</code>. The first are Database facing
final classes providing an abstraction layer above JDBC. The second are application facing abstract 
classes that are subclassed to provide customised classes for individual
database tables, containing internal references to the the
appropriate <code>Repository</code>/<code>Record</code> objects. This two level approach allows
a great deal of flexibility to refactor the domain objects without
requiring a corresponding reformat of the database schema. It also
allows a greater separation of concerns. 
</P>
<P>The <code>DataObjectFactory</code> class implements methods for extracting sets
of multiple database entries. We use a Factory class rather than
static methods because this allows us to inherit common factory code
and add additional behaviour suitable to particular domain objects. 
</P>
<P>In many cases the factory and the target class are independent
classes (we often keep one as a static inner class of the
other to keep them associated). Where the Objects are more likely to
be implemented as Sets of objects rather than individuals we can make
the <code>DataObject</code> an inner class (or pseudo-inner class) of the Factory see {@link uk.ac.ed.epcc.webapp.model.data.LinkManager} and
{@link uk.ac.ed.epcc.webapp.model.history.HistoryFactory}. 
</P>
<P>The classes in <B>uk.ac.ed.epcc.model.data.forms</B> provide an
interface for building user interface forms. These can be used to
easily construct interface forms. Though most modifications to model
state come via custom application logic, two very common operations
are to  create and to modify individual domain objects. This is
particularly important as part of the application management
functions. The {@link uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory} class contains code to produce
creation and update forms for a <code>DataObject</code> based on
the SQL schema. This class is sub-classed to produce {@link uk.ac.ed.epcc.webapp.model.data.forms.Creator} and {@link uk.ac.ed.epcc.webapp.model.data.forms.Updater}
which implements the default create/update logic. Individual model classes may sub-class these to customise the
behaviour. We also want to be able to set class-specific defaults for both the create and update forms. This is implemented by 
having the default methods in <code>DataObjectFormFactory</code> call methods in the <code>DataObjectFactory</code>.
Originally this functionality was implemented directly in the <code>DataObjectFactory</code> but the forms code was considered to be
a sufficiently distinct responsibility to merit its own class.
</P>
<P>There are many different frameworks available for doing database
persistence of Java objects. This particular scheme is database
centric in that much of the behaviour of the object is driven by the
Database schema rather than in the source of the object. For example
if an additional field is added to a table additional fields will
appear in the form used to create that object. A text field that can
be null in the database can be left blank in a form.  The API of
<code>Repository.Record</code> allows <code>DataObjects</code> to access a database field if it
exists and to pick up a default value if it does not. 
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

