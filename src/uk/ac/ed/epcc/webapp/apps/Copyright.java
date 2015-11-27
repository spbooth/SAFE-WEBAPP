package uk.ac.ed.epcc.webapp.apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * <p>
 * Basic code used to parse a directory and add a copyright notice to the top of
 * each file that ends in <em>java,properties,sql,wsdl,xml</em> or <em>xsd</em>.
 * The appropriate type of comment is used for each file type. For XML type
 * files, the copyright notice is added after the standard XML declaration
 * (&lt;?xml...).
 * </p>
 * <p>
 * This code is run using the main method. The root directory to start from can
 * be specified as the first argument. If no arguments are present, the current
 * working directory is used.
 * </p>
 * 
 * 
 * 
 * @author jgreen4
 * 
 */
public class Copyright {

	/*
	 * ##########################################################################
	 * EDITABLE VARIABLES - variables to edit if the copyright notice is to be
	 * changed.
	 * ##########################################################################
	 */

	/**
	 * Copyright notice
	 */
	public static final String COPYRIGHT = "Copyright - The University of Edinburgh "
			+ Calendar.getInstance().get(Calendar.YEAR);
	/**
	 * Regular expression matching the copyright notice or any other notice at the
	 * top of a file to be replaced by the copyright notice.
	 */
	public static final Pattern COPYRIGHT_REGEX = Pattern
			.compile("Copyright - The University of Edinburgh \\d{4}");
	
	public static final Pattern VERSION_REGEX = Pattern
			.compile("@(:?uk\\.ac\\.ed\\.epcc\\.webapp\\.)?Version\\([^\\)]*\\)");
	

	/*
	 * ##########################################################################
	 * END OF EDITABLE VARIABLES - variables below this line should not be
	 * modified unless the function of the program is to change
	 * ##########################################################################
	 */

	// Keyed by file extension
	public static final Map<String, String> copyrightText = new HashMap<String, String>();
	static {
		copyrightText.put("java", "// " + COPYRIGHT);
		copyrightText.put("properties", "# " + COPYRIGHT);
		copyrightText.put("sql", "-- " + COPYRIGHT);
		copyrightText.put("wsdl", "<!-- " + COPYRIGHT + " -->");
		copyrightText.put("xml", "<!-- " + COPYRIGHT + " -->");
		copyrightText.put("xsd", "<!-- " + COPYRIGHT + " -->");
		
		copyrightText.put("jsp", "<%-- " + COPYRIGHT + " --%>");
		copyrightText.put("jsf", "<%-- " + COPYRIGHT + " --%>");
	}

	public static final String XML_HEADER_START = "<?xml";

	public static final String NEW_LINE = System.getProperty("line.separator");

	// sanity check
	static {
		assert COPYRIGHT_REGEX.matcher(COPYRIGHT).matches() : "The regular "
				+ "expression for the copyright statement didn't match the current "
				+ "copyright notice";
	}

	private static class MutInt {
		private int i;

		public MutInt() {
			this.i = 0;
		}

		public int getValue() {
			return this.i;
		}

		public int increment() {
			this.i++;
			return this.i;
		}

		public String toString() {
			return Integer.toString(this.i);
		}
	}

	/**
	 * <p>
	 * Examines all files recursively from a specified root directory/file. If a
	 * file ends in <code>.java</code> or the xml extensions <code>.xml</code>,
	 * <code>.xsd</code> or <code>.wsdl</code>, a copyright notice is added (if it
	 * is not already present). If a copyright notice is present and matches the
	 * regular expression used to find copyright notices, but does not match the
	 * copyright notice being used, the old notice is replaced.
	 * </p>
	 * <p>
	 * This method takes a string array. Element 0 (if present) should be the root
	 * directory to search. If absent, the current working directory is used.
	 * </p>
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		File root;
		int total = 0;
		int skipped = 0;

		Map<String, MutInt> modFiles = new HashMap<String, MutInt>();
		Map<String, MutInt> numFiles = new HashMap<String, MutInt>();
		for (String type : copyrightText.keySet()) {
			modFiles.put(type, new MutInt());
			numFiles.put(type, new MutInt());
		}

		if (args.length < 1 && args[0] != null) {
			root = new File(System.getProperty("user.dir"));
		} else {
			root = new File(args[0]);
		}

		System.out.println("Adding copyright notice to all files in " + root);

		Collection<File> files = getFiles(root);
		for (File file : files) {
			total++;

			// Get the file extension
			String name = file.getName();
			int nameLen = name.length();
			int extensionDot = name.lastIndexOf('.');
			if (extensionDot < 0 || extensionDot >= nameLen - 1) {
				skipped++;
				continue;
			}
			String extension = name.substring(extensionDot + 1, nameLen);

			// Get a copyright notice and apply it if one exists for this extension
			String copyright = copyrightText.get(extension);
			if (copyright == null) {
				skipped++;
				continue;
			}

			try {
				numFiles.get(extension).increment();
				if (appendCopyright(file, copyright,extension)) {
					modFiles.get(extension).increment();
				} else {
					skipped++;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		System.out.println("Summary:");
		System.out.println("\tTotal files encountered: " + total);
		System.out.println("\tTotal files skipped:     " + skipped);
		for (String type : copyrightText.keySet()) {
			System.out.printf("\tTotal encountered/modified files of file type "
					+ "%1$14s: %2$8s/%3$-8s\n", type, numFiles.get(type), modFiles
					.get(type));
		}

		System.out.println();
	}

	/*
	 * ##########################################################################
	 * PRIVATE METHODS
	 * ##########################################################################
	 */

