// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/**
 * HTMLForm is a class representing a form in HTML
 * This type of form is for the MVC pattern where the form page is seperate from
 * the processing Servlet. If an error occurs then the info is stored in the
 * request and the posting page is invoked.
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: HTMLForm.java,v 1.15 2015/11/16 17:20:32 spb Exp $")

public class HTMLForm extends BaseHTMLForm {
	private static final String FORM_URL = "form_url";

	private static final String MISSING_FIELDS_TAG = "MissingFields";

	private static final String ERRORS_TAG = "Errors";



	public HTMLForm(AppContext c) {
		super(c);
	}

	

	

	

	/**
	 * emit a HTML edit form based on the current internal state of the form any
	 * internal inconsistencies of the state are shown as errors. used for
	 * initial show of an update form
	 * 
	 * @return String HTML form fragment
	 */
	public String getHtmlFieldTable() {
		return getHtmlFieldTable(new HtmlBuilder()).toString();
	}
	/**
	 * emit a HTML edit form based on the current internal state of the form any
	 * internal inconsistencies of the state are shown as errors. used for
	 * initial show of an update form
	 * 
	 * @param result to modify
	 * @return HtmlPrinter form fragment
	 */
	public <X extends HtmlBuilder> X getHtmlFieldTable(X result){
		Collection<String> missing = new HashSet<String>();
		Map<String,String> errors = new HashMap<String,String>();
		validate(missing, errors);
		result.setMissingFields(missing);
		result.setErrors(errors);
		result.setPostParams(null);
		result.addFormTable(getContext(), this);
		return result;
	}


	
	/**
	 * emit the HTML edit form based on the current request if there are no
	 * current errors then the internal form state is shown but unvalidated
	 * (assume these are the default values of a creation form).
	 * 
	 * For an update form where the initial state should be validated
	 * use {@link #getHtmlFieldTable(HtmlPrinter)} if there are no errors in the request
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @return String HTML fragment contain the HTML form
	 */
	public String getHtmlFieldTable(HttpServletRequest req) {
		return getHtmlFieldTableFromRequest(req,true);
	}
	public String getHtmlFieldTableFromRequest(HttpServletRequest req,boolean use_required) {
		HtmlBuilder hb = new HtmlBuilder();
		hb.setUseRequired(use_required);
		return getHtmlFieldTable(hb, req).toString();
	}
	/**
	 * emit the HTML edit form based on the current request if there are no
	 * current errors then the internal form state is shown but unvalidated
	 * (assume these are the default values of a creation form).
	 * 
	 * For an update form where the initial state should be validated
	 * use {@link #getHtmlFieldTable(HtmlPrinter)} if there are no errors in the request
	 * 
	 * @praram result HtmlPrinter to add report to
	 * @param req
	 *            HttpServletRequest
	 * @return HtmlPrinter
	 */
	public <X extends HtmlBuilder> X getHtmlFieldTable(X result,HttpServletRequest req){
		Collection<String> missing_fields = getMissing(req);
		Map<String,String> errors = getErrors(req);
		AppContext ctx = getContext();
		Map<String,Object> params;
		ServletService serv = ctx.getService(ServletService.class);
		if( serv != null){
			params = serv.getParams();
		}else{
			params = new HashMap<String,Object>();
		}
		return getHtmlFieldTable(result,missing_fields, errors, params);
	}

	



