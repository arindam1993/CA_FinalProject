package com.arindambose;
import com.arindambose.MainPApplet.pt;

import ddf.minim.analysis.*;

public class MusicVisData {
	
	FFT fft;
	BeatDetect bDet;
	
	pt[] fftSamples;
	
	public static final int numSamples = 384;
	
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
	}
	
	public pt getSample(int i){
		return fftSamples[i];
	}
}
