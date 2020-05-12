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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * @author Stephen Booth
 *
 */
public class BadKeyTest extends WebappTestBase {

	/**
	 * 
	 */
	public BadKeyTest() {
		// TODO Auto-generated constructor stub
	}

	
	
	@Test
	public void testBadKeyList() throws FieldException, DataException {
		BadKeyFactory fac = new BadKeyFactory(ctx);
		
		String keys[] = {
				   "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDNeMOshi5BBBJYQ69NuGLMWIRlyHHoOwpK/DMDdtvQaWdBiy/qc8uH1CV3WTKYeo2+9SqeAz+XtbLVgn5j17+ehQfdvOXkVom6/Qx9NqdHr2ajXbnRaHolqT++cuvDBgThM+hJfb5j+Xfq+aw1zg2cOZAgzBAmymdMOq0mO03EWD8qMqMNQ38DHZ/QsOqec90vivxmobeIuCpsvWlcUkLD9j/I/wbgrxV1pvOMwmXWxrTJp5iiUS46mLFtcDoUAXw6NgpEyKsrsKkpv8/0/YjGn67+LVopq9/GKmP5UI9Oi9JLQm8pJS/mean8h/bP8ATqpjEthT7z207HftGHA+NB sbooth@safe-dev.epcc.ed.ac.uk",
				   "ssh-dss AAAAB3NzaC1kc3MAAACBAIgUuU08DtlGe+T/PeWfi8ytIurdcekESNnRU3XcQyD02KqZihMNTZlLKnz74c/L3LUnJNjMuDjFIOD+vOUqpUJiHnV6FHwdKJOr4GlpnrRQ03lZC9DPXir9ej5TK8nq4C88ZSVTQAJS1+oLqctNvEdHVdVYf79nEm7+6R0/YbavAAAAFQCMMmuHb0IOP5tMnMOYDm0ZVrmwFwAAAIBuurWgdSkJFO/+lSn9+V9OdVwIoLi+7N6t17VHkECvbud2oLtDjleQ5jPi/1qwFPZXfd3f7eiUtzk+uPeeixcE/E//Vekn43GJC7FB/usPL+YNIbGg7bP7deKuwqP5qkqN0cAfStuYwIwsf6mcuL90jUksjxYopctEygYR09f/gwAAAIBd8nR6JACAg6fHeqicBJ8lK4QVvTIrI/2Tj5wAyy5e+9nQ7d74ilxMssC9rRNcYrI2JZtW56bbNwFA5P2o3slkpsFddAqB4lyIEN9cwBi1XP+XVgivbcf8R5VjaqdPuCUtVQkB6PrIyhSoqGOycCWcbptiavxbL37JERqn1MrJpw== sbooth@safe-dev.epcc.ed.ac.uk",
				   "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBPp154ncTWFiS8eg8QjfYjk/X9sjOb0exvvEmujhDXpbDRa2AnDF8S/XMYpNkELFYTiPhHQk9r29DhTuokqO338= sbooth@safe-dev.epcc.ed.ac.uk",
				   "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHwhd4XOp1hr84ckEP2eYvbSyG6KxHcbHMgObQPv8FWf sbooth@safe-dev.epcc.ed.ac.uk"
		   };
		
		for(String s : keys) {
			assertTrue(fac.allow(s));
			fac.validate(s);
		}
		fac.forbid(keys[0]);
		
		
		for(int i=0 ; i< keys.length ; i++) {
			assertEquals(i != 0 , fac.allow(keys[i]));
		}
		
	}
}
