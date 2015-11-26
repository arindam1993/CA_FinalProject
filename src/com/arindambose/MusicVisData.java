package com.arindambose;
import ddf.minim.analysis.*;

public class MusicVisData {
	
	FFT fft;
	BeatDetect bDet;
	
	float[] fftSamples;
	
	public static final int numSamples = 25;
	
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
		fftSamples = new float[numSamples];
	}
	
	public void updateBars(){
		fft.forward(MusicPlayer.getInstance().getBuffer());
		
		bDet.detect(MusicPlayer.getInstance().getBuffer());
		
		int numToAverage = 384/numSamples;
		System.out.println("Num to ave: " + numToAverage);
		for(int i= 0; i < fftSamples.length; i++){
			
			//Set on beat
//			if(bDet.isOnset()){
				int start = i * numToAverage;
				int end = start + numToAverage;
				float sum = 0;
				for( int j = start; j < end ; j++){
					sum+= fft.getBand(j);
				}
				float value = (float)  Math.log(sum/(float)(numSamples));
				float valueclamped = (value < 0.0001)? 0 : value * 20;
				if ( Math.abs(valueclamped - fftSamples[i]) > 20)
					fftSamples[i] = valueclamped;
				else	
					fftSamples[i] *=0.95;
//			}else{
//				fftSamples[i] *= 0.95;
//			}
		}
	}
	
	public float getSample(int i){
		return fftSamples[i];
	}
}
