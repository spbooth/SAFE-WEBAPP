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
/** This package contains generic form processing code.
 *  
<P>The initial
implementation is aimed at HTML forms but it is also possible to
generate GUI forms using the same basic structure. 
</P>
<P>A standard form consists of an ordered list of named fields. For each field
the form consists of a text label (defaulting to the name of the
field) and a Input. Inputs can be text-boxes, pull-downs,
radio-buttons etc. A form also has a set of action/submit buttons. 
</P>
<P>Note that Inputs may be composite. For example we may want to
build a Date Input out of separate pull-down menus for year, month
and day. 
</P>
<P>When a form is submitted each of the inputs is parsed and the
overall submission validated. If there are any errors in the form
input the form is re-presented marked up with errors. Errors can be
presented in the following ways: 
</P>
<UL>
	<LI><P>Non optional fields can be marked
	as <EM>missing</EM> 
	</P></LI>
	<LI><P>Fields can be annotated with field
	specific error messages 
	</P></LI>
	<LI><P>A general error message can be presented. 
	</P></LI>
</UL>
<P>Each field in a validated form has a corresponding value (Object).
The Form can return a Map of values for use by the program logic
after the form has been verified. Alternatively if a form has action
buttons defined then these buttons are tied to a {@link uk.ac.ed.epcc.webapp.forms.action.FormAction}
class that acts like a method call on the form.</P>
<P>The Form superclass is independent of how the form is edited. This
allows us to use generic code to build Forms of different types. The
Form validation code is also the same for all form types. 
</P>
<P>There are 2 levels of validation. Each field/Input can validate
its own inputs independently and generate field specific errors. Every Input class has its own
built in validation code and this can be augmented by adding a {@link uk.ac.ed.epcc.webapp.validation.FieldValidator} to the corresponding field of the form.
The overall form can also be validated and generate a generic error. This
is done by adding a {@link uk.ac.ed.epcc.webapp.forms.FormValidator} object to the form. These can be used to validate constraints that affect multiple fields for example if
one value should be less than another.
</P>
<P>The activation pattern for HTML and GUI forms are significantly
different. In a GUI there is a single Form instance each field can be
validated as it is edited. Each edit invokes an appropriate
call-back. In HTML form generation and parsing are separated into two
separate phases on different instances and progress in batch mode.
The Form editing methods are therefore specific to the subclass or handled using a visitor pattern. The
flow of control is as follows: 
</P>
<UL>
	<LI><P STYLE="margin-bottom: 0cm">The enclosing Servlet/GUI
	environment code creates a form of the correct sub-class. 
	</P></LI>
	<LI><P STYLE="margin-bottom: 0cm">This blank form is passed to a
	method on some model class to build the form 
	</P></LI>
	<LI><P STYLE="margin-bottom: 0cm">The enclosing environment code
	calls the form edit methods on the Form. 
	</P></LI>
	<LI><P>The validated form is passed to an action method to perform the necessary action. This may be a method on a model class 
	but it is more usual to embed a {@link uk.ac.ed.epcc.webapp.forms.action.FormAction} class within the form itself that contains references
	to the model classes that need to be modified.
	</P></LI>
</UL>
<p> To improve the user interface inputs that correspond to HTML5 input types can implement
{@link uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input} to take advantage of browser level field validation.
In general we prefer to rely on html5 features rather than introducing additional javascript. In principal we could allow inputs and validators
to add additional javascript though this would require each validation rule to be implemented and maintained in two different languages.
I

</p>
<P>The Input classes are kept generic to all different types of Form.
The Form class has to implement the different types of edit operation
depending on the kind of selector. This allows us to use inheritance
between different types of Input. If we sub-classed Inputs by Form
type each new Input type would need to be sub-classed for every Form
type. Where we subclass an Input by type there is a reasonable chance
that the inherited edit code will be sufficient and not need
re-implementing For example most Inputs can share text-box edit code
from a common superclass but can be quite different when it comes to
parse and validation. 
</P>
<p>
There is a very powerful framework for implementing application logic based on the
interfaces {@link uk.ac.ed.epcc.webapp.forms.transition.Transition} and
{@link uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory}. These define general form-based methods that can be called on an object.
A <code>Transition</code> defines the operation and associated form and the <code>TransitionFactory</code> provides the logic for
identifying the target and access control. This allows large amounts of functionality to be implemented independently of the jsp/servlet framework.
</p>


**/
package uk.ac.ed.epcc.webapp.forms;