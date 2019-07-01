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

public class UmeaT3ArenaGenerator {

	public static void main(String[] args) throws IOException {

		PrintStream fileStream = new PrintStream(new File("out.txt"));

		int csvLineNumber = 0;
		String csvFilePath = "DB/last30s_tieto7960rows.txt";
		String SPLIT_CHAR = ";";
		try {
			Scanner scanner = new Scanner(new File(csvFilePath));
			int lineCounter = 0;
			boolean setTheStartSeconds = true;
			int dateTimeCountdown = -1;
			while (scanner.hasNextLine()) {
				// fast forwarding to the previous last line in CSV file
				String scanLine = scanner.nextLine();

				String[] line = scanLine.split(SPLIT_CHAR);
				// String deviceId = line[1];
				String dateCsv = line[0];
				String latitude = line[1];
				String longitude = line[2];

				// parse time into seconds from 2018-07-05-07:00:30
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH);
				LocalDateTime dateTime = LocalDateTime.parse(dateCsv, formatter);

				// (x,y)
				double locationX = Double.parseDouble(latitude);
				double locationY = Double.parseDouble(longitude);
				// coordinates of the T3 arena
				double maxX = 740;
				double minX = 709;
				double maxY = 535;
				double minY = 506;
				// random destination in the arena
				double destinationX = (Math.random() * ((maxX - minX) + 1)) + minX;
				double destinationY = (Math.random() * ((maxY - minY) + 1)) + minY;

				// steps to destination
				int steps = 10;
				double stepX = (destinationX - locationX) / steps;
				double stepY = (destinationY - locationY) / steps;

				int count = 0;
				double currentLocationX = locationX;
				double currentLocationY = locationY;
				while (count != steps) {
					currentLocationX = currentLocationX + stepX;
					currentLocationY = currentLocationY + stepY;
					dateTime = dateTime.plusSeconds(30);
					String newDate = dateTime.toString().replace("T", "-");
					//2018-07-05-07:35:30 check for missing seconds
					if(newDate.length()<19){
						newDate=newDate+":00";
					}
					

					// String
					// newDate=dateTime.getYear()+"-"+dateTime.getMonthValue()+"-"+dateTime.getDayOfMonth()+"-"+dateTime.getHour()+":"+dateTime.getMinute()+":"+dateTime.getSecond();
					//System.out.println(newDate + ";" + currentLocationX + ";" + currentLocationY);
					fileStream.println(newDate + ";" + currentLocationX + ";" + currentLocationY);

					// saving the existing point in the same spot
					//System.out.println(newDate + ";" + latitude + ";" + longitude);
					fileStream.println(newDate + ";" + latitude + ";" + longitude);

					count++;
				}
				//hover on the same spot
				double hoverX = (Math.random() * ((2 - 1) + 1)) + 1;
				double hoverY = (Math.random() * ((2 - 1) + 1)) + 1;
				count=0;
				while (count != steps) {
					currentLocationX = currentLocationX + hoverX;
					currentLocationY = currentLocationY + hoverY;
					dateTime = dateTime.plusSeconds(30);
					String newDate = dateTime.toString().replace("T", "-");
					//2018-07-05-07:35:30 check for missing seconds
					if(newDate.length()<19){
						newDate=newDate+":00";
					}
					

					// String
					// newDate=dateTime.getYear()+"-"+dateTime.getMonthValue()+"-"+dateTime.getDayOfMonth()+"-"+dateTime.getHour()+":"+dateTime.getMinute()+":"+dateTime.getSecond();
					//System.out.println(newDate + ";" + currentLocationX + ";" + currentLocationY);
					fileStream.println(newDate + ";" + currentLocationX + ";" + currentLocationY);

					// saving the existing point in the same spot
					//System.out.println(newDate + ";" + latitude + ";" + longitude);
					fileStream.println(newDate + ";" + latitude + ";" + longitude);

					count++;
				}
				
				
				
				
				

				lineCounter++;
				System.out.println("line:" + lineCounter);
				if(lineCounter==3001){
					break;
				}

			}
			scanner.reset();
			scanner.close();
		} catch (FileNotFoundException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
			System.exit(0);
		}

		fileStream.close();

	}

}