	/**
	 * Append the copyright notice to files. If the xml header is encountered
	 * 
	 * @param file
	 *          The file to add the copyright notice to. .
	 * @param copyrightText
	 *          The copyright text to add
	 * @return <code>true</code> if a copyright notice was added or modified.
	 *         <code>false</code> if the file was left unchanged.
	 */
	private static boolean appendCopyright(File file, String copyrightText,String extension)
			throws FileNotFoundException, IOException {

		Scanner scanner = new Scanner(file);
		// If the file is empty, just add the notice
		if (scanner.hasNext() == false) {
			save(file, copyrightText);
			return true;
		}

		StringBuilder header = new StringBuilder();
		String line = scanner.nextLine();

		// If the file starts with the usual xml header
		if (line.startsWith(XML_HEADER_START)) {
			// If the file contains no xml, just add the notice below the xml
			// declaration
			if (scanner.hasNext() == false) {
				save(file, line, NEW_LINE, copyrightText);
				return true;
			}

			header.append(line).append(NEW_LINE);
			line = scanner.nextLine();
		}

		// If a copyright notice is already present
		if (COPYRIGHT_REGEX.matcher(line).find()) {
			// If the copyright notice is already there, we don't need to do anything
			// but still go on to do the version stuff
			
			// if we wanted to update
			//if (! line.trim().equals(copyrightText)) {
			//	header.append(copyrightText).append(NEW_LINE);
			//}
		} else {
			header.append(copyrightText).append(NEW_LINE);
			header.append(line).append(NEW_LINE);
		}

		// Read in the whole file;
		scanner.useDelimiter("\\Z");
		String restOfFile;
		if (scanner.hasNext()) {
			restOfFile = scanner.next();
		} else {
			restOfFile = "";
		}
		scanner.close();

		if (scanner.ioException() != null) {
			throw scanner.ioException();
		}
		header.append(restOfFile);

		String data;
		// now add id flags if necessary
		if( extension.equals("java")){
			data=addID(header);
		}else{
			data=header.toString();
		}
		
		save(file, data);

		return true;
	}

	
	private static String addID(StringBuilder header) {
		
		Matcher m = VERSION_REGEX.matcher(header);
		StringBuffer sb = new StringBuffer();
		
		if( m.find()){
			// removes the version annotation
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	/**
	 * Convenience method for recursively adding all files in a specified
	 * directory and the files indirrectories of all of its children. Hidden files
	 * are not added. If a directory is hidden, it will be skipped even if it
	 * contains files that are not hidden.
	 * 
	 * @param root
	 *          The file or directory to have it's child files added to the
	 *          returned collection.
	 * @return All non-hidden files contained in <code>root</code> and all of it's
	 *         sub-directories.
	 */
	private static Collection<File> getFiles(File root) {
		// We don't return hidden files or the contents of hidden directories
		if (root.isHidden())
			return Collections.emptySet();

		// If it's a file, return it in the appropriate wrapper object
		if (root.isFile()) {
			return Collections.singleton(root);
		}

		// go through all children
		Collection<File> files = new ArrayList<File>();
		for (File child : root.listFiles()) {
			if (child.isFile()) {
				if (child.isHidden() == false) {
					files.add(child);
				}
			} else {
				// If it's a directory, recursively add it's children
				files.addAll(getFiles(child));
			}
		}
		return files;
	}

	/**
	 * Convenience method for saving a file. All strings are concatenated and
	 * saved to the file. Nothing is inserted between strings so new lines must be
	 * explicitly added
	 * 
	 * @param file
	 *          The file to save the text to
	 * @param textChunks
	 *          chunks. Text to write to the file
	 * @throws IOException
	 *           IOException If an I/O error occurs
	 */
	private static void save(File file, String... textChunks) throws IOException {
		BufferedWriter out = null;

		// Concatenate the strings
		String text;
		if (textChunks.length == 0 || textChunks[0] == null) {
			text = "";
		} else if (textChunks.length == 1) {
			text = textChunks[0];
		} else {
			StringBuilder sb = new StringBuilder();
			for (String chunk : textChunks) {
				sb.append(chunk);
			}
			text = sb.toString();
		}

		// Write the file
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(text);
			out.close();
		} catch (IOException e) {
			if (out != null)
				out.close();
			throw e;
		}
	}
}