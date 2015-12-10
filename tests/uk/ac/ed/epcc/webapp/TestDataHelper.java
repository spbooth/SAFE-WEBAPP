//| Copyright - The University of Edinburgh 2015                            |
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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

/**
 * The TestDataHelper is a utility class for testing 
 */
public class TestDataHelper {



	
	/**
	 * Uses the contents of a file to populate a <code>Properties</code> object.
	 * 
	 * @param file the file to read
	 * 
	 * @return A <code>Properties</code> object containing all the properties 
	 * specified in the file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Properties readFileAsProperties(File file) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		return properties;
	}

	/**
	 * Read the contents of a file into a string.
	 * 
	 * @param file the file to read
	 * 
	 * @return the contents of the file into a string.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readFileAsString(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
		
	}
	
	
	
	
	/**
	 * Write the string to the file.
	 * 
	 * @param file the file to be written to
	 * @param output String to output
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeFile(File file, String output) throws IOException {		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(output);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Compare the line of expected to ensure each on is found in the actual 
	 * result but don't worry about the order of the lines.
	 * 
	 * @param actual the actual string 
	 * @param expected the expected lines
	 * 
	 * @return true, if successful
	 */
	public static boolean compareUnordered(String actual, String expected) {			
		for(String string: expected.split("\n")) {			
			if (!actual.contains(string)) {
				return false;
			}			
		}	
		return true;
		
	}


	/**
	 * Diffs two strings line by line
	 * 
	 * @param fromString the from string
	 * @param toString the to string
	 * 
	 * @return the string
	 */
	public static String diff(String fromString, String toString) {
		StringBuffer result = new StringBuffer();
		// Break up dense xml for easier diffing.
		String[] origionalArray = fromString.replace(">\\s*<", ">\n<").split("\r?\n");
		String[] newArray = toString.replace(">\\s*<",">\n<").split("\r?\n");
		
        Diff diff = new Diff(origionalArray, newArray); 
      
        for (Object object :diff.diff()) {
        	Difference difference = (Difference)object;
        
        	int as = difference.getAddedStart();
        	int ae = difference.getAddedEnd();
        	int ds = difference.getDeletedStart();
        	int de = difference.getDeletedEnd();
           
        	if (difference.getDeletedEnd() == -1) {
            	result.append(
            			ds+"a"+
            			(as+1)+","+
            			(ae+1)
            			+"\n");
            	for (int i = as; i <= ae; i++) {
            		result.append("> "+newArray[i]+"\n");
            	}
            	
        	} else if (ae == -1) {
            	result.append(
            			(as)+
            			"d"+
            			(ds+1)+","+
            			(de+1)
            			+"\n");
            	for (int i = ds; i <= de; i++) {
            		result.append("< "+origionalArray[i]+"\n");
            	}            	
        		
        	} else {
            	result.append(ds+1);
            	if (ds != de) {
                	result.append(","+(de+1));
            	}
            	result.append("c");
            	result.append(as+1);
            	if (as != ae) {
                	result.append(","+(ae+1));
            	}
            	result.append("\n");

            	for (int i = difference.getDeletedStart(); i <= difference.getDeletedEnd(); i++) {
            		result.append("< "+origionalArray[i]+"\n");
            	}
            	result.append("---\n");
            	for (int i = difference.getAddedStart(); i <= difference.getAddedEnd(); i++) {
            		result.append("> "+newArray[i]+"\n");
            	}
        	}
        	
        }
        return result.toString();
	}
	
	/**
	 * Redirect System.out to an file int he temp dir so iit can read later.
	 */
	private static PrintStream stdOut = System.out;	
	private static File redirectedStdOut;
	
	public static void redirectStdOut() throws IOException {			
		if (redirectedStdOut == null) {
			String redirectionFileName = ".redirected-of-stdout.txt";	
			String tmpDirName = System.getProperty("java.io.tmpdir");
			if (tmpDirName != null) {
				File tmpDir = new File(tmpDirName);
				if (tmpDir.exists() && tmpDir.isDirectory() && 
						tmpDir.canWrite()) {
					redirectionFileName = tmpDir.getAbsolutePath() + 
						File.separator + redirectionFileName;
					
				}		
			}
			redirectedStdOut = new File(redirectionFileName);	
		}	
		System.setOut(new PrintStream(redirectedStdOut));
		
	}
	
	public static void resetStdOut() {
		redirectedStdOut.delete();
		System.setOut(stdOut);
	}

	public static String readStdOut() throws IOException {
		return  readStream(new FileInputStream(redirectedStdOut));
	}
	
	/**
	 * Redirect System.err to an file int he temp dir so iit can read later.
	 */
	private static PrintStream stdErr = System.err;	
	private static File redirectedStdErr;
	
	public static void redirectStdErr() throws IOException {	
		if (redirectedStdErr == null) {
			String redirectionFileName = ".redirected-of-stdout.txt";	
			String tmpDirName = System.getProperty("TEMP");
			if (tmpDirName != null) {
				File tmpDir = new File(tmpDirName);
				if (tmpDir.exists() && tmpDir.isDirectory() && 
						tmpDir.canWrite()) {
					redirectionFileName = tmpDir.getAbsolutePath() + 
						File.separator + redirectionFileName;
					
				}		
			}
			redirectedStdErr = new File(redirectionFileName);	
		}	
		System.setErr(new PrintStream(redirectedStdErr));
	}
	
	public static void resetStdErr() {
		redirectedStdErr.delete();
		System.setErr(stdErr);
	}

	public static String readStdErr() throws IOException {
		return  readStream(new FileInputStream(redirectedStdErr));
	}
	
	/**
	 * Reads all the available data from an InputStream into a String.
	 * @param stream InputStream
	 * 
	 * @return a string of the data read form the stream.
	 * @throws IOException 
	 */
	public static String readStream(InputStream stream) throws IOException {
	
		
		StringBuffer string = new StringBuffer();
		int c;
		while((c=stream.read()) > 0) {
			string.append((char)c);
		}
		return string.toString();
	}
	


}