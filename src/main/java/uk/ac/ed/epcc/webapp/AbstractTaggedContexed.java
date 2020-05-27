package uk.ac.ed.epcc.webapp;

public class AbstractTaggedContexed extends AbstractContexed implements Tagged {

	public AbstractTaggedContexed(AppContext conn,String tag) {
		super(conn);
		this.tag=tag;
	}
	private final String tag;
	@Override
	public final String getTag() {
		return tag;
	}

}
