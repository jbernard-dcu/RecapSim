package eu.recap.sim.experiments;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.WorkloadModel.Workload;

public class DistributionLNK {
	/*
	2A17 503865936948258	0.13
	BA99 524176202573681	0.14
	318C 593782691932777	0.16
	9A4F 590034818293992	0.16
	F421 672765708411323	0.18
	9A2F 884979659589582	0.23	
	
	
	*/				
	

	public static void main(String[] args) {
		
		int requestNumber = 1_000_000;
		Double[] normalDistribution = {0.13,0.14,0.16,0.16,0.18,0.23};
		Distro(requestNumber,normalDistribution);

		

	}
	public static Integer[] Distro(int requestNumber, Double[] normalDistribution) {
		
		ArrayList<Integer> requestsArray = new ArrayList<Integer>();
		
		//initialise
		int counter=0;
		while(counter<requestNumber){
			requestsArray.add(counter);
			counter++;
		}
		for(int request:requestsArray){
			request =0;
		}
		
		
		Integer[] normalDistributionLimits = new Integer[normalDistribution.length];
		Integer[] normalDistributionBuckests = new Integer[normalDistribution.length];
		
		counter=0;
		for(Double probability:normalDistribution){
			//calculate limits of each bucket
			double numberOfRequests  = requestNumber * probability ;
			int roundedToHigherValue= (int)Math.ceil(numberOfRequests);
			normalDistributionLimits[counter]=roundedToHigherValue;
			//initialise buckets
			normalDistributionBuckests[counter]=0;
			counter++;
		}
		int emptyCycles=0;
		counter =0;
		while(counter<requestNumber){
			//1. pick random request from the array
			int requestNumberInArray = ThreadLocalRandom.current().nextInt(0, requestsArray.size());
			//remove this element from the array for future use
			requestsArray.remove(requestNumberInArray);
			//get random bucket location
			boolean emptyBucketNotFound = true;
			int chosenBucket=-1;
			while (emptyBucketNotFound){
				chosenBucket = ThreadLocalRandom.current().nextInt(0, normalDistributionBuckests.length);
				//check if the bucket within distribution limits (not full)
				if(normalDistributionBuckests[chosenBucket]<normalDistributionLimits[chosenBucket]){
					emptyBucketNotFound=false;
				}
				//check if all the buckets are full
				
				// TODO fix this by popping up full buckets from random search
				emptyCycles++;
			}
			
			//2. put request to the random bucket
			normalDistributionBuckests[chosenBucket] = normalDistributionBuckests[chosenBucket]+1;
			
			counter++;
		}
		
		System.out.println(counter+" times");
		System.out.println(emptyCycles+" empty cycles\n");
		for(int numbers : normalDistributionBuckests){
		
			System.out.println(numbers);
		}
		
		return normalDistributionBuckests;
		
	}

	}


