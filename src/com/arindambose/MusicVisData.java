package com.arindambose;
import java.util.ArrayList;

import com.arindambose.MainPApplet.pt;

import ddf.minim.analysis.*;

public class MusicVisData {
	
	FFT fft;
	BeatDetect bDet;
	
	pt[] fftSamples;
	
	ArrayList<pt> localMaxSamples;
	
	public static final int numSamples = 384;
	
	public static final int lnmsWindowSize = 6;
	
	private static MusicVisData instance;
	
	public static MusicVisData getInstance(){
		if(instance == null) instance = new MusicVisData();
		return instance;
	}
	
	MusicVisData(){
		songChanged();
		bDet = new BeatDetect();

	}
	
	//If the song changes (after each loop) reset buffers for the fft
	public void songChanged(){
		fft = new FFT(MusicPlayer.getInstance().getBufferSize(), MusicPlayer.getInstance().getSampleRate());
		fftSamples = new pt[numSamples];
		localMaxSamples = new ArrayList<pt>();
		
		for(int i = 0; i < fftSamples.length; i++){
			fftSamples[i] = MainPApplet.Instance.P(0,0);
		}
	}
	
	public void updateBars(){
		fft.forward(MusicPlayer.getInstance().getBuffer());
		
		bDet.detect(MusicPlayer.getInstance().getBuffer());

		for(int i= 0; i < fftSamples.length; i++){

			float value = (float)  fft.getBand(i)/(float)(numSamples);
			float valueclamped = (value < 0.0001 || !MusicPlayer.getInstance().isPlaying())? 0 : value * 50;
			fftSamples[i].x = i;
			fftSamples[i].y = valueclamped;

		}
		
		
		//Perform local non-maximal supression to keep local maximas only
		localMaxSamples.clear();
		
		for(int i = lnmsWindowSize/2; i < fftSamples.length - lnmsWindowSize/2 ; i++){
			pt currPt = fftSamples[i];
			
			//Check slopes on left size
			boolean isLeftSideSmall = true;
			for( int li = i - lnmsWindowSize/2; li < i ; li++){
				if( MainPApplet.Instance.slope(fftSamples[li], currPt) < 0){
					isLeftSideSmall = false;
					break;
				}
			}
			
			//Check slopes on right size
			boolean isRightSideSmall = true;
			for( int li = i + 1; li <= i + lnmsWindowSize/2 ; li++){
				if( MainPApplet.Instance.slope(fftSamples[li], currPt) > 0){
					isRightSideSmall = false;
					break;
				}
			}
			
			if( isLeftSideSmall && isRightSideSmall){
				localMaxSamples.add(currPt);
			}
		}
		
		
	}
	
	public pt getSample(int i){
		return fftSamples[i];
	}
	
	public int getNumLocalMax(){
		return localMaxSamples.size();
	}
	public pt getLocalMaxSample(int i){
		return localMaxSamples.get(i);
	}
	
	
}
