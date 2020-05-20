//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.ssh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * @author Stephen Booth
 *
 */
public class AuthorizedKeyValidatorTest {

	@Test
   public void testPattern() {
	   
		Pattern p = Pattern.compile(AuthorizedKeyValidator.getOptionPattern());
		
		assertNotNull(p);
		
		for(String good : new String[] { 
				"command=\"echo hello\",X11-forwarding",
				"no-pty",
				"restrict",
				"x11-forwarding,environment=\"PATH=/bin\"",
				"NO-PTY,Port-FORWARDING",
				"command=\"echo \\\"hi there\\\"\""
				
		}) {
			Matcher m = p.matcher(good);
			assertTrue(good,m.matches());
		}
		
		for(String bad : new String[] { 
				"",
				"PEnguins",
				"ssh-rsa"
				
		}) {
			Matcher m = p.matcher(bad);
			assertFalse(bad,m.matches());
		}
		
   }
	
   @Test
   public void testValidate() throws ValidateException {
	
	   String keys[] = {
			   "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDNeMOshi5BBBJYQ69NuGLMWIRlyHHoOwpK/DMDdtvQaWdBiy/qc8uH1CV3WTKYeo2+9SqeAz+XtbLVgn5j17+ehQfdvOXkVom6/Qx9NqdHr2ajXbnRaHolqT++cuvDBgThM+hJfb5j+Xfq+aw1zg2cOZAgzBAmymdMOq0mO03EWD8qMqMNQ38DHZ/QsOqec90vivxmobeIuCpsvWlcUkLD9j/I/wbgrxV1pvOMwmXWxrTJp5iiUS46mLFtcDoUAXw6NgpEyKsrsKkpv8/0/YjGn67+LVopq9/GKmP5UI9Oi9JLQm8pJS/mean8h/bP8ATqpjEthT7z207HftGHA+NB sbooth@safe-dev.epcc.ed.ac.uk",
			   "ssh-dss AAAAB3NzaC1kc3MAAACBAIgUuU08DtlGe+T/PeWfi8ytIurdcekESNnRU3XcQyD02KqZihMNTZlLKnz74c/L3LUnJNjMuDjFIOD+vOUqpUJiHnV6FHwdKJOr4GlpnrRQ03lZC9DPXir9ej5TK8nq4C88ZSVTQAJS1+oLqctNvEdHVdVYf79nEm7+6R0/YbavAAAAFQCMMmuHb0IOP5tMnMOYDm0ZVrmwFwAAAIBuurWgdSkJFO/+lSn9+V9OdVwIoLi+7N6t17VHkECvbud2oLtDjleQ5jPi/1qwFPZXfd3f7eiUtzk+uPeeixcE/E//Vekn43GJC7FB/usPL+YNIbGg7bP7deKuwqP5qkqN0cAfStuYwIwsf6mcuL90jUksjxYopctEygYR09f/gwAAAIBd8nR6JACAg6fHeqicBJ8lK4QVvTIrI/2Tj5wAyy5e+9nQ7d74ilxMssC9rRNcYrI2JZtW56bbNwFA5P2o3slkpsFddAqB4lyIEN9cwBi1XP+XVgivbcf8R5VjaqdPuCUtVQkB6PrIyhSoqGOycCWcbptiavxbL37JERqn1MrJpw== sbooth@safe-dev.epcc.ed.ac.uk",
			   "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBPp154ncTWFiS8eg8QjfYjk/X9sjOb0exvvEmujhDXpbDRa2AnDF8S/XMYpNkELFYTiPhHQk9r29DhTuokqO338= sbooth@safe-dev.epcc.ed.ac.uk",
			   "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHwhd4XOp1hr84ckEP2eYvbSyG6KxHcbHMgObQPv8FWf sbooth@safe-dev.epcc.ed.ac.uk",
			   " ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAgEA2N2TXMvqmK5IDUTeUfLuccm5em3vbKKj+TUdqVioP50icoDBxaV6F0zGAYaJ1oCLcqmM9lqX91MkvNoFmtuHwlvqvr/cRq5oBlbxEbkObF4TNoXSq12fRrKnkIYO52On36TAOkv5oGmKY2JdFdiYXCSZ8kHa31+ZxpjiEb7XX0KbBeWI8BQie+a+PCcVgI7O7y9mP0+H4y37Ej5Xz7sBmlCcxLmpp5VwRjvlRgxUF8SVZw74rT4Fdr/yhogmJi0B5Ta9sN7Fl7QvyU1JCWW4KQpaLr93e+06RFiE9p/Zkq7IY8btpTXgvwuWzE/FRPLi5/ViIijshHwv2g86tNWB//wKZ7HarSqgRmT2aj/7qgaEM8PbnAZtaAmkZnfQdXF3BibuywJ5isjI1ZspZt7Gv3HytrH0X0nWgC5BRwQksIDTVZ4+oVGqhP9VmaQDP+wUOATh6miRitc4ERnR30XWcEoafa++V+v1+B7z5F0wcIzi+mpwrHx/89d3y81sRy0Qis4aJ4SY8z1pqiAxsaassEw0RKvv9hWLci8Y+LkZTwxyV+GyGQd9UFwLu4E6Z1nz2nKRDLbY7zvFPsxBq9KkQ2eUm1FiYBZ49uWg2l0RiGfLuTERPHH/INFWVeCqfW9yf69OWSeJgFpe3m9Ucau5bVq2fvcTC/zVBLz9a7yQj90= rsa-key-20200514 (Archer)"
	   };
	   String options[] = {
			   "command=\"echo hello\",X11-forwarding ",
				"no-pty ",
				"restrict ",
				"x11-forwarding,environment=\"PATH=/bin\" ",
				"NO-PTY,Port-FORWARDING ",
				"command=\"echo \\\"hi there\\\"\" ",
				""
	   };
	   AuthorizedKeyValidator x = new AuthorizedKeyValidator();
	   for(String p : options) {
		   for(String k : keys) {
			   String v = p+k;
			   System.out.println(v);
			   x.validate(v);
		   }
	   }
	   
	   
   }
   
