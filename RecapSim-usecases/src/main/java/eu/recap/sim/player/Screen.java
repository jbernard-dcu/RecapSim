/**
 * Used to draw shapes
 * 
 */
package eu.recap.sim.player;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import eu.recap.sim.experiments.ExperimentHelpers;
import eu.recap.sim.models.WorkloadModel.Device;
import eu.recap.sim.models.LocationModel.Location;
import eu.recap.sim.models.WorkloadModel.Workload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.sql.Statement;

/**
 * @author Sergej Svorobej
 *
 */
@SuppressWarnings("serial")
public class Screen extends JPanel implements ActionListener {
	//presets for tieto db
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH);
	String startingDateTimeDb = "2018-07-05-07:00:30";
	Connection con = null;
	
	private Workload rwm;
	private Image img;
	int refreshNo = 0;
	int timestep = 30;// seconds
	Color deviceColour;
	
	//csv line number tracker
	int csvLineNumber=0;
	LocalDateTime startingDateTime = null;

	// delay milliseconds
	Timer timer = new Timer(5, this);
	int x = 0, velX = 2;

	public Screen(Image img, Workload rwm) {
		this.deviceColour = new Color(255, 50, 255, 100);
		this.rwm = rwm;
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);


		
		// this.repaint();

	}

	// (1)
	public Screen(Image img) {
		this.deviceColour = new Color(255, 50, 255, 100);
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);

		//initiate db connection
		
		String dbFilepath = "DB//tietodb.db";
		con = ConnectDb(dbFilepath);
		
		// this.repaint();

	}
	public static Connection ConnectDb(String dbFilepath) {

		try {
			// String dir = System.getProperty("user.dir");
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbFilepath);
			return con;
		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	public void paintComponentOld(Graphics g) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = image.getGraphics();
		int simtime = refreshNo * timestep;
		int prevousTime = simtime - timestep;
		if(simtime>1500){
			System.exit(0);
		}
		// background
		g.drawImage(img, 0, 0, null);
		//logo
		Image recapLogo = new ImageIcon("images/recap-logo_colour_small.png").getImage();
		g.drawImage(recapLogo, 1200, 5, 100, 62, null);
		
		//draw lines
		g.setColor(Color.DARK_GRAY);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(100+30, 400+20, 625+20, 533+15);
		g2d.drawLine(600+25, 424+25, 625+20, 533+15);
		g2d.drawLine(760+15, 284+25, 625+20, 533+15);
		g2d.drawLine(925+15, 235+25, 625+20, 533+15);
		g2d.drawLine(823+15, 532+17, 625+20, 533+15);
		g2d.drawLine(862+15, 860+15, 625+20, 533+15);
		g2d.drawLine(478+25, 739+15, 625+20, 533+15);
		
		//draw dz
		int width=40,  height =40;
		Image dcEdgeCircleImg = new ImageIcon("images/edge_circle.png").getImage();
		Image dcCoreCircleImg = new ImageIcon("images/core_circle.png").getImage();
		g.drawImage(dcEdgeCircleImg, 100, 400, width, height, null);
		g.drawImage(dcEdgeCircleImg, 600, 424, width, height, null);
		g.drawImage(dcEdgeCircleImg, 760, 284, width, height, null);
		g.drawImage(dcEdgeCircleImg, 925, 235, width, height, null);
		g.drawImage(dcEdgeCircleImg, 823, 532, width, height, null);
		g.drawImage(dcEdgeCircleImg, 862, 860, width, height, null);
		g.drawImage(dcEdgeCircleImg, 478, 739, width, height, null);
		//core
		g.drawImage(dcCoreCircleImg, 625, 533, width, height, null);
		

		
		
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, 1000, 1000);
		// countdown
		g.setColor(Color.BLACK);
		Font font = new Font("Arial", Font.BOLD, 18);
		g.setFont(font);
		System.out.println("Time:" + simtime + "s");
		//g.drawString("Time:" + simtime + "s", 500, 24);

		String csvFilePath = "DB/t3arena_578813rows.txt";
		String SPLIT_CHAR = ";";
		try {
			Scanner scanner = new Scanner(new File(csvFilePath));
			int lineCounter =0;
			boolean setTheStartSeconds =true;
			int dateTimeCountdown =-1;
			while (scanner.hasNextLine()) {
				//fast forwarding to the previous last line in CSV file
				String scanLine = scanner.nextLine();
				if(lineCounter==csvLineNumber){
					String[] line = scanLine.split(SPLIT_CHAR);
					//String deviceId = line[1];
					String dateCsv = line[0];
					String latitude = line[1];
					String longitude = line[2];
					
		            //parse time into seconds from 2018-07-05-07:00:30
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH);
		            LocalDateTime dateTime = LocalDateTime.parse(dateCsv,formatter);
		            if(setTheStartSeconds){
		            	startingDateTime = dateTime;
		            	setTheStartSeconds=false;
		            }
		            	
		            int seconds= ExperimentHelpers.differenceInSeconds(startingDateTime,dateTime);

		            
		            //check if second counter still the same as in the first line that was rewinded to
		            if(seconds<=timestep){
						// (x,y)
						int latitudeAprox = (int) Double.parseDouble(latitude);
						int longitudeAprox = (int) Double.parseDouble(longitude);
						g.setColor(deviceColour);
						g.fillRect(latitudeAprox, longitudeAprox, 5, 5);
						csvLineNumber++;
		            }else{
		            	String aa = "aa";
		            	//if nextline time is different then exit the loop
		            	break;
		            }
		            

				}
				lineCounter++;
			}
			scanner.reset();
			scanner.close();
		} catch (FileNotFoundException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
			System.exit(0);
		}


