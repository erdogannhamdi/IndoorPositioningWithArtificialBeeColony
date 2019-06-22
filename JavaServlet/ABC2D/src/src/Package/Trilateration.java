package src.Package;

public class Trilateration {
	private double[] Beacon1 = new double[2];
    private double[] Beacon2 = new double[2];
    private double[] Beacon3 = new double[2];
    private double r1;
    private double r2;
    private double r3;
    
    public double[] Hesapla(double[][] beacons, double[] r) {
        Beacon1[0] = beacons[0][0];
        Beacon1[1] = beacons[0][1];

        Beacon2[0] = beacons[1][0];
        Beacon2[1] = beacons[1][1];

        Beacon3[0] = beacons[2][0];
        Beacon3[1] = beacons[2][1];

        r1 = r[0];
        r2 = r[1];
        r3 = r[2];
        
        double a = 2*(Beacon2[0] - Beacon1[0]);
        double b = 2*(Beacon2[1] - Beacon1[1]);
        double d = 2*(Beacon3[0] - Beacon2[0]);
        double e = 2*(Beacon3[1] - Beacon2[1]);
        
        double c = Math.pow(r1, 2) - Math.pow(r2, 2) - Math.pow(Beacon1[0], 2) + Math.pow(Beacon2[0], 2) - Math.pow(Beacon1[1], 2) + Math.pow(Beacon2[1], 2);
        double f = Math.pow(r2, 2) - Math.pow(r3, 2) - Math.pow(Beacon2[0], 2) + Math.pow(Beacon3[0], 2) - Math.pow(Beacon2[1], 2) + Math.pow(Beacon3[1], 2);
        
        double x = ((c*e) - (f*b))/((e*a) - (b*d));
        double y = ((c*d) - (a*f))/ ((b*d) - (a*e));
        
        double[] xy = new double[2];
        
        xy[0] = x;
        xy[1] = y;
        
        return xy;
    }
}