   @Test
   public void testFingerprint() throws ParseException, NoSuchAlgorithmException {
	   AuthorizedKeyValidator x = new AuthorizedKeyValidator();
	   
	   assertEquals("MD5:c5:f5:ea:97:5e:59:97:1a:df:b1:b9:30:5d:ea:f1:01", x.fingerprint("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDUSeRMDqhz4AvPOjB/I0iHy0yc69zCbI3WI1yHm3DRbGRe7WVtL4AhTezEd3GKA+yf3ky/LlmnmkGniGZfMWott71gb6MfvLZKJ4XbKCwrzpogLkwahGv4RePtRrj+q65e1wdUbTvZNaQLCqyozyZEnyDSouV8I7wRUwDKqa7Wth/61cHSI6r7ID8YjxbK+jXkxCEGDZvUQa8s6ufNM8TWLpOqCk8djJKyrPtl1I87QCr6hxLt3220cFs4t4U11mlfpLTSKXqBwzYDx6pazgBAot8gcvXZdeqtYB0klknNlR3X8imo/tqaAs4hX1f74K7w82rk8Np5cZjFdcDhezNh stephen booth@E7390SB"));
	   assertEquals("SHA256:tIO5+QZY2LmVPTm68kNvVYH1eqRceYgVb52ZHBuAwnk", x.fingerprint2("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDUSeRMDqhz4AvPOjB/I0iHy0yc69zCbI3WI1yHm3DRbGRe7WVtL4AhTezEd3GKA+yf3ky/LlmnmkGniGZfMWott71gb6MfvLZKJ4XbKCwrzpogLkwahGv4RePtRrj+q65e1wdUbTvZNaQLCqyozyZEnyDSouV8I7wRUwDKqa7Wth/61cHSI6r7ID8YjxbK+jXkxCEGDZvUQa8s6ufNM8TWLpOqCk8djJKyrPtl1I87QCr6hxLt3220cFs4t4U11mlfpLTSKXqBwzYDx6pazgBAot8gcvXZdeqtYB0klknNlR3X8imo/tqaAs4hX1f74K7w82rk8Np5cZjFdcDhezNh stephen booth@E7390SB"));

   
   }
}
