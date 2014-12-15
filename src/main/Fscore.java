package main;

public class Fscore {

	private int truePositive;
	private int trueNegative;
	
	private int falsePositive;
	private int falseNegative;
	
	// 	prevelance hinzufuegen
	public void incrementFalsePositive(){
		falsePositive = falsePositive+1;
	}

	
	public void incrementFalseNegativ(){
		falseNegative = falseNegative+1;
	}
	
	public void incrementTrueNegativ(){
		trueNegative = trueNegative+1;
	}
	
	
	public void incrementTruePositive(){
		truePositive = truePositive+1;
	}
	
	
	
	public float computePrecision(){
		
		float precision = truePositive/(float) (truePositive+falsePositive);
		
		return precision;
		
	}


	
	public float computeAccuracy(){
		
		Float accuracy= (truePositive+falsePositive)/ (float) (truePositive+falsePositive+trueNegative+falseNegative);
		
		return accuracy;
		
	}
	

	public  float computeRecall(){
		
		float recall = truePositive/ (float) (truePositive+falseNegative);
		
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

	public String toString() {
		return "Accuracy:\t" 	+ computeAccuracy() + 
			   "Precision:\t" 	+ computePrecision() +
			   "Recall:\t\t" 	+ computeRecall();
	}
}

