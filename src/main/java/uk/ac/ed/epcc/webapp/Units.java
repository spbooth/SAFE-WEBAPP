package uk.ac.ed.epcc.webapp;

/** Enum for standard storage units. 
 * 
 * Aliases are allowed
 * When formatting a value the last definition where the
 * size is an exact multiple is used (unless the value is 1) When parsing a value
 * the first matching value is used. Therefore short forms should occur last in a list.
 * 
 * @author Stephen Booth
 *
 */
public enum Units {
	B(1L),
	kB(1000L),
	KiB(1024L),
	K(1024L),
	MB(1000_000L),
	MiB(1024L*1024L),
	M(1024L*1024L),
	GB(1000_000_000L),
	GiB(1024L*1024L*1024L),
	G(1024L*1024L*1024L),
	TB(1000_000_000_000L),
	TiB(1024L*1024L*1024L*1024L),
	T(1024L*1024L*1024L*1024L),
	PB(1000_000_000_000_000L),
	PiB(1024L*1024L*1024L*1024L*1024L),
	P(1024L*1024L*1024L*1024L*1024L);
	public final long bytes;
	private Units(long bytes) {
		this.bytes=bytes;
	}
}
