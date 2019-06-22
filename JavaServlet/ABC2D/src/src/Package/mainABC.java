package src.Package;
import java.util.Random;

public class mainABC {

	private Random rand = new Random();
	private int D = 2;
	private int besinSayisi = 100; //100
	private int limit = 150; //100
	private int maxCycle = 1000; //500
	private double objValSol;
	private double fitnessSol;
	private double r;
	private int degisecekIndis,komsuIndis;
	private double globalMin;
    private double[] Beacon1 = new double[2];
    private double[] Beacon2 = new double[2];
    private double[] Beacon3 = new double[2];
    private double[] altLimitler = new double[2];
    private double[] ustLimitler = new double[2];
    private double[] globalParams = new double[D+1];
    private double r1;
    private double r2;
    private double r3;
    private ABC[] besin = new ABC[besinSayisi];
    
    
    public double[] optimization(double[][] beacons, double[] r) {
        Beacon1[0] = beacons[0][0];
        Beacon1[1] = beacons[0][1];

        Beacon2[0] = beacons[1][0];
        Beacon2[1] = beacons[1][1];

        Beacon3[0] = beacons[2][0];
        Beacon3[1] = beacons[2][1];
        

        r1 = r[0];
        r2 = r[1];
        r3 = r[2];
    	//x
    	altLimitler[0] = 0.0;
    	ustLimitler[0] = 12.0;//5.5
    	//y
    	altLimitler[1] = 0.0;
    	ustLimitler[1] = 6.0;//10.0

    	for (int i = 0; i < besinSayisi; i++) {
			besinOlustur(i);
		}
    	/*for(int i =0 ; i< besin.length; i++) {
    		System.out.println(besin[i].getX() + " " + besin[i].getY());
    	}*/
    	globalMin = besin[0].getF();
    	globalParams[0] = besin[0].getX();
    	globalParams[1] = besin[0].getY();
    
    	enIyiCozum();

    	for (int i = 0; i < maxCycle; i++) {
    		IsciAriGonder();
            olasilikHesapla();
            gozcuAriGonder();
            enIyiCozum();
            kasifAriGonder();
		}
    	
    	//System.out.println("min:" + globalMin);
    	//System.out.println();
    	/*for (int i = 0; i < besin.length; i++) {
			System.out.println(besin[i].getTrial());
		}*/
    	globalParams[2] = globalMin;
    	
    	
    	
    	return globalParams;
    }
    
    public void IsciAriGonder(){  
    	
        for (int i = 0; i < besinSayisi; i++) {
        	double[] sol = new double[D];
        	double[] komsu = new double[D];
            
        	//r = rand.nextDouble();
        	r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
        	degisecekIndis = (int)(r * D);
            //r = rand.nextDouble();
            r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
            komsuIndis = (int)(r * besinSayisi);
            //sonradan eklendi
            /*while (komsuIndis == i) {                    
                r = rand.nextDouble();
                komsuIndis = (int)(r * besinSayisi);
            }*/
            
            sol[0] = besin[i].getX();
            sol[1] = besin[i].getY();

            
            komsu[0] = besin[komsuIndis].getX();
            komsu[1] = besin[komsuIndis].getY();

            //r = ((rand.nextDouble()*2)-1);
            r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
            
            sol[degisecekIndis] = sol[degisecekIndis] + ((sol[degisecekIndis] - komsu[degisecekIndis])*(r-0.5)*2);

            if(sol[degisecekIndis] < altLimitler[degisecekIndis]) {
                    sol[degisecekIndis] = altLimitler[degisecekIndis];
            } 
            
            if(sol[degisecekIndis] > ustLimitler[degisecekIndis]){            	
                sol[degisecekIndis] = ustLimitler[degisecekIndis];             
            }
            objValSol = yakinlik(sol[0], sol[1]);
            fitnessSol = calculateFitness(objValSol);
            if (fitnessSol > besin[i].getFitness()) {
                besin[i].setTrial(0);
                besin[i].setX(sol[0]);
                besin[i].setY(sol[1]);

                besin[i].setF(objValSol);
                besin[i].setFitness(fitnessSol);              
            } else {
                besin[i].setTrial(besin[i].getTrial() + 1);
            }
        }
        
    }
    
