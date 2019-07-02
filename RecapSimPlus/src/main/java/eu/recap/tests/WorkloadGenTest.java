package eu.recap.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.recap.sim.models.WorkloadModel;

public class WorkloadGenTest {
	
	public static void main(String[] args) {
		
	}
	
	public List<Request> buildRequests(int nbRequest){
		
	}
	
	public List<Long> randQuery(List<Long> termSet,List<Double> termFreq, int nbWord){
		List<Long> res=new ArrayList<Long>();
		
		List<Double> cumSum=new ArrayList<Double>();
		cumSum.add(termFreq.get(0));
		for(int i=1;i<termSet.size();i++) {
			cumSum.add(cumSum.get(i-1)+termFreq.get(i));
		}
		
		for(int word=0;word<nbWord;word++) {
			Double r=new Random().nextDouble()*cumSum.get(cumSum.size()-1);
			int index=0;
			while(r > cumSum.get(index)) {index++;}
			
			long add=termSet.get(index);
			if (!res.contains(add)) {
				res.add(add);
				word++;
			}
		}
		return res;
	}
	

}
