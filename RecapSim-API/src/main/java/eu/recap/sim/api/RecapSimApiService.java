package eu.recap.sim.api;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;
import com.google.protobuf.util.JsonFormat.Parser;

import com.google.gson.Gson;

import eu.recap.sim.RecapSim;
import eu.recap.sim.models.ExperimentModel;

public class RecapSimApiService {
    public static void main(String[] args) {
    	
        post("/StartSimulation", (request, response) -> {
            response.type("application/json");
            String jsonExperimentModel = request.body();
            Parser jsonParser = com.google.protobuf.util.JsonFormat.parser();
            ExperimentModel.Experiment.Builder simulationExperiment = ExperimentModel.Experiment.newBuilder();
            jsonParser.merge(jsonExperimentModel, simulationExperiment);
            
    		//run the simulation
    		RecapSim recapExperiment = new RecapSim();
    		String simulationId = recapExperiment.StartSimulation(simulationExperiment.build());	

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,simulationId));
        });

      get("/Simulation", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

       get("/Simulation/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(("Success"));
        });

        put("/Simulation/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(("Success"));
        });

        delete("/Simulation/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(("Success"));
        });

        options("/Simulation/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(("Success"));
        });  

    }
}