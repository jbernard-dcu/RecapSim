package Classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import eu.recap.sim.models.WorkloadModel.*;

public class WorkloadGenTest {

	final static int MOY_NBWORD = 4;
	final static int STD_NBWORD = 2;

	final static int NB_TERMSET = 10000;
	final static int NB_REQUEST = 1000;

	public static void main(String[] args) {

		/*
		 * Creation of termDist, Zipf distribution Creation of timeSequence, Exponential
		 * distribution
		 */
		long startTime = new Date().getTime();

		TreeMap<Long, Double> termDist = new TreeMap<Long, Double>();
		List<Long> timeSequence = new ArrayList<Long>();
		timeSequence.add(startTime);
		for (int rang = 1; rang < NB_TERMSET; rang++) {
			termDist.put((long) rang, 1. / rang);
			timeSequence.add(timeSequence.get(rang - 1) + getNextTime());
		}

		/*
		 * Generating client IDs list TODO : change method of filling to be faster
		 */
		int fill = 0;
		String clientID;
		List<String> IDS = new ArrayList<String>(NB_REQUEST);
		while (fill <= NB_REQUEST) {
			// generate new ID
			clientID = new ID().createID();
			// run one loop with for example exponential distribution to choose which
			// requests the client has
			for (int r = 0; r < NB_REQUEST; r = r + (int) (new ExpD(1).sample())) {
				if (IDS.get(r) == null) {
					IDS.set(r, clientID);
					fill++;
				}
			}
		}

		/*
		 * Creation of Requests
		 */
		List<Request.Builder> buildersRequests = buildersRequests(termDist, NB_REQUEST);
		List<Request> requests = new ArrayList<Request>();
		int i = 0;
		for (Request.Builder request : buildersRequests) {
			request.setTime(timeSequence.get(i));

			// TODO : setting all possible parameters here
			requests.add(request.build());
		}

		/*
		 * Allocation of requests on devices, LinknovateValidationRWM_LogAccess TODO :
		 * check
		 */
		// creating devices list
		int deviceQty = 0;
		HashMap<String, Device.Builder> devices = new HashMap<String, Device.Builder>();
		for (String id : IDS) {
			if (!devices.containsKey(id)) {
				Device.Builder device = Device.newBuilder();
				device.setDeviceId(deviceQty + "");
				device.setDeviceName("IP_" + id + "_" + deviceQty);
				deviceQty++;
				devices.put(id, device);
			}
		}
		// adding requests to each device
		for (int req = 0; req < NB_REQUEST; req++) {
			devices.get(IDS.get(req)).addRequests(requests.get(req));
		}

		/*
		 * Workload generation
		 */
		Workload.Builder workloadBuilder = Workload.newBuilder();
		for (Device.Builder device : devices.values()) {
			workloadBuilder.addDevices(device);
		}
		Workload workload = workloadBuilder.build();

		printResults(workload);

	}

	private static void printResults(Workload workload) {
		System.out.println("   Request   |   Time   |   Device   ");
		for (Device device : workload.getDevicesList()) {
			for (Request request : device.getRequestsList()) {
				System.out.println(request.getSearchContent().toString() + " | " + request.getTime() + " | "
						+ device.getDeviceId());
			}
		}

	}

	/**
	 * Exponential distributed time of next request
	 * 
	 * @return
	 */
	private static long getNextTime() {
		double lambda = 0.0001;
		return (long) (new ExpD(lambda).sample());
	}

	/**
	 * 
	 * @param termDist
	 * @param nbRequest
	 * @return
	 */
	public static List<Request.Builder> buildersRequests(TreeMap<Long, Double> termDist, int nbRequest) {
		List<Request.Builder> res = new ArrayList<Request.Builder>();

		for (int nR = 0; nR < nbRequest; nR++) {
			Request.Builder request = Request.newBuilder();
			request.setSearchContent(randQueryContent(termDist, randNbWord())).setComponentId("1").setApiId("1_1")
					.setRequestId(nR).setDataToTransfer(1); // ??
			// request.set...
			res.add(request);
		}
		return res;
	}

	/**
	 * 
	 * @return
	 */
	public static int randNbWord() {
		int res = (int) (MOY_NBWORD + STD_NBWORD * new Random().nextGaussian());
		return (res <= 0) ? 1 : res;
	}

	/**
	 * Creates a formatted String giving the contents of a Request. Change this method to change the 
	 * format of the String.
	 * 
	 * Current format : 123456289
	 * 1st digit : length of the number
	 * following digits : number
	 * etc... for all numbers
	 * So here the request is 2-456-89
	 * 
	 * @param termDist
	 * @param nbWord
	 * @return
	 */
	public static String randQueryContent(TreeMap<Long, Double> termDist, int nbWord) {
		//Creating distribution
		FreqD<Long> dist=new FreqD<Long>(termDist);
		
		// Creating list of content
		List<Long> content = new ArrayList<Long>();
		int word = 0;
		while (word < nbWord) {
			long add = dist.sample();
			if (!content.contains(add)) {
				content.add(add);
				word++;
			}
		}

		// Parsing into a formatted String
		String rep = "";
		for (long w : content) {
			int len = (int) (Math.log10(w) + 1);
			rep = rep + len + w;
		}

		return rep;
	}

}
