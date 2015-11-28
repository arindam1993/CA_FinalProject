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
	
	//Buffers to store subdivision stages
	private ArrayList<pt> refineSide1Buf;
	private ArrayList<pt> tuckSide1Buf;
	private ArrayList<pt> untuckSide1Buf;
	
	private ArrayList<pt> refineSide1Buf2;
	private ArrayList<pt> tuckSide1Buf2;
	private ArrayList<pt> untuckSide1Buf2;
	
	private ArrayList<pt> refineSide1Buf3;
	private ArrayList<pt> tuckSide1Buf3;
	private ArrayList<pt> untuckSide1Buf3;
	
	
	private ArrayList<pt> smoothSide2Pts;
	
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
		
		refineSide1Buf = new ArrayList<pt>();
		tuckSide1Buf = new ArrayList<pt>();
		untuckSide1Buf = new ArrayList<pt>();
		
		refineSide1Buf2 = new ArrayList<pt>();
		tuckSide1Buf2 = new ArrayList<pt>();
		untuckSide1Buf2 = new ArrayList<pt>();
		
		refineSide1Buf3 = new ArrayList<pt>();
		tuckSide1Buf3 = new ArrayList<pt>();
		untuckSide1Buf3 = new ArrayList<pt>();
		
		
		smoothSide2Pts = new ArrayList<pt>();
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
			
			if( Math.abs(sample.y - currHeight) > 5){
			
				side1CntrlPts[(int)(sample.x)].x = baseX + ( this.width * sample.x/ (float)(MusicVisData.numSamples));
				side1CntrlPts[(int)(sample.x)].y = baseY - sample.y ;
				
				
				side2CntrlPts[(int)(sample.x)].x = baseX + ( this.width * sample.x/MusicVisData.numSamples);
				side2CntrlPts[(int)(sample.x)].y = baseY + sample.y ;
			}
		}
		
		//Deflate all the points
		for (int i = 0 ; i < side1CntrlPts.length ; i++){
			float height = side2CntrlPts[i].y - this.startPt.y;
			
			height *= 0.95;
			
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
	
	private void calcSmoothPoints(){
		

		
		/** First Smoothening pass **/
		//Aliases
		ArrayList<pt> targetBuf = finalSide1Pts;
		ArrayList<pt> refineBuf = refineSide1Buf;
		ArrayList<pt> tuckBuf = tuckSide1Buf;
		ArrayList<pt> untuckBuf = untuckSide1Buf;
		refineBuf.clear();
		tuckBuf.clear();
		untuckBuf.clear();
		//Refine Step
		if( targetBuf.size() > 0){		
			refineBuf.add(startPt);
			refineBuf.add(pApp.P(startPt, targetBuf.get(0)));
			for( int i =0; i < targetBuf.size() - 1 ; i++){
				pt curr = targetBuf.get(i);
				pt next = targetBuf.get(i + 1);
				
				refineBuf.add(curr);
				refineBuf.add(pApp.P(curr, next));
			}
			refineBuf.add(targetBuf.get( targetBuf.size() - 1));
			refineBuf.add(pApp.P(targetBuf.get( targetBuf.size() - 1), endPt));
			refineBuf.add(endPt);
			
			
			
			//Tuck Step
			tuckBuf.add(refineBuf.get(0));
			float tuckFactor = 0.5f;
			for(int i = 1; i < refineBuf.size() - 1 ; i++){
				pt left = refineBuf.get(i-1);
				pt right = refineBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec tuckvec = pApp.V(refineBuf.get(i), avg).scaleBy(tuckFactor);
				
				tuckBuf.add(pApp.P(refineBuf.get(i), tuckvec));		
			}
			tuckBuf.add(refineBuf.get(refineBuf.size() - 1));
			
			//Untuck Step
			untuckBuf.add(tuckBuf.get(0));
			float untuckFactor = 0.25f;
			for(int i = 1; i < tuckBuf.size() - 1 ; i++){
				pt left = tuckBuf.get(i-1);
				pt right = tuckBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec untuckvec = pApp.V(tuckBuf.get(i), avg).scaleBy( - 1 * untuckFactor);
				
				untuckBuf.add(pApp.P(tuckBuf.get(i), untuckvec));		
			}
			untuckBuf.add(tuckBuf.get(tuckBuf.size() - 1));
		}
		
		
		
		/**Second Smoothening pass**/
		targetBuf = untuckSide1Buf;
		refineBuf = refineSide1Buf2;
		tuckBuf = tuckSide1Buf2;
		untuckBuf = untuckSide1Buf2;
		refineBuf.clear();
		tuckBuf.clear();
		untuckBuf.clear();
		//Refine Step
		if( targetBuf.size() > 0){		
			refineBuf.add(startPt);
			refineBuf.add(pApp.P(startPt, targetBuf.get(0)));
			for( int i =0; i < targetBuf.size() - 1 ; i++){
				pt curr = targetBuf.get(i);
				pt next = targetBuf.get(i + 1);
				
				refineBuf.add(curr);
				refineBuf.add(pApp.P(curr, next));
			}
			refineBuf.add(targetBuf.get( targetBuf.size() - 1));
			refineBuf.add(pApp.P(targetBuf.get( targetBuf.size() - 1), endPt));
			refineBuf.add(endPt);
			
			
			
			//Tuck Step
			tuckBuf.add(refineBuf.get(0));
			float tuckFactor = 0.5f;
			for(int i = 1; i < refineBuf.size() - 1 ; i++){
				pt left = refineBuf.get(i-1);
				pt right = refineBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec tuckvec = pApp.V(refineBuf.get(i), avg).scaleBy(tuckFactor);
				
				tuckBuf.add(pApp.P(refineBuf.get(i), tuckvec));		
			}
			tuckBuf.add(refineBuf.get(refineBuf.size() - 1));
			
			//Untuck Step
			untuckBuf.add(tuckBuf.get(0));
			float untuckFactor = 0.25f;
			for(int i = 1; i < tuckBuf.size() - 1 ; i++){
				pt left = tuckBuf.get(i-1);
				pt right = tuckBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec untuckvec = pApp.V(tuckBuf.get(i), avg).scaleBy( - 1 * untuckFactor);
				
				untuckBuf.add(pApp.P(tuckBuf.get(i), untuckvec));		
			}
			untuckBuf.add(tuckBuf.get(tuckBuf.size() - 1));
		}
		
		/**Third Smoothening pass**/
		targetBuf = untuckSide1Buf2;
		refineBuf = refineSide1Buf3;
		tuckBuf = tuckSide1Buf3;
		untuckBuf = untuckSide1Buf3;
		refineBuf.clear();
		tuckBuf.clear();
		untuckBuf.clear();
		//Refine Step
		if( targetBuf.size() > 0){		
			refineBuf.add(startPt);
			refineBuf.add(pApp.P(startPt, targetBuf.get(0)));
			for( int i =0; i < targetBuf.size() - 1 ; i++){
				pt curr = targetBuf.get(i);
				pt next = targetBuf.get(i + 1);
				
				refineBuf.add(curr);
				refineBuf.add(pApp.P(curr, next));
			}
			refineBuf.add(targetBuf.get( targetBuf.size() - 1));
			refineBuf.add(pApp.P(targetBuf.get( targetBuf.size() - 1), endPt));
			refineBuf.add(endPt);
			
			
			
			//Tuck Step
			tuckBuf.add(refineBuf.get(0));
			float tuckFactor = 0.5f;
			for(int i = 1; i < refineBuf.size() - 1 ; i++){
				pt left = refineBuf.get(i-1);
				pt right = refineBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec tuckvec = pApp.V(refineBuf.get(i), avg).scaleBy(tuckFactor);
				
				tuckBuf.add(pApp.P(refineBuf.get(i), tuckvec));		
			}
			tuckBuf.add(refineBuf.get(refineBuf.size() - 1));
			
			//Untuck Step
			untuckBuf.add(tuckBuf.get(0));
			float untuckFactor = 0.25f;
			for(int i = 1; i < tuckBuf.size() - 1 ; i++){
				pt left = tuckBuf.get(i-1);
				pt right = tuckBuf.get(i+1);
				pt avg = pApp.P(left, right);
				vec untuckvec = pApp.V(tuckBuf.get(i), avg).scaleBy( - 1 * untuckFactor);
				
				untuckBuf.add(pApp.P(tuckBuf.get(i), untuckvec));		
			}
			untuckBuf.add(tuckBuf.get(tuckBuf.size() - 1));
		}
	}
	

	
	public void render(){
		updateCntrlPoints();
		setFinalPts();
		calcSmoothPoints();
		pApp.pen(Color.blue, 3.0f);
		pApp.fill(Color.purple);
		
		
//		pApp.beginShape();
//		
//		pApp.vertex(startPt.x, startPt.y);
//		for(int i = 0 ; i < finalSide1Pts.size(); i++){
//			pApp.vertex(finalSide1Pts.get(i).x, finalSide1Pts.get(i).y);
//		}
//		pApp.vertex(endPt.x, endPt.y);
//		
//				
////		pApp.vertex(endPt.x, endPt.y);
//		for(int i = 0 ; i < finalSide2Pts.size(); i++){
//				pApp.vertex(finalSide2Pts.get(i).x, finalSide2Pts.get(i).y);
//		}
//		pApp.vertex(startPt.x, startPt.y);
//		
//		pApp.endShape(PConstants.OPEN);
		
		
		pApp.pen(Color.red, 3.0f);
		pApp.fill(Color.yellow);
		//Render the smooth points
		pApp.beginShape();
		for(int i =0; i<untuckSide1Buf3.size() ; i++){
			pt curr = untuckSide1Buf3.get(i); 
			pApp.vertex(curr.x, curr.y);
		}
		pApp.endShape(PConstants.OPEN);
		
		pApp.beginShape();
		for(int i =0; i<untuckSide1Buf3.size() ; i++){
			
			pt curr = untuckSide1Buf3.get(i); 
			float height = startPt.y - curr.y;
			pApp.vertex(curr.x, curr.y + 2*height);
		}
		pApp.endShape(PConstants.OPEN);
		
	}
}
