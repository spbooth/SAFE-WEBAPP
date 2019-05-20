/** Classes for the dynamic creation of database tables. 

We use an abstract representation of
the table schema to make it easier port between database back-ends

Classes that require database tables can use this code to automatically create database tables 
if they don't already exist. Classes can also expose an interface for editing the table structure
(via the transition framework) by implementing {@link uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionProvider}
 * 
 */
package uk.ac.ed.epcc.webapp.jdbc.table;

