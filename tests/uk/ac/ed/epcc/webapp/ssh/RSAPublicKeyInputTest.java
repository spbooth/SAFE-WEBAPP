//| Copyright - The University of Edinburgh 2014                            |
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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/**
 * @author spb
 *
 */

public class RSAPublicKeyInputTest extends ParseAbstractInputTestCase<String,RsaPublicKeyInput>{

	
	public RsaPublicKeyInput getInput() {
		return new RsaPublicKeyInput();
	}

	
	public Set<String> getBadParseData() {
		Set<String> bad_parse = new HashSet<String>();
		bad_parse.add("I am a hamster");
		bad_parse.add("ssh-dss AAAAB3NzaC1kc3MAAACBAMot/qdk4G5WgLUTodzXa8YdvzmvsQsEHRMhgfRtq2DGDXn6QYuO"+
				"I4ORiXHXLhyaY4Igtc3bj3QFPNNy/tDzFt+ejhknPlgGZtn1W9ye4/b//EBiyUFT/y7ETYP2tvfAhVJn"+
				"BkmDEXxoMgnTyZLGwjzHkhpsN1yeZkS+cISZVXP/AAAAFQCrvEDO7oarp4uNPfsiX7fb390fawAAAIB2"+
				"RnGx/WHGPjYolKNKC8oTmzO7CRLs6SqMjdCcqNCDYRkt7DnS4VKO1KCjO03mI1kTjNXgjX7zlMFkmF4o"+
				"pWQmDgqB7HPBb0/dgdyCXOIOErzMCjqu70anMaFz3m5buH7hq2lcuxrLlAX5tNmHU2WL/nEgSb5CnKXS"+
				"NA0h0pbnyAAAAIBLFrX7ZWEuZnzFThiRVrkHJY5GcEVt8PM16MvhVdbYbxJhWeVfQHwCX4j8ngVxywBw"+
				"dEuAWm066xAfWLU1cf/PM/CbKwaMMHA/i2VMlydDhfMHeiV3ydhDFke7h9geKAxM26/Cewd6U4OOJyrr"+
				"1cYMVm+GXeHOML6nGR41aK48eQ== spb@t3400spb.epcc.ed.ac.uk");
		// duplicate keys
//		bad_parse.add("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEApjAn9h0LR4QG4P42qEThPT7IfvQiOQ2xM2UbNvaET2hiPMz7xIoyyButLtib/+mWeY+njRUKe1ENW5IF8OawvzIF5TCFt3/Da40es3y0a5YYzJuB8XJl5Nm99Xf82J9hAzkrxb66j40z8zCassFQ6aA+Sp/W3xDFucYGMHpU0UGSD934G2moBlBPsOfer+LnWyjBwJbfPM+/DgM54gCHCfJf67kSCWdmYhtAQqhiBWM4lHZrfdOOezDikhz0oh9nCyevBIKWZeZU928hLpLJOENe/MbHPshp0QSieFyX3qHXgh4fitLGD/SLsSt78cgsNRpRiYLrsrYHwXnm/UWkVQ==ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC3o+JxZAijBA+kWaWo6Ls0j8Wclb20OrM3lY8/YHkGFCFTQ4cW1oEZTe9pzbFcc+ww+Lr3FKfnlngX3EBj1xZLVdq82iemdCKnEtvWpLev1dKn4PQ4ib5kRBztVBy8aDmJdBmKO1SaQYt7ECWqIdCxjZHH/0YBGPb46SsO8aAYSV7/LrKXKRJt84R+LwPbTN60cqeXK+nWFHL4B7LVT8bHTtS7DWVHAMMat0OYGF9ou/d75dykd4c2MWL7UEuiuXq9fFPQuSgJVffvEkGDMNW5fgxD5PwyqSjz38H2gRqkxaZ6PeAAC+oHaYzenjQ73D1clsJSKVc8U1PyAeodRDeJssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC3o+JxZAijBA+kWaWo6Ls0j8Wclb20OrM3lY8/YHkGFCFTQ4cW1oEZTe9pzbFcc+ww+Lr3FKfnlngX3EBj1xZLVdq82iemdCKnEtvWpLev1dKn4PQ4ib5kRBztVBy8aDmJdBmKO1SaQYt7ECWqIdCxjZHH/0YBGPb46SsO8aAYSV7/LrKXKRJt84R+LwPbTN60cqeXK+nWFHL4B7LVT8bHTtS7DWVHAMMat0OYGF9ou/d75dykd4c2MWL7UEuiuXq9fFPQuSgJVffvEkGDMNW5fgxD5PwyqSjz38H2gRqkxaZ6PeAAC+oHaYzenjQ73D1clsJSKVc8U1PyAeodRDeJssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC3o+JxZAijBA+kWaWo6Ls0j8Wclb20OrM3lY8/YHkGFCFTQ4cW1oEZTe9pzbFcc+ww+Lr3FKfnlngX3EBj1xZLVdq82iemdCKnEtvWpLev1dKn4PQ4ib5kRBztVBy8aDmJdBmKO1SaQYt7ECWqIdCxjZHH/0YBGPb46SsO8aAYSV7/LrKXKRJt84R+LwPbTN60cqeXK+nWFHL4B7LVT8bHTtS7DWVHAMMat0OYGF9ou/d75dykd4c2MWL7UEuiuXq9fFPQuSgJVffvEkGDMNW5fgxD5PwyqSjz38H2gRqkxaZ6PeAAC+oHaYzenjQ73D1clsJSKVc8U1PyAeodRDeJ");
		return bad_parse;
	}

	
	public Set<String> getGoodParseData() {
		Set<String> good = new HashSet<String>();
		// with and without newline breaks
		good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy\n"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79\n"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l\n"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt\n"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN\n"+
		"iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8\n"+
		"VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/\n"+
		"5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t\n"+
		"nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");
		
		good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN"+
				"iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8"+
				"VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/"+
				"5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t"+
				"nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");
		return good;
	}

	
	public Set<String> getBadData() {
		return getBadParseData();
	}

	
	public Set<String> getGoodData() {
		Set<String> good = new HashSet<String>();
		// only without newline breaks
		good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP");
		return good;
	}

	

	@Test
	public void testNormalise() throws ParseException {
		RsaPublicKeyInput input = new RsaPublicKeyInput();
		
		input.parse("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
		
		
		assertEquals("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy"+
				"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79"+
				"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l"+
				"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt"+
				"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP",
				input.getValue()
				);
	}
}