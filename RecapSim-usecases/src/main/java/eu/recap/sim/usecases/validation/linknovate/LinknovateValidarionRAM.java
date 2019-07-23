/**
 * 
 */
package eu.recap.sim.usecases.validation.linknovate;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Field;
import com.google.protobuf.UnknownFieldSet;

import eu.recap.sim.models.ApplicationModel.Application;
import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.ApplicationModel.Deployment;
import eu.recap.sim.models.ApplicationModel.VeFlavour;
import eu.recap.sim.models.ApplicationModel.Application.Component;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;
import eu.recap.sim.models.InfrastructureModel.Node;
import eu.recap.sim.models.InfrastructureModel.ResourceSite;

/**
 * @author Malika
 *
 */
public class LinknovateValidarionRAM {
	public static int timeUnits=1000; // multiply by this to get in millisecond
	
	/*
	 * LINKNOVATE
	 */
	
	/**
		
	* Application model consists of: 
	* 1x Web Server (98F9): 8 cores, 28 GB RAM, 181 GB storage
	* 1x ES Client (77A0): 16 cores, 112 GB RAM, 181 GB storage
	* 6x Data Nodes (2A17, BA99, F421, 9A4F, 318C, AB2F): 8 cores, 28 GB RAM, 181 GB storage
		
	 * @param applicationQty
	 * @param componentQty
	 * @param rim the infrastructure model to source nodes for component deployment
	 * @return
	 */
	public static ApplicationLandscape GenerateLinknovateValidationApplication(int applicationQty, Infrastructure rim) {
		
		
		
		//All VMs are the same
		int vmCores = 8;
		int vmMemory = 28_000;
		int vmStorage = 181_000;
		
		/////////////////////
		///// Cloudlets default values
		//////////////////////
		
		//resource consumption going from client to web server
		int clientToWebServer_mips = 300*timeUnits/10;
		int clientToWebServer_iops = 1;
		int clientToWebServer_ram = 200;//500
		int clientToWebServer_transferData = 1*timeUnits;
				
		//resource consumption going from web server to ES
		int webServerToES_mips = 300*timeUnits/10;
		int webServerToES_iops = 1;
		int webServerToES_ram = 200;//500
		int webServerToES_transferData = 1*timeUnits;
		
		
		//resource consumption going from ES to DataNode
		int ESToDataNode_mips = 1*timeUnits/10;
		int ESToDataNode_iops = 1;
		int ESToDataNode_ram = 200; //2000
		int ESToDataNode_transferData = 1*timeUnits;
		
		
		//resource consumption going from DataNode to ES
		int DataNodeToES_mips = 300*timeUnits/10;
		int DataNodeToES_iops = 1;
		int DataNodeToES_ram = 200;//1000
		int DataNodeToES_transferData = 1*timeUnits;
		
		
		//resource consumption going from ES to Web Server
		int ESToWebServer_mips = 300*timeUnits/10;
		int ESToWebServer_iops = 1;
		int ESToWebServer_ram = 200;//500
		int ESToWebServer_transferData = 1*timeUnits;
		
		List<String> nodeIds = new ArrayList<String>();
		
		for (ResourceSite site:rim.getSitesList()){
			for (Node node: site.getNodesList()){
				nodeIds.add(node.getId());
				
			}
			
		}
		
		int indexNmberOfNodes = nodeIds.size()-1;
		int indexNmberOfNodesCounter=0;

		ApplicationLandscape.Builder applicationList = ApplicationLandscape.newBuilder();
		applicationList.setNotes("linknovate");
		int appCounter=1;
		while(applicationQty!=appCounter-1){
			
			Application.Builder application = Application.newBuilder();
			application.setApplicationId(appCounter+"");
			application.setApplicationName(appCounter+"");
			
			//Web server (VM1)
			Component.Builder webServer = Component.newBuilder();
			webServer.setComponentName("Web Server (98F9)");
			webServer.setComponentId("1");
			webServer.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_webServer = Deployment.newBuilder();
			deployment_webServer.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
			
			webServer.setDeployment(deployment_webServer.build());
			
			//create 2 API paths (VM1)
			
			//path 1
			String apiId="1_1"; //Component ID, API ID
			Component.Api.Builder webServerApi_1 = Component.Api.newBuilder();
			webServerApi_1.setApiId(apiId);
			webServerApi_1.setApiName(webServer.getComponentName()+"_"+apiId);
			//resource consumption
			webServerApi_1.setMips(clientToWebServer_mips);
			webServerApi_1.setIops(clientToWebServer_iops);
			webServerApi_1.setRam(clientToWebServer_ram);
			webServerApi_1.setDataToTransfer(clientToWebServer_transferData);
			
			//connect to next api
			webServerApi_1.addNextComponentId("2");
			webServerApi_1.addNextApiId("2_1");
			webServer.addApis(webServerApi_1.build());
			
			//return
			//path 2
			apiId="1_2"; //Component ID, API ID
			Component.Api.Builder webServerApi_7 = Component.Api.newBuilder();
			webServerApi_7.setApiId(apiId);
			webServerApi_7.setApiName(webServer.getComponentName()+"_"+apiId);
			//resource consumption
			webServerApi_7.setMips(ESToWebServer_mips);
			webServerApi_7.setIops(ESToWebServer_iops);
			webServerApi_7.setRam(ESToWebServer_ram);
			webServerApi_7.setDataToTransfer(ESToWebServer_transferData);
			//connect to next api
			// no add of next component
			// no add of next api
			webServer.addApis(webServerApi_7.build());
			
			
			
			//create flavour
			VeFlavour.Builder veFlavour_controlPlane = VeFlavour.newBuilder();
			veFlavour_controlPlane.setCores(vmCores);
			veFlavour_controlPlane.setMemory(vmMemory);
			veFlavour_controlPlane.setStorage(vmStorage);
			
			webServer.setFlavour(veFlavour_controlPlane.build());
			application.addComponents(webServer.build());

			//ES Client (VM2)
			Component.Builder esClient = Component.newBuilder();
			esClient.setComponentName("ES Client (77A0)");
			esClient.setComponentId("2");
			esClient.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_esClient = Deployment.newBuilder();
			deployment_esClient.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
			
			esClient.setDeployment(deployment_esClient.build());
			
			//create 2 API paths (VM2)
			
			//path 1
			apiId="2_1"; //Component ID, API ID
			Component.Api.Builder esClientApi_1 = Component.Api.newBuilder();
			esClientApi_1.setApiId(apiId);
			esClientApi_1.setApiName(esClient.getComponentName()+"_"+apiId);
			//resource consumption
			esClientApi_1.setMips(webServerToES_mips);
			esClientApi_1.setIops(webServerToES_iops);
			esClientApi_1.setRam(webServerToES_ram);
			esClientApi_1.setDataToTransfer(webServerToES_transferData);
			//connect to next api
			esClientApi_1.addNextComponentId("3");
			esClientApi_1.addNextApiId("3_1");
			esClientApi_1.addNextComponentId("4");
			esClientApi_1.addNextApiId("4_1");
			esClientApi_1.addNextComponentId("5");
			esClientApi_1.addNextApiId("5_1");
			esClientApi_1.addNextComponentId("6");
			esClientApi_1.addNextApiId("6_1");
			esClientApi_1.addNextComponentId("7");
			esClientApi_1.addNextApiId("7_1");
			esClientApi_1.addNextComponentId("8");
			esClientApi_1.addNextApiId("8_1");
			
			esClient.addApis(esClientApi_1.build());
			
			
			//return
			//path 2
			apiId="2_2"; //Component ID, API ID
			Component.Api.Builder esClientApi_7 = Component.Api.newBuilder();
			esClientApi_7.setApiId(apiId);
			esClientApi_7.setApiName(esClient.getComponentName()+"_"+apiId);
			//resource consumption
			esClientApi_7.setMips(DataNodeToES_mips);
			esClientApi_7.setIops(DataNodeToES_iops);
			esClientApi_7.setRam(DataNodeToES_ram);
			esClientApi_7.setDataToTransfer(DataNodeToES_transferData);
			//connect to next api
			esClientApi_7.addNextComponentId("1");
			esClientApi_7.addNextApiId("1_2");
			esClient.addApis(esClientApi_7.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_esClient = VeFlavour.newBuilder();
			veFlavour_esClient.setCores(16);
			veFlavour_esClient.setMemory(112_000);
			veFlavour_esClient.setStorage(181_000);
			
			esClient.setFlavour(veFlavour_esClient.build());
			application.addComponents(esClient.build());
			
			
			//#### COMPONENTS 3-8 ####### Each with single API
			//Shard 1 (VM3)
			Component.Builder shard1 = Component.newBuilder();
			shard1.setComponentName("Shard_1 (2A17)");
			shard1.setComponentId("3");
			shard1.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard1 = Deployment.newBuilder();
			deployment_shard1.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard1.setDeployment(deployment_shard1.build());
			
			//create 1 API
			apiId="3_1"; //Component ID, API ID
			Component.Api.Builder shard1Api_1 = Component.Api.newBuilder();
			shard1Api_1.setApiId(apiId);
			shard1Api_1.setApiName(shard1.getComponentName()+"_"+apiId);
			//resource consumption
			shard1Api_1.setMips(ESToDataNode_mips);
			shard1Api_1.setIops(ESToDataNode_iops);
			shard1Api_1.setRam(ESToDataNode_ram);
			shard1Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard1Api_1.addNextComponentId("2");
			shard1Api_1.addNextApiId("2_2");
			shard1.addApis(shard1Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard1 = VeFlavour.newBuilder();
			veFlavour_shard1.setCores(vmCores);
			veFlavour_shard1.setMemory(vmMemory);
			veFlavour_shard1.setStorage(vmStorage);
			shard1.setFlavour(veFlavour_shard1.build());
			application.addComponents(shard1.build());
			
			//Shard 2 (VM4)
			Component.Builder shard2 = Component.newBuilder();
			shard2.setComponentName("Shard_2 (BA99)");
			shard2.setComponentId("4");
			shard2.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard2 = Deployment.newBuilder();
			deployment_shard2.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard2.setDeployment(deployment_shard2.build());
			
			//create 1 API
			apiId="4_1"; //Component ID, API ID
			Component.Api.Builder shard2Api_1 = Component.Api.newBuilder();
			shard2Api_1.setApiId(apiId);
			shard2Api_1.setApiName(shard1.getComponentName()+"_"+apiId);
			//resource consumption
			shard2Api_1.setMips(ESToDataNode_mips);
			shard2Api_1.setIops(ESToDataNode_iops);
			shard2Api_1.setRam(ESToDataNode_ram);
			shard2Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard2Api_1.addNextComponentId("2");
			shard2Api_1.addNextApiId("2_2");
			shard2.addApis(shard2Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard2 = VeFlavour.newBuilder();
			veFlavour_shard2.setCores(vmCores);
			veFlavour_shard2.setMemory(vmMemory);
			veFlavour_shard2.setStorage(vmStorage);
			shard2.setFlavour(veFlavour_shard2.build());
			application.addComponents(shard2.build());
			
			//Shard 3 (VM5)
			Component.Builder shard3 = Component.newBuilder();
			shard3.setComponentName("Shard_3 (F421)");
			shard3.setComponentId("5");
			shard3.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard3 = Deployment.newBuilder();
			deployment_shard3.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard3.setDeployment(deployment_shard3.build());
			
			//create 1 API
			apiId="5_1"; //Component ID, API ID
			Component.Api.Builder shard3Api_1 = Component.Api.newBuilder();
			shard3Api_1.setApiId(apiId);
			shard3Api_1.setApiName(shard1.getComponentName()+"_"+apiId);
			//resource consumption
			shard3Api_1.setMips(ESToDataNode_mips);
			shard3Api_1.setIops(ESToDataNode_iops);
			shard3Api_1.setRam(ESToDataNode_ram);
			shard3Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard3Api_1.addNextComponentId("2");
			shard3Api_1.addNextApiId("2_2");
			shard3.addApis(shard3Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard3 = VeFlavour.newBuilder();
			veFlavour_shard3.setCores(vmCores);
			veFlavour_shard3.setMemory(vmMemory);
			veFlavour_shard3.setStorage(vmStorage);
			shard3.setFlavour(veFlavour_shard3.build());
			application.addComponents(shard3.build());
			
			
			//Shard 4 (VM6)
			Component.Builder shard4 = Component.newBuilder();
			shard4.setComponentName("Shard_4 (F421)");
			shard4.setComponentId("6");
			shard4.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard4 = Deployment.newBuilder();
			deployment_shard4.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard4.setDeployment(deployment_shard4.build());
			
			//create 1 API
			apiId="6_1"; //Component ID, API ID
			Component.Api.Builder shard4Api_1 = Component.Api.newBuilder();
			shard4Api_1.setApiId(apiId);
			shard4Api_1.setApiName(shard1.getComponentName()+"_"+apiId);
			//resource consumption
			shard4Api_1.setMips(ESToDataNode_mips);
			shard4Api_1.setIops(ESToDataNode_iops);
			shard4Api_1.setRam(ESToDataNode_ram);
			shard4Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard4Api_1.addNextComponentId("2");
			shard4Api_1.addNextApiId("2_2");
			shard4.addApis(shard4Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard4 = VeFlavour.newBuilder();
			veFlavour_shard4.setCores(vmCores);
			veFlavour_shard4.setMemory(vmMemory);
			veFlavour_shard4.setStorage(vmStorage);
			shard4.setFlavour(veFlavour_shard4.build());
			application.addComponents(shard4.build());
			
			
			//Shard 5 (VM7)
			Component.Builder shard5 = Component.newBuilder();
			shard5.setComponentName("Shard_5 (F521)");
			shard5.setComponentId("7");
			shard5.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard5 = Deployment.newBuilder();
			deployment_shard5.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard5.setDeployment(deployment_shard5.build());
			
			//create 1 API
			apiId="7_1"; //Component ID, API ID
			Component.Api.Builder shard5Api_1 = Component.Api.newBuilder();
			shard5Api_1.setApiId(apiId);
			shard5Api_1.setApiName(shard1.getComponentName()+"_"+apiId);
			//resource consumption
			shard5Api_1.setMips(ESToDataNode_mips);
			shard5Api_1.setIops(ESToDataNode_iops);
			shard5Api_1.setRam(ESToDataNode_ram);
			shard5Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard5Api_1.addNextComponentId("2");
			shard5Api_1.addNextApiId("2_2");
			shard5.addApis(shard5Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard5 = VeFlavour.newBuilder();
			veFlavour_shard5.setCores(vmCores);
			veFlavour_shard5.setMemory(vmMemory);
			veFlavour_shard5.setStorage(vmStorage);
			shard5.setFlavour(veFlavour_shard5.build());
			application.addComponents(shard5.build());
			
			//Shard 6 (VM8)
			Component.Builder shard6 = Component.newBuilder();
			shard6.setComponentName("Shard_6 (F621)");
			shard6.setComponentId("8");
			shard6.setIsLoadbalanced(false);
			
			//deploy on consecutive nodes
			Deployment.Builder deployment_shard6 = Deployment.newBuilder();
			deployment_shard6.setNodeId(nodeIds.get(indexNmberOfNodesCounter));
			//reset or advance counter
			if(indexNmberOfNodesCounter==indexNmberOfNodes){
				indexNmberOfNodesCounter =0;
			}else{
				indexNmberOfNodesCounter++;
			}
	
			shard6.setDeployment(deployment_shard6.build());
			
			//create 1 API
			apiId="8_1"; //Component ID, API ID
			Component.Api.Builder shard6Api_1 = Component.Api.newBuilder();
			shard6Api_1.setApiId(apiId);
			shard6Api_1.setApiName(shard6.getComponentName()+"_"+apiId);
			//resource consumption
			shard6Api_1.setMips(ESToDataNode_mips);
			shard6Api_1.setIops(ESToDataNode_iops);
			shard6Api_1.setRam(ESToDataNode_ram);
			shard6Api_1.setDataToTransfer(ESToDataNode_transferData);
			//connect to next api TO-DO
			shard6Api_1.addNextComponentId("2");
			shard6Api_1.addNextApiId("2_2");
			shard6.addApis(shard6Api_1.build());
			
			//create flavour
			VeFlavour.Builder veFlavour_shard6 = VeFlavour.newBuilder();
			veFlavour_shard6.setCores(vmCores);
			veFlavour_shard6.setMemory(vmMemory);
			veFlavour_shard6.setStorage(vmStorage);
			shard6.setFlavour(veFlavour_shard6.build());
			application.addComponents(shard6.build());
						
			applicationList.addApplications(application.build());
			
			appCounter++;
		}
		
		
		return applicationList.build();

	}


}
