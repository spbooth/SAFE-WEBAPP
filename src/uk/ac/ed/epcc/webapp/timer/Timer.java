//| Copyright - The University of Edinburgh 2011                            |
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
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.timer;

import java.text.NumberFormat;

import uk.ac.ed.epcc.webapp.Version;

/** Simple region times
 * 
 * @author spb
 *
 */


public class Timer implements Comparable<Timer>{
   private long calls;
   private long time;
   private long start;
   private long depth;
   private String name;
   public Timer(String name){
	   this.name=name;
   }
   public synchronized void start(){
	   if( depth == 0){
		   // don't restart if recursive call
		   start = System.currentTimeMillis();
	   }
	   depth++;
	   calls++;
   }
   public synchronized void stop(){
	   depth--;
	   if( depth == 0 ){
		   time += System.currentTimeMillis() - start;
	   }
   }
   /** stop all timer nest
    * 
    */
   public synchronized void terminate(){
	   if( depth > 0){
		   depth=0;
		   time += System.currentTimeMillis() - start;
	   }
   }
   public long getTime(){
	   if( depth == 0 ){
	      return time;
	   }
	   return time + (System.currentTimeMillis()-start);
   }
   public long getCalls(){
	   return calls;
   }
   public String getStats(){
	   return "calls: "+calls+" depth: "+depth+" time: "+formatTime(getTime())+" name: "+name;
   }
   private static NumberFormat  ms_format=null;
   public String formatTime(long time){
	  long micro = time % 1000;
	  time = time /1000;
	  long sec = time % 60;
	  time = time/60;
	  long min = time %60;
	  time = time/60;
	  if( ms_format == null ){
		  ms_format = NumberFormat.getInstance();
		  ms_format.setMinimumIntegerDigits(3);
	  }
	  return ""+time+":"+min+":"+sec+"."+ms_format.format(micro);
	  
	  
   }
public int compareTo(Timer o) {
	long time1 = getTime();
	long time2 = o.getTime();
    if( time1 < time2 ){
    	return -2;
    }
    if( time1 > time2 ){
    	return 2;
    }
	return name.compareTo(o.name);
}

}