package eu.recap.sim.helpers;
/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
//packaeu.recap.sim.helpersles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudsimplus.builders.tables.Table;
import org.cloudsimplus.builders.tables.TableBuilderAbstract;
import org.cloudsimplus.builders.tables.TableColumn;
import org.cloudsimplus.builders.tables.*;


import eu.recap.sim.cloudsim.cloudlet.RecapCloudlet;
import eu.recap.sim.models.ApplicationModel.ApplicationLandscape;
import eu.recap.sim.models.ExperimentModel.Experiment;
import eu.recap.sim.models.InfrastructureModel.Infrastructure;
import eu.recap.sim.models.WorkloadModel.Request;
import eu.recap.sim.models.WorkloadModel.Workload;

/**
 * Builds a table for printing simulation results from a list of Cloudlets.
 * It defines a set of default columns but new ones can be added
 * dynamically using the {@code addColumn()} methods.
 *
 * <p>The basic usage of the class is by calling its constructor,
 * giving a list of Cloudlets to be printed, and then
 * calling the {@link #build()} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class RecapCloudletsTableBuilder extends TableBuilderAbstract<RecapCloudlet> {
	public static int timeUnits=1000; // multiply by this to get in millisecond
	
	
    private static final String TIME_FORMAT = "%d";
    private static final String MILISECONDS = "ms";
    private static final String CPU_CORES = "CPU cores";
    
    private Infrastructure rim; 
	private ApplicationLandscape ram; 
	private Workload rwm;
	private Experiment config;
	
	HashMap<Integer, Double> timeStartingRequest = new HashMap<Integer, Double>();
	
    private List<? extends RecapCloudlet> cloudletList;
    /**
     * A Map containing a function that receives a Cloudlet and returns
     * the data to be printed from that Cloudlet to the associated column
     * of the table to be printed.
     */
    private Map<TableColumn, Function<RecapCloudlet, Object>> columnsDataFunctions;

    /**
     * Creates new helper object to print the list of cloudlets using the a
     * default {@link TextTableBuilder}.
     * To use a different {@link TableBuilder}, use the
     * alternative constructors.
     *
     * @param list the list of Cloudlets that the data will be included into the table to be printed
     */
    public RecapCloudletsTableBuilder(final List<? extends RecapCloudlet> list, Infrastructure rim, ApplicationLandscape ram, Workload rwm, Experiment config){
        super(list);
        
        this.rim=rim;
        this.ram=ram;
        this.rwm=rwm;
        this.config=config;
    }
    
    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * given {@link Table}.
     *
     * @param list the list of Cloudlets to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */
    public RecapCloudletsTableBuilder(final List<? extends RecapCloudlet> list, final Table table) {
        super(list, table);
    }


    /**
     * Creates the columns of the table and define how the data for those columns
     * will be got from a Cloudlet.
     */
    @Override
    protected void createTableColumns() {
    	
        final String ID = "ID";
        
        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
        addColumnDataFunction(getTable().addColumn("Request", ID), c -> c.getRequestId());
        addColumnDataFunction(getTable().addColumn("Application", ID), c -> c.getApplicationId());
        addColumnDataFunction(getTable().addColumn("Component", ID), c -> c.getApplicationComponentId());
        addColumnDataFunction(getTable().addColumn("Status "), cloudlet -> cloudlet.getStatus().name());
        addColumnDataFunction(getTable().addColumn("DC", ID), cloudlet -> cloudlet.getVm().getHost().getDatacenter().getId());
        addColumnDataFunction(getTable().addColumn("Host", ID), cloudlet -> cloudlet.getVm().getHost().getId());
        //addColumnDataFunction(getTable().addColumn("Host PEs ", CPU_CORES), cloudlet -> cloudlet.getVm().getHost().getNumberOfWorkingPes());
        addColumnDataFunction(getTable().addColumn("VM", ID), cloudlet -> cloudlet.getVm().getId());
        addColumnDataFunction(getTable().addColumn("VM PEs   ", CPU_CORES), cloudlet -> cloudlet.getVm().getNumberOfPes());
        addColumnDataFunction(getTable().addColumn("CloudletLen", "MI"), Cloudlet::getLength);
        addColumnDataFunction(getTable().addColumn("CloudletPEs", CPU_CORES), Cloudlet::getNumberOfPes);

        TableColumn col = getTable().addColumn("StartTime", MILISECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long) Math.floor(cloudlet.getExecStartTime()*1000/timeUnits));

        col = getTable().addColumn("FinishTime", MILISECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long) Math.floor(cloudlet.getFinishTime()*1000/timeUnits));

        col = getTable().addColumn("ExecTime", MILISECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long)Math.floor(cloudlet.getActualCpuTime() *1000/timeUnits));
        
        
        addColumnDataFunction(getTable().addColumn("TotalExecTime",MILISECONDS), 
				  		cloudlet -> {
					  			if(!timeStartingRequest.containsKey(cloudlet.getRequestId())) {
					  				timeStartingRequest.put(cloudlet.getRequestId(), cloudlet.getExecStartTime());
					  			}
					  			
					  			if(cloudlet.getApiId().equals("1_2"))
					  			{
						  			return Integer.toString((int)Math.floor( (cloudlet.getFinishTime() - timeStartingRequest.get(cloudlet.getRequestId()))*1000/timeUnits ));
					  				
					  			}
					  			else
					  			{
					  				return "";
					  			}
				  			  }
				 );
        
        addColumnDataFunction(getTable().addColumn("Final", "Y/N"), cloudlet -> cloudlet.getApiId().equals("1_2") ? "Y" : "");
        addColumnDataFunction(getTable().addColumn("ExpectedTime",MILISECONDS), 
        					  cloudlet -> cloudlet.getApiId().equals("1_2") ? Integer.toString((int)Math.floor(ModelHelpers.getRequestTask(this.rwm.getDevicesList(), cloudlet.getOriginDeviceId(),cloudlet.getRequestId()).getExpectedDuration())) : ""
        					 );
        
        
    }


}
