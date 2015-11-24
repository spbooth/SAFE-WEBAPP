// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class Sleep {

	/**
	 * 
	 */
	public Sleep() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		int seconds=1;
		if(args.length > 0){
			seconds = Integer.parseInt(args[0]);
		}
		Thread.sleep(seconds*1000);
		System.out.println("done");
		System.exit(0);;
	}

}
