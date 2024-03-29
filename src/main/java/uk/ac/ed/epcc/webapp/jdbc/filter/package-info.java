//| Copyright - The University of Edinburgh 2015                            |
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
/**
Filter interface definitions which are ways
of selecting a set of objects/records from a Factory/table. 
All filters inherit from {@link uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter}.
<P>
There are a number of
different filter types corresponding to different ways of selecting
objects. The majority are concerned with filtering at the SQL level
though the {@link uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter} filters objects after they have
been retrieved from the database. 
</P>
<P>The basic model of a SQL query we use is: 
</P>
<PRE><B>SELECT</B> <EM>target</EM> <B>FROM</B> <EM>table</EM> 
     <EM>[ <B>JOIN</B> table <B>ON</B> join-condition ]*</EM>
     <B>WHERE</B>
     <EM> [ select-conditions ]+ </EM>
     <EM> [ order-by ]? </EM></PRE><P>
The various clauses (most importantly the select conditions) are
represented by filters implementing one of the various interfaces.
Normally we only need very simple filters selecting on a single table
field. These can then be composed into more complex expressions
using the composition classes in this package. 
</P>
<P>Filters that operate purely in SQL should implement {@link uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter}
to indicate this fact. Only <CODE>SQLFilter</CODE>s can reliably be
used with more advanced SQL features like LIMIT clauses or COUNT. The
interfaces <CODE>SQLFilter</CODE> and <CODE>AcceptFilter</CODE> have
incompatible method signatures to prevent any class implementing them
both.</P>
<P>This package is essentially stand-alone compared to the rest of
the framework as it only interacts via interfaces defined in this
package. 
</P>
<P> The <b>visitor</b> pattern is used by filters. Each class can only accept one 
method from the visitor class so usually filters can only implement one of the interfaces.
The exception to this is the composition filters that implement multiple interfaces and have their
own explicit accept method indicating that they need special treatment. These are defined
as representing the intersection of the behaviours of their different interfaces.
The basic filters
should only implement one of the interfaces, Where possible the use of 
<CODE>AcceptFilter</CODE> should be avoided. Note that OR combinations of SQL and accept-filters are particularly expensive as
the SQL branches of the combined query need to be converted to accept filters that will require an additional query per record
to evaluate. 
</P>
<P>All filters are generic with the type
they select and implement {@link uk.ac.ed.epcc.webapp.Targetted} so that these
checks can also be performed at run-time. Though only 
<CODE>AcceptFilter</CODE> has other methods whose signature uses the target
type.
</P>
*/
package uk.ac.ed.epcc.webapp.jdbc.filter;