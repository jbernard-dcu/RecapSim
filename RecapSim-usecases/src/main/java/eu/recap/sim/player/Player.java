/**
 * 
 */
package eu.recap.sim.player;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import eu.recap.sim.models.WorkloadModel.Workload;



/**
 * @author Sergej Svorobej
 *
 */
public class Player {

	/**
	 * @param args
	 */
	public Player(Workload workload) {
		// TODO Auto-generated method stub
		//create infrastructure model
		//create workload model
		//Workload.Builder workload = Workload.newBuilder(); //dummy
		//create jpanel with the map image background
		Screen devices = new Screen(new ImageIcon("images/Umea_map_pic.png").getImage(), workload);

	    JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(1334, 1034);//size of the image
	    frame.setIconImage(new ImageIcon("images/recap_logo.png").getImage());
	    frame.setResizable(true);
	    frame.setTitle("Umea Map");
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().add(devices);
	    
	    //frame.pack();
	    frame.setVisible(true);
	    
	  }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//create infrastructure model
		//create workload model
		//Workload.Builder workload = Workload.newBuilder(); //dummy
		//create jpanel with the map image background
		Screen devices = new Screen(new ImageIcon("images/Umea_map_pic.png").getImage());

	    JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(1334, 1034);//size of the image
	    frame.setIconImage(new ImageIcon("images/recap_logo.png").getImage());
	    frame.setResizable(true);
	    frame.setTitle("Umea Map");
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().add(devices);
	    
	    //frame.pack();
	    frame.setVisible(true);
	    
	  }
	
	}

