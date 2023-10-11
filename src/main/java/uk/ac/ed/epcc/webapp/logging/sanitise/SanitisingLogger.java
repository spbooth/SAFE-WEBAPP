package uk.ac.ed.epcc.webapp.logging.sanitise;

import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.logging.Logger;

public class SanitisingLogger implements Logger {

	private final Logger nested;
	public SanitisingLogger(Logger nested) {
		this.nested=nested;
	}

	
	public Supplier<String> clean(Object message){
		return cleanString(message.toString());
	}
	public Supplier<String> cleanString(String input) {
		return new Supplier<String>() {

			@Override
			public String get() {
				StringBuilder sb = new StringBuilder();
				for( int i=0 ; i< input.length() ; i++) {
					int code = input.codePointAt(i);
					if( Character.isWhitespace(code)) {
						sb.append(Character.toString(code));
					}else if( code < 32 || code == 127 ) {
						sb.append("\\");
						sb.append(Integer.toHexString(code));
					}else {
						sb.append(Character.toString(code));
					}
				}
				return sb.toString();
			}
			
		};
	}
	public Supplier<String> cleanS(Supplier<String> s){
		return cleanString(s.get());
	}
	@Override
	public void debug(Object message) {
		nested.debug(clean(message));
	}

	@Override
	public void debug(Object message, Throwable t) {
		nested.debug(clean(message), t);

	}

	@Override
	public void error(Object message) {
		nested.error(clean(message));

	}

	@Override
	public void error(Object message, Throwable t) {
		nested.error(clean(message),t);

	}

	@Override
	public void fatal(Object message) {
		nested.fatal(clean(message));

	}

	@Override
	public void fatal(Object message, Throwable t) {
		nested.fatal(clean(message),t);

	}

	@Override
	public void info(Object message) {
		nested.info(clean(message));
	}

	@Override
	public void info(Object message, Throwable t) {
		nested.info(clean(message),t);

	}

	@Override
	public void warn(Object message) {
		nested.warn(clean(message));

	}

	@Override
	public void warn(Object message, Throwable t) {
		nested.warn(clean(message),t);

	}

	@Override
	public void debug(Supplier<String> message) {
		nested.debug(cleanS(message));
	}

	@Override
	public void debug(Supplier<String> message, Throwable t) {
		nested.debug(cleanS(message),t);
	}

	@Override
	public void error(Supplier<String> message) {
		nested.error(cleanS(message));
	}

	@Override
	public void error(Supplier<String> message, Throwable t) {
		nested.error(cleanS(message),t);
	}

	@Override
	public void fatal(Supplier<String> message) {
		nested.fatal(cleanS(message));
	}

	@Override
	public void fatal(Supplier<String> message, Throwable t) {
		nested.fatal(cleanS(message), t);
	}

	@Override
	public void info(Supplier<String> message) {
		nested.info(cleanS(message));
	}

	@Override
	public void info(Supplier<String> message, Throwable t) {
		nested.info(cleanS(message),t);
	}

	@Override
	public void warn(Supplier<String> message) {
		nested.warn(cleanS(message));
	}

	@Override
	public void warn(Supplier<String> message, Throwable t) {
		nested.warn(cleanS(message),t);
	}

}
