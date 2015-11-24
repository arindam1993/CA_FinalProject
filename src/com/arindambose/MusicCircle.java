package com.arindambose;

import com.arindambose.MainPApplet.pt;
import com.arindambose.MainPApplet.vec;



public class MusicCircle {
	
	public MainPApplet pApp;
	
	private float radius;
	private pt center;
	private int numDivisions;
	private int numSemitones;
	


	private int numNotes;
	private float baseSemitone;
	private int noteCount;
	
	private float phraseDuration;
	private vec playLine;
	private float sectionDuration;
	
	private boolean isMuted;
	
	private pt leftArrowCenter;
	private pt rightArrowCenter;
	
	//Buffers which store notes
	float T[]; // times at which the notes start
	float D[]; //duration of each note
	float S[]; //Semitone of each note
	
	static final float initOffset = 50.0f;
	static final float thicknessOffset = 2.0f;
	static final float clipDuration = 4.0f;
	
	public MusicCircle(float radius, pt center, int numDivisions, int numSemitones, float baseSemitone){
		pApp = MainPApplet.Instance;
		
		this.radius = radius;
		this.center = center;
		this.numDivisions = numDivisions;
		this.numSemitones = numSemitones;

		this.baseSemitone = baseSemitone;
	
		calcParams();
		
		isMuted = false;
		
	}
	
	public void render(){
		
		pApp.noFill();
		if (!isMuted())	pApp.pen(Color.sand, 2.0f);
		else pApp.pen(Color.red, 2.0f);
		pApp.ellipse(center.x, center.y, 2 * this.radius, 2 * this.radius);
		
		
		float cellHeight = getCellHeight();
		float cellRadians = getCellRadians();
		
		//Draw semitone level
		float drawRadius = cellHeight + initOffset;
		pApp.pen(Color.grey, 1.0f);
		for(int i=0; i<this.numSemitones - 1; i++){
			pApp.ellipse(center.x, center.y, 2 * drawRadius, 2 * drawRadius);
			drawRadius+=cellHeight;
		}
		
		//Draw play line
		if(MusicPlayer.getInstance().isPlaying() && !isMuted()){
			pApp.pen(Color.red, 1.0f);
			float angle = getCurrentPlayRadian();
			playLine.rotateTo(angle);
			if(angle > 2 * Math.PI){
				MusicPlayer.getInstance().stopPlaying();
				MusicPlayer.getInstance().startPlaying();
			}
			pApp.line(this.center.x, this.center.y, this.center.x + playLine.x, this.center.y + playLine.y);
		}
		
		pApp.scribe("Octave: "+ Lookups.getOctaveLabel(baseSemitone), this.center.x - 25, this.center.y + this.radius +25);
		
		
		//Draw lines seperating each semitone
		pApp.pen(Color.grey, 1.0f);
		vec startLine = pApp.V(this.radius, 0);
		for(int i = 0; i<this.numDivisions ; i++){
			pApp.line(this.center.x, this.center.y, this.center.x + startLine.x, this.center.y + startLine.y);
			startLine.rotateBy(cellRadians);
		}
		
		//Draw all notes
		for(int i=0; i<noteCount ; i++){
			drawNote(S[i], D[i], T[i]);
		}
		
		//Center of circle
		pApp.fill(Color.sand);
		pApp.pen(Color.sand, 1.0f);
		pApp.ellipse(center.x, center.y, 2 * initOffset, 2 * initOffset);
		
		//Draw Arrows 
		pApp.fill(Color.black);
		pt leftArrowCenter = getLeftArrowCenter();
		pApp.show(pApp.P(leftArrowCenter.x - 10, leftArrowCenter.y), 
					pApp.P(leftArrowCenter.x , leftArrowCenter.y + 10), 
					pApp.P(leftArrowCenter.x , leftArrowCenter.y - 10));
		pt rightArrowCenter = getRightArrowCenter();
		pApp.show(pApp.P(rightArrowCenter.x + 10, rightArrowCenter.y), 
					pApp.P(rightArrowCenter.x , rightArrowCenter.y + 10), 
					pApp.P(rightArrowCenter.x , rightArrowCenter.y - 10));
		
		
		if(pApp.mousePressed){
			pt mousePt = pApp.P(pApp.mouseX,pApp.mouseY);
			//If the mouse click is within the right quadrant
			if ( pApp.d(center, mousePt) < this.radius && pApp.d(center, mousePt) > initOffset){
				vec pick = pApp.V(this.center, mousePt);	
				float semitone = getSemitoneFromDistance(pick.norm() - initOffset + 1);

				//Draw overlay for note
				pApp.pen(Color.black, 0.5f);
				pApp.fill(Color.white);		
				pApp.ellipse(pApp.mouseX - 10, pApp.mouseY - 10, 20, 20);
				pApp.scribe(Lookups.getSemitoneLabel(semitone), pApp.mouseX - 15, pApp.mouseY - 5);
			}
		}
	}
	
	
	
	private float getCellHeight() {
		return (this.radius - initOffset)/this.numSemitones;
	}
	
	private float getCellRadians(){
		return (float) (2 * Math.PI/this.numDivisions);
	}
	
	public float getCurrentPlayRadian(){
		float t = MusicPlayer.getInstance().getPlayTime();
		return (float) ((t/phraseDuration)* 2 * Math.PI);
	}
	
	//Use relative semitones here( start from 1 )
	private  void drawNote(float semitone, float duration, float startTime){
		float semitoneAngleStart = startTime * getCellRadians();
		float semitoneAngleEnd = (startTime + duration) * getCellRadians();
		float semitoneRadius = initOffset + (semitone - 1) * getCellHeight() + getCellHeight()/2;
		
		if(semitone != MusicPlayer.SILENCE){		//Draw the arc
			pApp.noFill();
			pApp.pen(Color.grey, getCellHeight() - thicknessOffset);
			pApp.arc(this.center.x, this.center.y, 2 * semitoneRadius, 2 * semitoneRadius, semitoneAngleStart, semitoneAngleEnd);
		}
		
	}
	
