package eu.recap.tests;

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
		 * Generating client IDs list
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
		int i = 0;
		for (Request.Builder request : buildersRequests) {
			request.setTime(timeSequence.get(i));

			// TODO : setting all possible parameters here
			request.build();
		}

		/*
		 * Allocation of requests on devices, LinknovateValidationRWM_LogAccess
		 * TODO : check
		 */
		// Get IP addresses of devices who sent requests
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
		
		
		
		/*
		 * Workload generation
		 */

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

	public static int randNbWord() {
		int res = (int) (MOY_NBWORD + STD_NBWORD * new Random().nextGaussian());
		return (res <= 0) ? 1 : res;
	}

	public static List<Long> randQueryContent(TreeMap<Long, Double> termDist, int nbWord) {
		List<Long> res = new ArrayList<Long>();

		List<Double> cumSum = (List<Double>) termDist.values();
		for (int i = 1; i < cumSum.size(); i++) {
			cumSum.set(i, cumSum.get(i) + cumSum.get(i - 1));
		}

		// TODO : update values according to the rank in the map
		// TODO : change choice of mapping, currently value dependant

		for (int word = 0; word < nbWord; word++) {
			Double r = new Random().nextDouble() * cumSum.get(cumSum.size() - 1);
			long pick = 0;
			while (r > cumSum.get((int) pick)) {
				pick++;
			}

			if (!res.contains(pick)) {
				res.add(pick);
				word++;
			}
		}
		return res;
	}

}
