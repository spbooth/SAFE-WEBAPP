package uk.ac.ed.epcc.webapp.session;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;

/** class holding password policy/complexity rules
 * 
 * @author Stephen Booth
 *
 */
public class PasswordPolicy extends AbstractContexed implements SingleLineFieldValidator{
	private static final Feature CHECK_COMPLEXITY = new Feature("password.check_complexity",true,"Perform complexity check on user generated passwords");

	public PasswordPolicy(AppContext conn) {
		super(conn);
	}
	public int minPasswordLength() {
		return getContext().getIntegerParameter("password.min_length", 8);
	}
	public int minDiffChars() {
		int min = getContext().getIntegerParameter("password.min_diff_char", 6);
		if( min > minPasswordLength()){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		return min;
	}
	public int minDigits() {
		int min = getContext().getIntegerParameter("password.min_digits", 0);
		if( min > minPasswordLength()){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		return min;
	}
	public int minNonAlphaNumeric() {
		int min = getContext().getIntegerParameter("password.min_special", 0);
		if( min > (minPasswordLength()- minDigits())){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		if( min < 0){
			return 0;
		}
		return min;
	}
	
	public String getPasswordPolicy(){
		if( CHECK_COMPLEXITY.isEnabled(getContext())){
			StringBuilder sb = new StringBuilder();
			
			sb.append( "Passwords must be at least ");
			sb.append(Integer.toString(minPasswordLength()));
			sb.append(" characters long (not counting repeated characters and character sequences). ");
			int mindiff = minDiffChars();
			if( mindiff > 1){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(mindiff));
				sb.append(" different characters. ");
			}
			int mindigit = minDigits();
			if(mindigit > 0){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(mindigit));
				sb.append(" numerical characters. ");
			}
			int minspecial = minNonAlphaNumeric();
			if(minspecial > 0){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(minspecial));
				sb.append(" non alpha-numeric characters. ");
			}
			return sb.toString();
		}
		return "Passwords must be at least "+minPasswordLength()+" characters long";
	}
	@Override
	public void validate(String data) throws FieldException {
		 Set<Character> chars = new HashSet<>();
		   int neighbours=0;
		   int specials=0;
		   int numbers=0;
		   char prev = 0;
		   for(int i=0 ; i < data.length() ; i++){
			   char c = data.charAt(i);
			   if( Character.isDigit(c)){
				   numbers++;
			   }else if( ! Character.isLetter(c)){
				   specials++;
			   }
			   chars.add(Character.valueOf(c));
			   if( c == prev+1 || c+1 == prev || c == prev){
				   neighbours++;
			   }
			   prev=c;
			   
		   }
		   int min = minDiffChars();
		   if( chars.size() < min){
			   throw new ValidateException("Password must contain at least "+min+" different characters");
		   }
		   if( (data.length() - neighbours) < minPasswordLength()){
			   throw new ValidateException("Password too simple, too many repeated or consecutive characters");
		   }
			
		   int mindigit = minDigits();
		   if( numbers < mindigit){
			   throw new ValidateException("Password must contain at least "+mindigit+" numerical digits");
		   }
		   int minspecial = minNonAlphaNumeric();
		   if( specials < minspecial){
			   throw new ValidateException("Password must contain at least "+minspecial+" non alpha-numeric characters");
		   }
		
	}
	
	public PasswordInput makeNewInput(){
		PasswordInput input = new PasswordInput();
		input.setMinimumLength(minPasswordLength());
		input.addValidator(this);
		input.setAutoCompleteHint("new-password");
		return input;
	}
}
