package eu.recap.sim.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import eu.recap.sim.experiments.ExperimentHelpers;

public class UmeaTietoWorkloadPruning {

	public static void main(String[] args) throws IOException {
		//which line will be saved in the new file
		int pruningLimit =100;
		int pruningCounter = 0;
		String fileName = "30min_normal_Tieto_Umea_455024rows.txt";
		PrintStream fileStream = new PrintStream(new File(fileName+"_reduced_by_"+pruningLimit+".txt"));

		int csvLineNumber = 0;
		int lineCounter = 0;

		String csvFilePath = "DB/"+fileName;

		try {
			Scanner scanner = new Scanner(new File(csvFilePath));


			while (scanner.hasNextLine()) {
				// fast forwarding to the previous last line in CSV file
				String scanLine = scanner.nextLine();

				
				if(pruningCounter == pruningLimit){
					//write line to another file
					fileStream.println(scanLine);				
					//reset the counter
					pruningCounter=0;
					csvLineNumber++;
				}
				
				pruningCounter++;
				lineCounter++;
			}
			scanner.reset();
			scanner.close();
		} catch (FileNotFoundException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
			System.exit(0);
		}

		fileStream.close();
		
		System.out.println("Lines before removal:"+lineCounter);
		System.out.println("Lines after removal:"+csvLineNumber);

	}

}
