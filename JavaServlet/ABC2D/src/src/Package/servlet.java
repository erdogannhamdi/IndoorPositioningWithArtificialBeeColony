package src.Package;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class servlet
 */
@WebServlet("/Server")
public class servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/*public double[][] Beacons = {
    		{4.5, 3.2},
    		{1.3, 3.2},
    		{1.3, 0.0},
    		};
	//ev*/
	public double[][] Beacons = {
    		{0.0, 0.0},
    		{0.0, 6.0},
    		{6.0, 0.0},
    		{6.0, 6.0},
    		{12.0, 0.0},
    		{12.0, 6.0},
    		};
	//okul
    public double[] xy = new double[3]; 
    public double[] Txy = new double[2];
	public String x,y,hata,Tx,Ty;
	public Random rand = new Random();
	public List<Data> datas = new ArrayList<Data>();
	public String path = "C:\\xampp\\htdocs\\Plotly\\dataZigzag.csv";
	public boolean check = false;
	
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			
			double[][] beacons = new double[3][3];
			//long startTime = System.currentTimeMillis();
			double[] r = new double[3];
			String[] name = new String[3];
			String gelen;

			if(!request.getParameter("r1").equals("NaN") && !request.getParameter("r2").equals("NaN") && !request.getParameter("r3").equals("NaN") ) {
				for (int i = 0; i < 3; i++) {
					gelen = request.getParameter("r"+(i+1));
					String[] ifade = gelen.split("-");
					r[i] = Double.parseDouble(ifade[0]);
					name[i] = ifade[1];		
				}


		        mainABC op = new mainABC();
		        Trilateration trila = new Trilateration();

		        for (int i = 0; i < 3; i++) {
		        	for (int k = 1; k < 8; k++) {
						if(name[i].equals("Beacon"+(k))) {
							beacons[i][0] = Beacons[k-1][0];
							beacons[i][1] = Beacons[k-1][1];

						}
					}
		        	
				}
		        
		        xy = op.optimization(beacons, r);
		        Txy = trila.Hesapla(beacons, r);
		        x = new DecimalFormat("##.##").format(xy[0]);
		        y = new DecimalFormat("##.##").format(xy[1]);
		        Tx = new DecimalFormat("##.##").format(Txy[0]);
		        Ty = new DecimalFormat("##.##").format(Txy[1]);

		        
		        //System.out.println(Tx + " " + Ty);
		        
		        //hata = ""+xy[2];
		        response.getWriter().append("X:" + x + "  Y:" + y);
		        writeData(x, y,Tx,Ty);
			} else {
				response.getWriter().append("X:" + x + "  Y:" + y);
				writeData(x, y,Tx,Ty);
			}

	        //long endTime = System.currentTimeMillis();
	        //long estimatedTime = endTime - startTime;
	        //double seconds = (double)estimatedTime/1000;
	        //System.out.println(seconds);
		
		
	}
	
	public void writeData(String x, String y,String Tx, String Ty) throws IOException {
		//csv	
		FileWriter fileWriter = new FileWriter(path,true);
		if(!check) {
			fileWriter.append("X,Y,TX,TY\n");
			check = true;
		}
		Data data = new Data();
		
		x = x.replace(',', '.');
		y = y.replace(',', '.');
		Tx = Tx.replace(',', '.');
		Ty = Ty.replace(',', '.');

		
		data.setX(Double.parseDouble(x));
		data.setY(Double.parseDouble(y));
		data.setTx(Double.parseDouble(Tx));
		data.setTy(Double.parseDouble(Ty));

		datas.add(data);
		
		try {

			fileWriter.append(String.valueOf(data.getX()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(data.getY()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(data.getTx()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(data.getTy()));
			
			fileWriter.append("\n");
		
			fileWriter.flush();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	
	

	
}