package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public class NullFilterResult<D> extends AbstractFilterResult<D> {

	private final Logger log;
	public NullFilterResult(Logger log) {
		this.log=log;
	}

	@Override
	protected CloseableIterator<D> makeIterator() throws DataFault {
		return new NullIterator<D>();
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

}