//		g.drawRect(100, 100, 50, 50);
//		g.setColor(Color.GREEN);
//		g.fillRect(190, 200, 50, 50);
//
//		g.fillRect(300, 300, 5, 5);
//		g.setColor(Color.RED);
//		g.fillRect(x, 30, 2, 2);


		//this.paint(g);
		 try {
		        ImageIO.write(image, "png", new File("slideshow/"+refreshNo+".png"));
		    } catch (IOException ex) {
		    	ex.printStackTrace();
	        
		   }
		
		timer.start();

		if (simtime > 86_400) {
			System.out.println("reached 24h");
			timer.stop();
			System.exit(0);
		}

	}

	public void paintComponent(Graphics g) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//g = image.getGraphics();
		int simtime = refreshNo * timestep;
		int prevousTime = simtime - timestep;
		if(simtime>1500){
			System.exit(0);
		}
		// background
		g.drawImage(img, 0, 0, null);
		//logo
		Image recapLogo = new ImageIcon("images/recap-logo_colour_small.png").getImage();
		g.drawImage(recapLogo, 1200, 5, 100, 62, null);
		
		//draw lines
		g.setColor(Color.DARK_GRAY);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(100+30, 400+20, 625+20, 533+15);
		g2d.drawLine(600+25, 424+25, 625+20, 533+15);
		g2d.drawLine(760+15, 284+25, 625+20, 533+15);
		g2d.drawLine(925+15, 235+25, 625+20, 533+15);
		g2d.drawLine(823+15, 532+17, 625+20, 533+15);
		g2d.drawLine(862+15, 860+15, 625+20, 533+15);
		g2d.drawLine(478+25, 739+15, 625+20, 533+15);
		
		//draw DC
		int width=40,  height =40;
		Image dcEdgeCircleImg = new ImageIcon("images/edge_circle.png").getImage();
		Image dcCoreCircleImg = new ImageIcon("images/core_circle.png").getImage();
		g.drawImage(dcEdgeCircleImg, 100, 400, width, height, null);
		g.drawImage(dcEdgeCircleImg, 600, 424, width, height, null);
		g.drawImage(dcEdgeCircleImg, 760, 284, width, height, null);
		g.drawImage(dcEdgeCircleImg, 925, 235, width, height, null);
		g.drawImage(dcEdgeCircleImg, 823, 532, width, height, null);
		g.drawImage(dcEdgeCircleImg, 862, 860, width, height, null);
		g.drawImage(dcEdgeCircleImg, 478, 739, width, height, null);
		//core
		g.drawImage(dcCoreCircleImg, 625, 533, width, height, null);
		

		
		
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, 1000, 1000);
		// countdown
		g.setColor(Color.BLACK);
		Font font = new Font("Arial", Font.BOLD, 18);
		g.setFont(font);
		System.out.println("Time:" + simtime + "s");
		//g.drawString("Time:" + simtime + "s", 500, 24);
		
		for (Device device : rwm.getDevicesList()) {
			for (Location location : device.getLocationsList()) {
				// if the coordinates are in the time frame paint them
				if (prevousTime < location.getTime() && location.getTime() <= simtime) {
					// TO-DO : convert coordinates into the map picture pixels

					// (x,y)
					int latitudeAprox = (int) location.getLatitude();
					int longitudeAprox = (int) location.getLongitude();
					g.setColor(deviceColour);
					g.fillRect(latitudeAprox, longitudeAprox, 2, 2);
				}

			}
		}


		timer.start();

		if (simtime > 86_400) {
			timer.stop();
		}

	}

	public void paintComponentOldCsv(Graphics g) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = image.getGraphics();
		int simtime = refreshNo * timestep;
		int prevousTime = simtime - timestep;
		// background
		g.drawImage(img, 0, 0, null);
		// countdown
		Font font = new Font("Arial", Font.BOLD, 18);
		g.setFont(font);
		g.drawString("Time:" + simtime + "s", 500, 24);
		
		//draw infrastructure
		g.drawImage(img, 0, 0, null);

		String csvFilePath = "traceUe_full.log";
		String SPLIT_CHAR = ";";
		try {
			Scanner scanner = new Scanner(new File(csvFilePath));
			int lineCounter =0;
			boolean setTheStartSeconds =true;
			int dateTimeCountdown =-1;
			while (scanner.hasNextLine()) {
				//fast forwarding to the previous last line in CSV file
				String scanLine = scanner.nextLine();
				if(lineCounter==csvLineNumber){
					String[] line = scanLine.split(SPLIT_CHAR);
					String deviceId = line[1];
					String dateCsv = line[2];
					String latitude = line[3];
					String longitude = line[4];
					
		            //parse time into seconds from 2018-07-05-07:00:30
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH);
		            LocalDateTime dateTime = LocalDateTime.parse(dateCsv,formatter);
		            if(setTheStartSeconds){
		            	startingDateTime = dateTime;
		            	setTheStartSeconds=false;
		            }
		            	
		            int seconds= ExperimentHelpers.differenceInSeconds(startingDateTime,dateTime);

		            
		            //check if second counter still the same as in the first line that was rewinded to
		            if(seconds<=timestep){
						// (x,y)
						int latitudeAprox = (int) Double.parseDouble(latitude);
						int longitudeAprox = (int) Double.parseDouble(longitude);
						g.setColor(deviceColour);
						g.fillRect(latitudeAprox, longitudeAprox, 5, 5);
						csvLineNumber++;
		            }else{
		            	String aa = "aa";
		            	//if nextline time is different then exit the loop
		            	break;
		            }
		            

				}
				lineCounter++;
			}
			scanner.reset();
			scanner.close();
		} catch (FileNotFoundException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
			System.exit(0);
		}


