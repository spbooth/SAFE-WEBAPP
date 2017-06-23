// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.session;

import java.security.SecureRandom;

import uk.ac.ed.epcc.webapp.AppContextService;

/** An {@link AppContextService} to generate random tokens and passwords.
 * 
 * These need to be secure as they are likely to be used as authentication tokens.
 * @author spb
 *
 */

public class RandomService implements AppContextService<RandomService> {
	SecureRandom random = new SecureRandom();

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class getType() {
		return RandomService.class;
	}
	
	/**
	 * Generates random sequences of characters taken from the input range,
	 * which has the format:
	 * 
	 * <pre>
	 * 	 {
	 * 	 ( {&lt;first_char&gt;, &lt;second_char&gt;}, ) | {&lt;single_char&gt;}, ) *
	 * 	 }
	 * 	
	 * </pre>
	 * 
	 * The <code>second_char</code> value <i>must</i> be higher than the
	 * <code>first_char</code> value or the output will be undefined or
	 * generate an <code>Exception</code>.
	 * 
	 * @param chars
	 *            An array containing ranges of characters and/or single
	 *            characters
	 * @param length
	 *            The length of the desired random string
	 * @return A random string chosen from the provided input
	 */
	public String randomString(char[][] chars, int length) {
		// Work out how many possible characters are available
		int total_chars = 0;
		for (int i = 0; i < chars.length; i++) {
			char[] range = chars[i];
			if (range.length == 0) {
				continue; // Bad format, ignore
			}
			if (range.length == 1) {
				total_chars++;
			} else {
				total_chars += (range[1] - range[0]) + 1;
			}
		}
	
		StringBuilder buff = new StringBuilder(length);
		for (int c = 0; c < length; c++) {
			// Pick a random character from the ranges
			int r = randomInt(total_chars);
	
			// Work out which character we picked
			for (int i = 0; i < chars.length; i++) {
				char[] range = chars[i];
				if (range.length == 0) {
					continue;
				}
				if (range.length == 1) {
					if (r == 0) {
						buff.append(chars[i][0]);
						break;
					}
					r--;
					continue;
				}
				int char_range = (range[1] - range[0]) + 1;
				if (r >= char_range) {
					r -= char_range;
				} else {
					buff.append((char) (chars[i][0] + r));
					break;
				}
			}
		}
		return buff.toString();
	}

	/**
	 * @param total_chars
	 * @return
	 */
	protected int randomInt(int total_chars) {
		return random.nextInt(total_chars);
	}
	/**
	 * Generates random sequences of characters in the ranges 'a'..'z', 'A'..'Z'
	 * and '0'..'9'.
	 * 
	 * @param length
	 *            The desired length of the output String
	 * @return String
	 */
	public String randomString(int length) {
		char[][] chars = { { 'a', 'z' }, { 'A', 'Z' }, { '0', '9' }, };
		return randomString(chars, length);
	}
}
