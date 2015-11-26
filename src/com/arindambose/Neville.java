package com.arindambose;

import com.arindambose.MainPApplet.*;

public class Neville {
	MainPApplet pa = MainPApplet.Instance;
	public static pt  GeneralQuadratic(pt A, float a, pt B, float b, pt C, float c, float t ){
		//UGLY
		return MainPApplet.Instance.L(MainPApplet.Instance.L(A,B ,(t-a)/(b-a)),MainPApplet.Instance.L(B,C,(t-b)/(c-b)), (t-a)/(c-a));
	}
	
	public static float General1D(float A, float a, float B, float b, float C, float c, float t ){
		return L1D(L1D(A,B,(t-a)/(b-a)), L1D(B,C,(t-b)/(c-b)), (t-a)/(c-a));
	}
	
	public static float General1D(float A, float a, float B, float b, float C, float c, float D, float d, float t){
		float f1 = General1D(A,a,B,b,C,c,t);
		float f2 = General1D(B,b,C,c,D,d,t);
		return L1D(f1,f2,(t-a)/(d-a));
	}
	
	public static float Standard1D(float A, float B, float C,float D, float t){
		return General1D(A,0,B,0.25f,C,0.75f,D,1,t);
	}

	
	public static float Standard1D(float A, float B, float C, float t){
		return General1D(A, 0 , B , 0.5f, C , 1, t);
	}
	
	private static float L1D(float a, float b, float t){
		return a + (b -a)*t;
	}
	
	public static pt  GeneralQuadratic(pt A, float a, pt B, float b, pt C, float c, pt D, float d, float t ){
		pt P = GeneralQuadratic(A,a,B,b,C,c,t);
		pt Q = GeneralQuadratic(B,b,C,c,D,d,t);
		return MainPApplet.Instance.L(P,Q ,(t-a)/(d-a));
	}
	
	
	public static vec GeneralQuadraticSLV(vec _A, float a, vec _B, float b, vec _C, float c, float t ){
		float ta = t-a;
		vec A = MainPApplet.Instance.U(_A);
		vec B = MainPApplet.Instance.U(_B);
		vec C = MainPApplet.Instance.U(_C);		
		return MainPApplet.Instance.slerp(MainPApplet.Instance.slerp(A,(t-a)/(b-a),B),(t-a)/(c-a),MainPApplet.Instance.slerp(B,(t-b)/(c-b),C));
	}

	public static vec GeneralQuadraticSLV(vec A, float a, vec B, float b, vec C, float c, vec D, float d, float t ){
		vec P = MainPApplet.Instance.U(GeneralQuadraticSLV(A,a,B,b,C,c,t));
		vec Q = MainPApplet.Instance.U(GeneralQuadraticSLV(B,b,C,c,D,d,t));
		return MainPApplet.Instance.U(MainPApplet.Instance.slerp(P,(t-a)/(d-a),Q));
	}
	
	
	public static vec GeneralQuadraticV(vec _A, float a, vec _B, float b, vec _C, float c, float t ){
		float ta = t-a;
		vec A = MainPApplet.Instance.U(_A);
		vec B = MainPApplet.Instance.U(_B);
		vec C = MainPApplet.Instance.U(_C);		
		return MainPApplet.Instance.L(MainPApplet.Instance.L(A,(t-a)/(b-a),B),(t-a)/(c-a),MainPApplet.Instance.L(B,(t-b)/(c-b),C));
	}

	public static vec GeneralQuadraticV(vec A, float a, vec B, float b, vec C, float c, vec D, float d, float t ){
		vec P = MainPApplet.Instance.U(GeneralQuadraticV(A,a,B,b,C,c,t));
		vec Q = MainPApplet.Instance.U(GeneralQuadraticV(B,b,C,c,D,d,t));
		return MainPApplet.Instance.U(MainPApplet.Instance.L(P,(t-a)/(d-a),Q));
	}
		
	
	public static pt  StandardQuadratic(pt A, pt B, pt C, float t ){
		//UGLY
		return GeneralQuadratic(A, 0, B, 0.5f, C, 1, t );
	}
	
	public static pt  StandardQuadratic(pt A, pt B, pt C, pt D, float t ){
		//UGLY
		return GeneralQuadratic(A, 0, B, 0.25f, C, 0.75f, D, 1, t );
	}
}