//		g.drawRect(100, 100, 50, 50);
//		g.setColor(Color.GREEN);
//		g.fillRect(190, 200, 50, 50);
//
//		g.fillRect(300, 300, 5, 5);
//		g.setColor(Color.RED);
//		g.fillRect(x, 30, 2, 2);


		//this.paint(g);
		 try {
		        ImageIO.write(image, "png", new File("slideshow/"+refreshNo+".png"));
		    } catch (IOException ex) {
		    	ex.printStackTrace();
		        
		   }
		
		timer.start();

		if (simtime > 86_400) {
			System.out.println("reached 24h");
			timer.stop();
			System.exit(0);
		}

	}

	public void paintComponentOldSql(Graphics g) {
		int simtime = refreshNo * timestep;
		int prevousTime = simtime - timestep;
		// background
		g.drawImage(img, 0, 0, null);
		// countdown
		Font font = new Font("Arial", Font.BOLD, 18);
		g.setFont(font);
		g.drawString("Time:" + simtime + "s", 500, 24);

		// connect to the DB
		
        
        LocalDateTime startDateTime = LocalDateTime.parse(startingDateTimeDb,formatter);

		ResultSet resultSet = null;
		Statement statement = null;

		
		try {
			statement = con.createStatement();
			//SELECT * FROM traceUe_full WHERE datetime ="2018-07-05-07:00:30";
			resultSet = statement.executeQuery("SELECT latitude,longitude FROM 'traceUe_full' WHERE datetime="+"'"+startingDateTimeDb+"'");
			while (resultSet.next()) {
				//System.out.println("OLO" + resultSet.getString("uid"));
				// (x,y)
				int latitudeAprox = (int) Double.parseDouble(resultSet.getString("latitude"));
				int longitudeAprox = (int) Double.parseDouble(resultSet.getString("longitude"));
				g.setColor(deviceColour);
				g.fillRect(latitudeAprox, longitudeAprox, 5, 5);

				}
			} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				// close everything
				resultSet.close();
				statement.close();
				//con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		startDateTime.plusSeconds(30);
		startingDateTimeDb = startDateTime.toString().replace("T", "-");


	}
	
	// (2)
	public void actionPerformed(ActionEvent e) {
		x = x + velX;

		refreshNo++;
		repaint();
	}

}
