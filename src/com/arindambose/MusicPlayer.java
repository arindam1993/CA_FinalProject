package com.arindambose;


import ddf.minim.*;
import ddf.minim.ugens.*;

public class MusicPlayer {
	
	//Singleton Instance
	private static MusicPlayer Instance;
	
	private boolean isPlaying;
	float F00=220;
	
	//Minim Stuff
	Minim minim;
	AudioOutput out;
	
	private float loopOffset = 0.0f;
	
	public static float SILENCE=1000;
	
	private int playFrameCounter;
	
	public MusicPlayer(){
		minim = new Minim(MainPApplet.Instance);
		isPlaying = false;
		this.playFrameCounter = 0;
		out = minim.getLineOut(Minim.MONO,1024*16); 
	}
	
	public void countFrames(){
		if( isPlaying)	playFrameCounter++;
	}
	
	public float getPlayTime(){
		return (float)(playFrameCounter)/30;
	}
	public static MusicPlayer getInstance(){
		if (Instance == null) Instance = new MusicPlayer();
		return Instance;		
	}
	
	public void togglePlaying(){ 
		if (isPlaying) stopPlaying();
		else startPlaying();
	}
	
	public void startPlaying(){
		if(!isPlaying){
			out = minim.getLineOut(Minim.MONO,1024*16); 
		    playPhrase();
		    isPlaying = true;
		}
	}
	
	public void stopPlaying(){
		if(isPlaying){
			out.close();
			isPlaying = false;
			playFrameCounter = 0;
		}
	}
	
	//Store all Notes from a MusicCircle
	private void getNotes(MusicCircle circle){
		if(! circle.isMuted()){
			for(int i=0; i<circle.getNumNotes(); i++){
				if( circle.getS(i) != SILENCE) note(circle.getT(i) + loopOffset, circle.getD(i), Fofs(circle.getS(i) + circle.getBaseSemitone()));
			}
		}
	}
	
	void playPhrase(){
		
	   out.pauseNotes(); // do not play yet, first put all notes into the play buffer to help synchronization
	   //Get all notes from music circles in PApplet
	   for(MusicCircle musicCircle : MainPApplet.Instance.musicCircles){
		   getNotes(musicCircle);
	   }
	   out.resumeNotes(); // play now
	   
	}
		   
	void note(float start, float duration, float freq){ // adds a note to the play buffer
	   out.playNote(start,duration,freq); 
	 }

	float Fofs(float semitone) {return (float) (F00*Math.pow(2.,semitone/12));} // returns frequency of semitone (which is pitch/12)

	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void reset(){
		if(isPlaying){
			stopPlaying();
			startPlaying();
			MusicVisData.getInstance().songChanged();
		}
	}
	
	public AudioBuffer getBuffer(){
		return out.mix;
	}
	
	public float getSampleRate(){
		return out.sampleRate();
	}
	
	public int getBufferSize(){
		return out.bufferSize();
	}
	public void reLoop(){
		out.setNoteOffset(0.0f);
		playFrameCounter = 0;
	}
	
	
}
