package eu.recap.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import eu.recap.sim.models.WorkloadModel.*;

public class WorkloadGenTest {
	
	final static int MOY_NBWORD=4;
	final static int STD_NBWORD=2;
	
	final static int NB_TERMSET=10000;
	final static int NB_REQUEST=1000;
	
	
	public static void main(String[] args) {
		
		/*
		 * Creation of termSet and termFreq, Zipf distribution
		 */
		Map<Long,Double> termDist=new TreeMap<Long,Double>();
		for(int rang=1;rang<NB_TERMSET;rang++) {termDist.put((long)rang, 1./rang);}
		
		/*
		 * 
		 */
		
		
		
		
		
		
		
	}
	
	
	
	public List<Request> buildRequests(TreeMap<Long,Double> termDist, int nbRequest){
		List<Request> res=new ArrayList<Request>();
		
		for(int nR=0;nR<nbRequest;nR++) {
			Request.Builder request=Request.newBuilder();
			request.setSearchContent(randQueryContent(termDist,randNbWord()));
			res.add(request.build());
		}
		return res;
	}
	
	
	
	public int randNbWord() {
		int res=(int)(MOY_NBWORD+STD_NBWORD*new Random().nextGaussian());
		return (res<=0)?1:res;
	}
	
	
	
	public List<Long> randQueryContent(TreeMap<Long,Double> termDist, int nbWord){
		List<Long> res=new ArrayList<Long>();
		
		List<Double> cumSum=(List<Double>)termDist.values();
		for(int i=1;i<cumSum.size();i++) {cumSum.set(i,cumSum.get(i)+cumSum.get(i-1));}
		
		//TODO : update values according to the rank in the map
		//TODO : change choice of mapping, currently value dependant
		
		for(int word=0;word<nbWord;word++) {
			Double r=new Random().nextDouble()*cumSum.get(cumSum.size()-1);
			long pick=0;
			while(r > cumSum.get((int)pick)) {pick++;}
			
			if (!res.contains(pick)) {
				res.add(pick);
				word++;
			}
		}
		return res;
	}
	

}