	public void addNote(float semitone , float duration, float startTime){
		S[noteCount] = semitone;
		D[noteCount] = duration;
		T[noteCount] = startTime;
		
		noteCount++;
	}
	
	//Call In mousedragged()
	public void move(int x, int y){
		if(Math.sqrt((x - center.x)*(x - center.x) + (y - center.y)*(y - center.y)) < initOffset){
			center.x = x;
			center.y = y;
		}
	}
	
	//Call In mouseDragged()
	public void onClicked(int x, int y){
		pt mousePt = pApp.P(x,y);
		if ( pApp.d(center, mousePt) < initOffset){
			toggleMute();
			MusicPlayer.getInstance().reset();
		}else if( pApp.d(center, mousePt) > initOffset && pApp.d(center, mousePt) < radius){
			vec pick = pApp.V(this.center, mousePt);
			float pickAngle = pApp.positive(pick.angle());
			
			float semitone = getSemitoneFromDistance(pick.norm() - initOffset + 1);
			int section = getSectionFromAngle(pickAngle);
	
			addOrRemoveNote(section, semitone);
		}else if(pApp.d(getLeftArrowCenter(), mousePt) < 10){
			//Left arrow is clicked
			if(this.numDivisions > 4){
				this.numDivisions /= 2;
				this.setNumDivisions(this.numDivisions);
			}
		}else if(pApp.d(getRightArrowCenter(), mousePt) < 10){
			if (this.numDivisions < 32){
				this.numDivisions *= 2;
				this.setNumDivisions(this.numDivisions);
			}
		}
	}
	
	//Call in mousedragged()
	public void changeSemitone(int x, int y){
		pt mousePt = pApp.P(x,y);
		//If the mouse click is within the right quadrant
		if ( pApp.d(center, mousePt) < this.radius && pApp.d(center, mousePt) > initOffset){
			vec pick = pApp.V(this.center, mousePt);
			float pickAngle = pApp.positive(pick.angle());
			
			float semitone = getSemitoneFromDistance(pick.norm() - initOffset + 1);
			int section = getSectionFromAngle(pickAngle);
	
			editNoteInSection(section, semitone);
			
		}
	}
	
	//Call in mousewheel
	public void changeOctave(int x, int y, float direction){
		pt mousePt = pApp.P(x,y);
		if( pApp.d(mousePt, this.center) < initOffset){
			this.baseSemitone += direction * 12;
			if( this.baseSemitone > 12) this.baseSemitone = 12;
			else if( this.baseSemitone < -12) this.baseSemitone = -12;
		}
		
	}
	
	
	private int getSectionFromAngle(float angle){
		return (int) (angle/getCellRadians());
	}
	
	private float getSemitoneFromDistance(float distance){
		float semitone =  (float) Math.floor(distance/getCellHeight());
		if(semitone < 1)return 1.0f;
		return semitone;
	}
	
	private void editNoteInSection(int section, float semitone){
		float startTime = section * sectionDuration * (numDivisions / phraseDuration);
		//Change existing note if one is found
		for( int i = 0; i < noteCount ; i++){
			if( T[i] == startTime && S[i] != MusicPlayer.SILENCE){
				S[i] = semitone;
				D[i] = sectionDuration * (numDivisions / phraseDuration);		
				return;
			}
		}	
		//If no note exists  add a note
//		addNote(semitone, sectionDuration * (numDivisions / phraseDuration), startTime);
//		System.out.println("Adding: "+ semitone +" " + sectionDuration + " " +startTime);
	}
	
	private void addOrRemoveNote(int section, float semitone){
		float startTime = section * sectionDuration * (numDivisions / phraseDuration);
		//Remove note if one it exists
		for( int i = 0; i < noteCount ; i++){
			if( T[i] == startTime && S[i] != MusicPlayer.SILENCE){
				S[i] = MusicPlayer.SILENCE;
				D[i] = sectionDuration * (numDivisions / phraseDuration);		
				return;
			}
		}	
		
		//Else add the note
		addNote(semitone, sectionDuration * (numDivisions / phraseDuration), startTime);
	}
	
	public void toggleMute(){
		isMuted = !isMuted;
	}
	
	private pt getLeftArrowCenter(){
		return pApp.P(this.center.x - this.radius - 25 , this.center.y);
	}
	
	private pt getRightArrowCenter(){
		return pApp.P(this.center.x + this.radius + 25 , this.center.y);
	}
	
	
	/********Getters and Setters********/
	
	public int getNumNotes() {
		return numNotes;
	}

	public float getBaseSemitone() {
		return baseSemitone;
	}

	public float getT(int index) {
		return T[index] * this.sectionDuration;
	}

	public float getD(int index) {
		return D[index] * this.sectionDuration;
	}

	public float getS(int index) {
		return S[index];
	}
	
	public boolean isMuted(){
		return isMuted;
	}

	public void setNumDivisions(int div){
		this.numDivisions = div;
		calcParams();
	}
	
	private void calcParams(){
		this.numNotes = this.numSemitones * this.numDivisions;
		
		//Initialize music buffers (Semitone buffers)
		noteCount = 0;
		T = new float[this.numNotes];
		D = new float[this.numNotes];
		S = new float[this.numNotes];
		
		phraseDuration = clipDuration;
		sectionDuration = this.phraseDuration/(float)(this.numDivisions);
		playLine = pApp.V(this.radius, 0);
		
	}

}
