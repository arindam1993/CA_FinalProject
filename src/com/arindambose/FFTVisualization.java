package com.arindambose;

import java.util.ArrayList;

import com.arindambose.MainPApplet.*;

import processing.core.PConstants;

public class FFTVisualization {
	
	
	private float width;
	private float yHeight;
	
	private pt startPt;
	private pt endPt;
	private pt[] side1CntrlPts;
	private pt[] side2CntrlPts;
	
	private ArrayList<pt> finalSide1Pts;
	private ArrayList<pt> finalSide2Pts;
	
	private MainPApplet pApp;
	
	public FFTVisualization(float width, float yHeight){
		this.width = width;
		this.yHeight = yHeight;
		
		pApp = MainPApplet.Instance;
		startPt = pApp.P(0, this.yHeight);
		endPt = pApp.P(width, this.yHeight);
		
		side1CntrlPts = new pt[MusicVisData.numSamples];
		side2CntrlPts = new pt[MusicVisData.numSamples];
		
		for(int i = 0 ; i < side1CntrlPts.length ; i++){
			side1CntrlPts[i] = pApp.P(this.width * i/MusicVisData.numSamples, startPt.y);
		}
		
		for(int i = 0 ; i < side1CntrlPts.length ; i++){
			side2CntrlPts[i] = pApp.P(this.width * i/MusicVisData.numSamples, startPt.y);;
		}
		
		finalSide1Pts = new ArrayList<pt>();
		finalSide2Pts = new ArrayList<pt>();
		updateCntrlPoints();
	}
	
	
	//Call before drawing draw()
	private void updateCntrlPoints(){
		float baseX = startPt.x;
		float baseY = startPt.y;
		
		for(int i = 0; i<MusicVisData.getInstance().getNumLocalMax() ; i++){
			pt sample = MusicVisData.getInstance().getLocalMaxSample(i);
			System.out.println(sample.x + "   " + sample.y);
			
			float currHeight = side2CntrlPts[(int)(sample.x)].y - baseY;
			
			if( sample.y - currHeight > 5){
			
				side1CntrlPts[(int)(sample.x)].x = baseX + ( this.width * sample.x/ (float)(MusicVisData.numSamples));
				side1CntrlPts[(int)(sample.x)].y = baseY - sample.y ;
				
				
				side2CntrlPts[(int)(sample.x)].x = baseX + ( this.width * sample.x/MusicVisData.numSamples);
				side2CntrlPts[(int)(sample.x)].y = baseY + sample.y ;
			}
		}
		
		//Deflate all the points
		for (int i = 0 ; i < side1CntrlPts.length ; i++){
			float height = side2CntrlPts[i].y - this.startPt.y;
			
			height *= 0.85;
			
			side1CntrlPts[i].y = startPt.y - height;
			side2CntrlPts[i].y = startPt.y + height;
		}
	}
	
	
	private void setFinalPts(){
		finalSide1Pts.clear();
		finalSide2Pts.clear();
		for(int i = 0 ; i < side1CntrlPts.length; i++){
			float height = side2CntrlPts[i].y - startPt.y;
			if( height > 2.0f)
				finalSide1Pts.add(side1CntrlPts[i]);
		}
		
		for(int i = side2CntrlPts.length - 1 ; i >= 0 ; i--){
			float height = side2CntrlPts[i].y - startPt.y;
			if( height > 2.0f)
				finalSide2Pts.add(side2CntrlPts[i]);
		}
		
		
		
	}
	

	
	public void render(){
		updateCntrlPoints();
		setFinalPts();
		pApp.pen(Color.blue, 3.0f);
		pApp.fill(Color.purple);
		
		
		pApp.beginShape();
		
		pApp.vertex(startPt.x, startPt.y);
		for(int i = 0 ; i < finalSide1Pts.size(); i++){
			pApp.vertex(finalSide1Pts.get(i).x, finalSide1Pts.get(i).y);
		}
		pApp.vertex(endPt.x, endPt.y);
		
				
//		pApp.vertex(endPt.x, endPt.y);
		for(int i = 0 ; i < finalSide2Pts.size(); i++){
				pApp.vertex(finalSide2Pts.get(i).x, finalSide2Pts.get(i).y);
		}
		pApp.vertex(startPt.x, startPt.y);
		
		pApp.endShape(PConstants.CLOSE);
		
		
	}
}
