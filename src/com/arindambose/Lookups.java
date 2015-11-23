package com.arindambose;

import java.util.HashMap;

public class Lookups {
	
	public static HashMap<Float,String> octaveLabelLookup;
	
	public static HashMap<Float, String> semitoneLabelLookup;
	
	private static void initOctaveLabels(){
		octaveLabelLookup = new HashMap<Float, String>();
		octaveLabelLookup.put(-12f, "1st");
		octaveLabelLookup.put(0f, "2nd");
		octaveLabelLookup.put(12f, "3rd");
	}
	
	private static void initSemitoneLabels(){
		semitoneLabelLookup = new HashMap<Float, String>();
		semitoneLabelLookup.put(1f, "C");
		semitoneLabelLookup.put(2f, "C#");
		semitoneLabelLookup.put(3f, "D");
		semitoneLabelLookup.put(4f, "D#");
		semitoneLabelLookup.put(5f, "E");
		semitoneLabelLookup.put(6f, "F");
		semitoneLabelLookup.put(7f, "F#");
		semitoneLabelLookup.put(8f, "G");
		semitoneLabelLookup.put(9f, "G#");
		semitoneLabelLookup.put(10f, "A");
		semitoneLabelLookup.put(11f, "A#");
		semitoneLabelLookup.put(12f, "B");
	}
	
	public static String getOctaveLabel(float base){
		if(octaveLabelLookup == null) initOctaveLabels();
		return octaveLabelLookup.get(base);
	}
	
	public static String getSemitoneLabel(float semitone){
		if(semitoneLabelLookup == null)	initSemitoneLabels();
		return semitoneLabelLookup.get(semitone);
	}
}
