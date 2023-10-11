package uk.ac.ed.epcc.webapp.logging.sanitise;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.logging.buffer.BufferLoggerService;

public class TestSanitise extends WebappTestBase {

	public TestSanitise() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testSanitise() {
		BufferLoggerService bl = new BufferLoggerService(ctx);
		ctx.setService(bl);
		SanitisingLoggerService sl = new SanitisingLoggerService(ctx);
		ctx.setService(sl);
		
		Logger l = ctx.getService(LoggerService.class).getLogger(getClass());
		l.warn("A message with a \u0007  \b \n");
		
		String string = bl.getBuffer().toString();
		string = string.replace("\r\n", "\n");
		assertEquals("A message with a \\7  \\8 \n"
				+ "\n"
				+ "", string);
		
	}
}