	/**
	 * Checks the requested action. IF it needs to be confirmed then return the
	 * action string otherwise return null
	 * 
	 * @param params
	 *            Map of the form parameters
	 * @return String, the action string or null if confirmation not needed
	 * @throws ActionException
	 */
	public String mustConfirm(Map params) throws ActionException {

		for (Iterator<String> it = getActionNames(); it.hasNext();) {
			String name =  it.next();
			if (params.get(name) != null) {
				return mustConfirm(name);
			}
		}
		String defname=getSingleActionName();
		if( defname != null){
			// bug in IE8.0 action name not sent with
			// single text input.
			return mustConfirm(defname);
		}
		throw new ActionException("No matching action found");
	}

	
	/**
	 * parse and validate a post request.
	 * 
	 * @param req
	 * @return boolean true if all ok
	 */
	public boolean parsePost(HttpServletRequest req) {
		AppContext c = getContext();
		Map<String,Object> params;
		params = c.getService(ServletService.class).getParams();
		if( params.get(FORM_URL) == null && params.get("direct") == null){
			// we won't be able to return to forms to show errors
			// not a problem if parse succeeds but report it so it can be fixed
			// create an Exception to generate a stack trace
			// The parameter direct can be used to supress this check where we
			// are bypassing a form stage (e.g pre selecting the object in an update form
			c.error(new Exception("No form_url in parameters"),  "No form_url in parameters");
		}
		boolean ok = parsePost(getErrors(req), params,false);
		if (!ok) {
			c.getService(LoggerService.class).getLogger(getClass()).debug("internal parse failed");
		}
		ok = ok && validate(getMissing(req), getErrors(req));
		if (!ok) {
			c.getService(LoggerService.class).getLogger(getClass()).debug("internal validate failed");
		}
		return ok;
	}

   
	
	public static void addGeneralError(String text, HttpServletRequest request) {
		Map<String,String> errors = getErrors(request);
		errors.put(MapForm.GENERAL_ERROR, text);
	}

	public static void clearErrors(HttpServletRequest request) {
		request.removeAttribute(ERRORS_TAG);
		request.removeAttribute(MISSING_FIELDS_TAG);
	}

	/**
	 * redirect to the submitting form if errors occur in the form We expect the
	 * URL of the submitting form to be avaliable as a "form_url" parameter.from
	 * the form. This avoid the Servlets needing to know which form submitted to
	 * them and keeps presentation and logic seperate. The actuall error
	 * information is cached in the request object by the Form Code
	 * 
	 * @param ctx
	 *            AppContext
	 * @param req
	 *            Request
	 * @param res
	 *            Response
	 */
	public static void doFormError(AppContext ctx,
			HttpServletRequest req, HttpServletResponse res) {

		String form_url = (String) ctx.getService(ServletService.class).getParams().get(FORM_URL);
		if (form_url == null) {
			// lets get the stacktrace from where this occured
			throw new ConsistencyError("Return URL of form not specified");

		}
		try {
			ctx.getService(ServletService.class).forward(form_url);
		} catch (Exception e) {
			ctx.error(e, "Exception dispatching form error");

		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String,String> getErrors(HttpServletRequest request) {
		Map<String,String> errors = null;
		if( request != null ){
			errors = (Map<String,String>) request.getAttribute(ERRORS_TAG);
		}
		if (errors == null) {
			errors = new HashMap<String,String>();
			if( request != null ){
			request.setAttribute(ERRORS_TAG, errors);
			}
		}
		return errors;
	}

	/**
	 * Get the url to use for error reporting. If it already exists in the
	 * parameters then use that (we have already returned errors). Otherwise
	 * assume this is the first show of the form and take it from the request.
	 * 
	 * @param req
	 * @return String
	 */
	public static String getFormURL(HttpServletRequest req) {
		String result = req.getParameter(FORM_URL);
		if (result == null) {
			result = req.getServletPath();
		}
		return result;
	}

	public static String getGeneralError(HttpServletRequest request) {
		Map<String,String> errors = getErrors(request);
		return errors.get(MapForm.GENERAL_ERROR);
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> getMissing(HttpServletRequest request) {
		Collection<String> missing_fields = null;
		if( request != null ){
			missing_fields = (Collection) request.getAttribute(MISSING_FIELDS_TAG);
		}
		if (missing_fields == null) {
			missing_fields = new HashSet<String>();
			if( request != null){
			  request.setAttribute(MISSING_FIELDS_TAG, missing_fields);
			}
		}
		return missing_fields;
	}

	public static boolean hasError(HttpServletRequest request) {
		Map<String,String> errors = getErrors(request);
		Collection<String> missing = getMissing(request);
		return (!errors.isEmpty()) || (!missing.isEmpty());
	}
}