    public void gozcuAriGonder(){ 
    	
        int i,t;
        i = 0;
        t = 0;
        while (t < besinSayisi) {            
            r = rand.nextDouble();
            if(r < besin[i].getProb()){
            	double[] sol = new double[D];
            	double[] komsu = new double[D];
                t++; 
                //r = rand.nextDouble();
                r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
                degisecekIndis = (int)(r * D);
                //r = rand.nextDouble();
                r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
                komsuIndis = (int)(r * besinSayisi);
                
                while (komsuIndis == i) {                    
                    r = rand.nextDouble();
                    komsuIndis = (int)(r * besinSayisi);
                }
       
                sol[0] = besin[i].getX();
                sol[1] = besin[i].getY();

                komsu[0] = besin[komsuIndis].getX();
                komsu[1] = besin[komsuIndis].getY();

                //r = ((rand.nextDouble()*2)-1);
                r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)));
                sol[degisecekIndis] = sol[degisecekIndis] + ((sol[degisecekIndis] - komsu[degisecekIndis])*(r-0.5)*2);
                
                if(sol[degisecekIndis] < altLimitler[degisecekIndis]) {
                        sol[degisecekIndis] = altLimitler[degisecekIndis];
                } 
                if(sol[degisecekIndis] > ustLimitler[degisecekIndis]){
                    sol[degisecekIndis] = ustLimitler[degisecekIndis];
                }
                objValSol = yakinlik(sol[0], sol[1]);
                fitnessSol = calculateFitness(objValSol);
                    
                if (fitnessSol > besin[i].getFitness()) {
                    besin[i].setTrial(0);
                    besin[i].setX(sol[0]);
                    besin[i].setY(sol[1]);

                    besin[i].setF(objValSol);
                    besin[i].setFitness(fitnessSol);    
                } else {
                    besin[i].setTrial(besin[i].getTrial() + 1);;
                }
            }
            i++;
            if (i == besinSayisi) {
                i = 0;
            }
        }
        
    }
    
    public void kasifAriGonder(){
        int i,maxTrialIndex;
        maxTrialIndex = 0;
        
        for (i = 1; i < besinSayisi; i++) {
        	
            if (besin[i].getTrial() > besin[maxTrialIndex].getTrial()) {
                maxTrialIndex = i;
            }
        }
        if (besin[maxTrialIndex].getTrial() >= limit) {
            besinOlustur(maxTrialIndex);
            //System.out.println(maxTrialIndex + "aþtý");
        }
        /*for (i = 0; i < besinSayisi; i++) {
        	if(besin[i].getTrial() >= limit) {
        		besinOlustur(i);
        	}
        }
        */
        
    }
    
    public double yakinlik(double x, double y){
        double d;

        d = Math.abs((Math.pow(x - Beacon1[0], 2) + Math.pow(y - Beacon1[1], 2) - Math.pow(r1, 2))) + 
     		Math.abs((Math.pow(x - Beacon2[0], 2) + Math.pow(y - Beacon2[1], 2) - Math.pow(r2, 2))) + 
     		Math.abs((Math.pow(x - Beacon3[0], 2) + Math.pow(y - Beacon3[1], 2) - Math.pow(r3, 2)));

        return d;
    }
    
    public double calculateFitness(double f){
        double result = 0;
        if(f >= 0){
            result = 1 / (f + 1);
        }
        else{
            result = 1 + Math.abs(f);
        }
        return result;
    }
    
    public void olasilikHesapla(){
        double toplamFitness = 0;
        for (int i = 0; i < besinSayisi; i++) {
            toplamFitness += besin[i].getFitness();
        }
        for (int i = 0; i < besinSayisi; i++) {
        	besin[i].setProb(besin[i].getFitness() / toplamFitness);
        }
        
    }
    
    public void besinOlustur(int i) {
		besin[i] = new ABC(altLimitler[0],ustLimitler[0],altLimitler[1],ustLimitler[1]);
		besin[i].setF(yakinlik(besin[i].getX(), besin[i].getY()));
		besin[i].setFitness(calculateFitness(besin[i].getF()));
		besin[i].setTrial(0);
    }
    
    public void enIyiCozum(){
        for (int i = 0; i < besinSayisi; i++) {
            if (besin[i].getF() < globalMin) {
                globalMin = besin[i].getF();
                globalParams[0] = besin[i].getX();
                globalParams[1] = besin[i].getY();

            }
        }
    }
}
