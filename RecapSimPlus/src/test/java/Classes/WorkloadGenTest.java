package Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import eu.recap.sim.models.WorkloadModel.*;

public class WorkloadGenTest {

	// Parameters randNbWords()
	final static int MOY_NBWORD = 4;
	final static int STD_NBWORD = 2;

	// Parameters termSet and querySet
	final static int NB_TERMSET = 10000;
	final static int NB_REQUEST = 1000;

	public static void main(String[] args) {

		/*
		 * Creation of termDist, Zipf distribution Creation of timeSequence, Exponential
		 * distribution
		 */
		long startTime = System.currentTimeMillis();

		TreeMap<Long, Double> termDist = new TreeMap<Long, Double>();
		List<Long> timeSequence = new ArrayList<Long>();
		timeSequence.add(startTime);
		for (int rang = 1; rang < NB_TERMSET; rang++) {
			termDist.put((long) rang, 1. / rang);
			timeSequence.add(timeSequence.get(rang - 1) + getNextTime());
		}

		/*
		 * Generating client IDs list and repart of requests between clients TODO :
		 * allow any distribution
		 */
		// Creating lists
		String[] IDs = new String[NB_REQUEST]; // Each index corresponds to one request, the value is the clientID
												// corresponding
		List<Integer> freeSpace = new ArrayList<Integer>();
		for (int i = 0; i < NB_REQUEST; i++) {
			freeSpace.add(i);
		}
		String clientID;

		// Creating Exp law
		double lambda = 10. / NB_REQUEST;
		ExpD exp = new ExpD(lambda);

		// Creating ID generator
		IDGenerator idGen = new IDGenerator();

		while (!freeSpace.isEmpty()) {
			int space = freeSpace.get(0);
			clientID = idGen.createID();

			while (space < NB_REQUEST) {
				IDs[space] = clientID;
				freeSpace.remove((Integer) space);
				space = space + (int) exp.sample();
			}
		}

		/*
		 * Creation of Requests
		 */
		List<Request.Builder> buildersRequests = buildersRequests(termDist);
		List<Request> requests = new ArrayList<Request>();
		int i = 0;
		for (Request.Builder request : buildersRequests) {
			request.setTime(timeSequence.get(i));

			// TODO : setting all possible parameters here
			requests.add(request.build());

			i++;
		}

		/*
		 * Allocation of requests on devices, LinknovateValidationRWM_LogAccess TODO :
		 * check
		 */
		// creating devices list
		int deviceQty = 0;
		HashMap<String, Device.Builder> devices = new HashMap<String, Device.Builder>();
		for (String id : IDs) {
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
			devices.get(IDs[req]).addRequests(requests.get(req));
		}

		/*
		 * Workload generation
		 */
		Workload.Builder workloadBuilder = Workload.newBuilder();
		for (Device.Builder device : devices.values()) {
			workloadBuilder.addDevices(device);
		}
		Workload workload = workloadBuilder.build();

		printResults(workload, startTime);

	}

	private static void printResults(Workload workload, long startTime) {
		String configPrintf="%32s %1s %8s %1s %6s %1s %17s";
		System.out.printf(configPrintf,"Request","|","Time","|","Device","|","Score\n");
		System.out.println("----------------------------------------------------------------------------------");
		for (Device device : workload.getDevicesList()) {
			for (Request request : device.getRequestsList()) {
				System.out.printf(configPrintf,
						request.getSearchContent().toString(),"|",
						(request.getTime() - startTime),"|",
						device.getDeviceId(),"|",
						getScore(request)+"\n");
			}
		}

	}

	/**
	 * Exponential distributed time of next request
	 */
	private static long getNextTime() {
		double lambda = 0.0001;
		return (long) (new ExpD(lambda).sample());
	}

	/**
	 * generates a List of nbRequest Request.Builders</br>
	 * searchContent, ComponentId, apiId, reqestId and dataToTransfer are set
	 * here</br>
	 * TODO : add as many settings as possible TODO : add querySet!=querySequence
	 */
	public static List<Request.Builder> buildersRequests(TreeMap<Long, Double> termDist) {
		// Generating RequestSet and RequestScores
		List<Request.Builder> requestSet = new ArrayList<Request.Builder>();
		List<Double> requestScores = new ArrayList<Double>();
		for (int nR = 0; nR < NB_REQUEST; nR++) {
			Request.Builder request = Request.newBuilder();
			request.setSearchContent(randQueryContent(termDist, randNbWord())).setComponentId("1").setApiId("1_1")
					.setRequestId(nR).setDataToTransfer(1); // ??
			// request.set...
			requestSet.add(request);
			requestScores.add(getScore(request.build()));
		}

		// Generating distribution
		FreqD<Request.Builder> dist = new FreqD<Request.Builder>(requestSet, requestScores);

		// Picking requests
		List<Request.Builder> requestSequence = new ArrayList<Request.Builder>();
		for (int req = 0; req < NB_REQUEST; req++) {
			requestSequence.add(dist.sample());
		}

		return requestSequence;

	}

	/**
	 * Returns the score of the query</br>
	 * Change this method to change way of valorising requests.
	 */
	public static double getScore(Request r) {
		String content = r.getSearchContent();
		int index = 0;
		int nbWord = 0;
		double score = 0.;
		while (index < content.length()) {
			int len = Integer.parseInt(content.substring(index, index + 1));
			long word = Long.parseLong(content.substring(index + 1, index + 1 + len));
			nbWord += 1;
			score += 1. / word;
			index += len + 1;
		}
		return score / nbWord;
	}

	/**
	 * Random integer number with a gaussian distribution</br>
	 * Change parameters or this to have a different distribution of the length of
	 * words
	 */
	public static int randNbWord() {
		int res = (int) (MOY_NBWORD + STD_NBWORD * new Random().nextGaussian());
		return (res <= 0) ? 1 : res;
	}

	/**
	 * Creates a formatted String giving the contents of a Request. </br>
	 * Change this method to change the format of the String.</br>
	 * Change proto file of WorkloadModel to change type of request content. </br>
	 * 
	 * TODO format : "1+23+5+..."
	 */
	public static String randQueryContent(TreeMap<Long, Double> termDist, int nbWord) {
		// Creating distribution
		FreqD<Long> dist = new FreqD<Long>(termDist);

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
