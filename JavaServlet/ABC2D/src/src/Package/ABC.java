package src.Package;

import java.util.Random;

public class ABC {
	private Random rand = new Random();
	private double x,y,xAlt,xUst,yAlt,yUst,f,fitness,prob,trial;

    public ABC(double xAlt, double xUst, double yAlt, double yUst) {
    	this.x = xAlt+(rand.nextDouble()*(xUst - xAlt));
    	this.y = yAlt+(rand.nextDouble()*(yUst - yAlt));
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getxAlt() {
		return xAlt;
	}

	public void setxAlt(double xAlt) {
		this.xAlt = xAlt;
	}

	public double getxUst() {
		return xUst;
	}

	public void setxUst(double xUst) {
		this.xUst = xUst;
	}

	public double getyAlt() {
		return yAlt;
	}

	public void setyAlt(double yAlt) {
		this.yAlt = yAlt;
	}

	public double getyUst() {
		return yUst;
	}

	public void setyUst(double yUst) {
		this.yUst = yUst;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public double getTrial() {
		return trial;
	}

	public void setTrial(double trial) {
		this.trial = trial;
	}

	
	
    
}
