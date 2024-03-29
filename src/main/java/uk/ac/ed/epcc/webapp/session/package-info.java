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

/** The package uk.ac.ed.epcc.webapp.session contains classes related to authentication, session management and logins.
 * 
 * <p>
 * The most important of these is the {@link uk.ac.ed.epcc.webapp.session.SessionService} which is an {@link uk.ac.ed.epcc.webapp.AppContextService} that
 * represents all known information about the current logged in user. This is usually a sub-class of {@link uk.ac.ed.epcc.webapp.session.AbstractSessionService}
 * </p>
 * <p>
 * Known users of the system are usually recorded using some sub-class of the {@link uk.ac.ed.epcc.webapp.session.AppUser} class
 * and its associated factory {@link uk.ac.ed.epcc.webapp.session.AppUserFactory}.
 * In most cases access control logic should use the {@link uk.ac.ed.epcc.webapp.session.SessionService} rather than an instance of the {@link uk.ac.ed.epcc.webapp.session.AppUser}.
 * This is because the {@link uk.ac.ed.epcc.webapp.session.SessionService} also has access to session specific state and information from the container. In principle it
 * is possible to not record users in the database at all and only use container authentication.
 * </p>
 * <p> Typically there is a unique name that can be used to identify a system user.
 * Frequently there is more than one such name and the exact configuration depends on local requirements. These unique names are therefore 
 * implemented as {@link uk.ac.ed.epcc.webapp.session.AppUserNameFinder} {@link uk.ac.ed.epcc.webapp.model.data.Composite}s. These names play an important part in authentication.
 * When using container level authentication the identity provided by the container is mapped to a database record by name.
 * Container level authentication can be set globally or multiple authentications mechanisms can be supported via explicit authentication servlets at different URLs.
 * We can also support password based authentication if the {@link uk.ac.ed.epcc.webapp.session.AppUserFactory} contains a {@link uk.ac.ed.epcc.webapp.model.data.Composite} registered as a {@link uk.ac.ed.epcc.webapp.session.PasswordAuthComposite}.
 * Password based authentication can be by an explicit login form or using basic-auth.  
 * </p>
 * <p>
 * There are two key abstractions for the permissions model:
 * <ul>
 * <li><b>Roles</b> These represent capabilities belonging to users. They are represented as strings. Role-mappings can be defined in configuration parameters so that
 * a small number of meta-roles can be assigned to users which are then mapped to a large number of specific roles used to access different functions of the application.</li>
 * <li><b>Relationships</b> These are like roles but apply to specific model objects. For example a user might have the <em>manager</em> relationship with one project but not with another.</li>
 * </ul>
 * <p> 
 * Roles can be set to be <em>togglable</em>. In this case the user with the role can turn it on and off during their session. 
 * The role mechanism is also a key extension point and can be customised by creating a new {@link uk.ac.ed.epcc.webapp.session.SessionService} class. Though role settings are normally stored in the database, roles defined in the servlet container can be imported 
 * into the application and made available. In addition model specific information (like project membership) can be made available as roles. 
 * </p>
 * Relationships are accessed as filters. The {@link uk.ac.ed.epcc.webapp.session.SessionService} can generate a filter for all objects of a certain type that the current user has a specified relationship with. Similarly it can generate a filter for people that have
 * a relationship with a specified object. Most of this information comes from model objects and is frequently a combination of data from different sources.
 * classes can contribute to relationships by implementing {@link uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider}. 
 * 
 * 
 */
package uk.ac.ed.epcc.webapp.session;
