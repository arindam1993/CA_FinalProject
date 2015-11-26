package com.arindambose;

import com.arindambose.MainPApplet.*;

import processing.core.PConstants;

public class FFTVisualization {
	
	
	private float width;
	private float yHeight;
	
	private pt startPt;
	private pt endPt;
	private pt[] side1CntrlPts;
	private pt[] side2CntrlPts;
	
	private MainPApplet pApp;
	
	public FFTVisualization(float width, float yHeight){
		this.width = width;
		this.yHeight = yHeight;
		
		pApp = MainPApplet.Instance;
		startPt = pApp.P(0, yHeight);
		endPt = pApp.P(width, yHeight);
		
		side1CntrlPts = new pt[MusicVisData.numSamples];
		side2CntrlPts = new pt[MusicVisData.numSamples];
		
		for(int i = 0 ; i < side1CntrlPts.length ; i++){
			side1CntrlPts[i] =pApp.P();
		}
		
		for(int i = 0 ; i < side1CntrlPts.length ; i++){
			side2CntrlPts[i] =pApp.P();
		}
		
		updateCntrlPoints();
	}
	
	
	//Call before drawing draw()
	private void updateCntrlPoints(){
		float baseX = startPt.x;
		float baseY = startPt.y;
		
		float incX = this.width/(MusicVisData.numSamples + 1);
		for(int i = 0; i<MusicVisData.numSamples ; i++){
			baseX+=incX;
			side1CntrlPts[i].x = baseX;
			side1CntrlPts[i].y = baseY - MusicVisData.getInstance().getSample(i);
			
			
			side2CntrlPts[i].x = baseX;
			side2CntrlPts[i].y = baseY + MusicVisData.getInstance().getSample(i);
		}
	}

	
	public void render(){
		updateCntrlPoints();
		pApp.pen(Color.blue, 3.0f);
		pApp.fill(Color.purple);
		
		
		pApp.beginShape();
		
		pApp.vertex(startPt.x, startPt.y);
		for(int i = 0 ; i < side1CntrlPts.length ; i++){
			pApp.vertex(side1CntrlPts[i].x, side1CntrlPts[i].y);
		}
		pApp.vertex(endPt.x, endPt.y);
		
				
//		pApp.vertex(endPt.x, endPt.y);
		for(int i = side1CntrlPts.length - 1 ; i >= 0 ; i--){
			pApp.vertex(side2CntrlPts[i].x, side2CntrlPts[i].y);
		}
		pApp.vertex(startPt.x, startPt.y);
		
		pApp.endShape(PConstants.CLOSE);
		
		
	}
}
