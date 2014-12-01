package main;

public class Fscore {

	private Integer truePositive = 0;
	private Integer trueNegative = 0;
	
	private Integer falsePositive = 0;
	private Integer falseNegative = 0;
	

	public void incrementFalsePositive(){
		this.falsePositive = this.falsePositive++;
	}

	
	public void incrementFalseNegativ(){
		this.falseNegative = this.falseNegative++;
	}
	
	public void incrementTrueNegativ(){
		this.trueNegative = this.trueNegative++;
	}
	
	
	public void incrementTruePositive(){
		this.truePositive = this.truePositive++;
	}
	
	
	
	public Integer computePrecision(){
		
		Integer precision = this.truePositive/(this.truePositive+this.falsePositive);
		
		return precision;
		
	}


	
	public static Integer computeAccuracy(Integer trueP, Integer falseP,Integer trueN,Integer falseN){
		
		Integer accuracy= (trueP+falseP)/(trueP+falseP+trueN+falseN);
		
		return accuracy;
		
	}
	

	public static Integer computeRecall(Integer trueP, Integer falseN){
		
		Integer recall = trueP/(trueP+falseN);
		
		return recall;
		
	}



	public Integer getFalsePositive() {
		return falsePositive;
	}



	public void setFalsePositive(Integer falsePositive) {
		this.falsePositive = falsePositive;
	}



	public Integer getFalseNegative() {
		return falseNegative;
	}



	public void setFalseNegative(Integer falseNegative) {
		this.falseNegative = falseNegative;
	}



	public Integer getTrueNegative() {
		return trueNegative;
	}



	public void setTrueNegative(Integer trueNegative) {
		this.trueNegative = trueNegative;
	}



	public Integer getTruePositiv() {
		return truePositive;
	}



	public void setTruePositiv(Integer truePositiv) {
		this.truePositive = truePositiv;
	}






}

