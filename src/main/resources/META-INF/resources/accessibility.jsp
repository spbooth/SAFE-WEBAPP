<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<%
	String page_title = "Accessibility statement for "+service_name+" "+website_name;
%>
<%@ include file="std_header.jsf"%>
<div class="block">
<h1><%=page_title %></h1>
<p>This accessibility statement applies to this website <wb:content bundle="accessibility" message="location" optional="true"/>
</p>
<p> This website is run by <wb:content bundle="accessibility" message="run_by"/>. We want as many people as possible to be able to use this website. For example, that means you should be able to:
<ul>
<li>Increase font sizes, to make text more readable.</li>
<li>Navigate most of the website using just a keyboard.</li>
<li>Listen to most of the website using a screen reader.</li>
</ul>
<h2>How accessible this website is</h2>
<p>We know some parts of this website are not fully accessible:</p>
<ul>
<li> Some form elements (specifically complex elements that utilise multiple html inputs) may work poorly with screen readers as only the first input will be referenced by the form label.</li>
<li>Much of the content is dynamically generated, Dynamically generated content may be overly wide and hard to read at high magnification. Dynamically generated graphs will
not have useful alternative text (though we try to ensure that the same information is also available in tabular form).</li>
</ul>
<h2>Feedback and contact information</h2>
If you need information on this website or additional assistance contact: <wb:content bundle="accessibility" message="contact_details"/>

<h2>Reporting accessibility problems with this website</h2>
<p>We're always looking to improve the accessibility of this website. If you find any problems not listed on this page or think we're not meeting accessibility requirements, 
contact: <wb:content bundle="accessibility" message="contact_details"/>
</p>
<h2>Enforcement procedure</h2>

The Equality and Human Rights Commission (EHRC) is responsible for enforcing the Public Sector Bodies (Websites and Mobile Applications) (No. 2) Accessibility Regulations 2018 (the 'accessibility regulations'). If you're not happy with how we respond to your complaint,
 contact the Equality Advisory and Support Service (EASS)
<a href="https://www.equalityadvisoryservice.com/">https://www.equalityadvisoryservice.com/</a>.

<h2>Technical information about this website's accessibility</h2>
<p><wb:content bundle="accessibility" message="organisation" /> is committed to making its website accessible, in accordance with the Public Sector Bodies (Websites and Mobile Applications) (No. 2) Accessibility Regulations 2018.</p>
<h3>Compliance status</h3>
<p>This website is partially compliant with the Web Content Accessibility Guidelines version 2.1 AA standard, due to 
the non-compliances listed below.
<h4>Non-accessible content</h4>
<ul>
<li> Some form elements (specifically complex elements that utilise multiple html inputs) may work poorly with screen readers as only the first input will be referenced by the form label.</li>
<li>Much of the content is dynamically generated, Dynamically generated content may be overly wide and hard to read at high magnification. Dynamically generated graphs will
not have useful alternative text (though we try to ensure that the same information is also available in tabular form).</li>
</ul>

<h2>Preparation of this accessibility statement</h2>
</p>
This statement was prepared on <wb:content bundle="accessibility" message="statement_date"/>. It was last reviewed on <wb:content bundle="accessibility" message="review_date"/>.

This website was last tested on <wb:content bundle="accessibility" message="tested_date"/>. <wb:content bundle="accessibility" message="tested_by" optional="true"/>
<wb:content bundle="accessibility" message="test_methodology" optional="true"/>
</p>
</div>
<%@ include file="std_footer.jsf"%>
