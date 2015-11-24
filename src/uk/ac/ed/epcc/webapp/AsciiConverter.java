// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;
import java.text.Collator;
@uk.ac.ed.epcc.webapp.Version("$Id: AsciiConverter.java,v 1.7 2014/09/15 14:30:11 spb Exp $")

public class AsciiConverter {

	private final Collator coll; 
	private final boolean allow_7bit;
	public AsciiConverter(){
		this(true);
	}
	public AsciiConverter(boolean allow_7bit){
		this.allow_7bit=allow_7bit;
	   coll= Collator.getInstance();
	   coll.setStrength(Collator.PRIMARY);
	}
	char target[] = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	public char convert(char input){
		if( allow_7bit && input < 128){
			// 7 bit ascii
			return input;
		}
		for(char c : target){
			if( 0 == coll.compare(Character.toString(c),Character.toString(input))){
				return c;
			}
		}
		return ' ';
	}
	public String convert(String input){
		StringBuilder sb = new StringBuilder();
		if( input != null ){
			for( char c : input.toCharArray()){
				sb.append(convert(c));
			}
		}
		return sb.toString();
	}
}