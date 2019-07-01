1) Utilization for IOPS and BW extraction from VMs:
hostSimple -> updateProcessing -> getUtilizationOfCpuMips()
				->return vmList.stream().mapToDouble(Vm::getTotalCpuMipsUsage).sum();
				
				-> VMSimple
					    public double getTotalCpuMipsUsage() {
        return getTotalCpuMipsUsage(getSimulation().clock());
    }
					
    public double updateProcessing(final double currentTime) {
        setPreviousUtilizationMips(getUtilizationOfCpuMips());
        double nextSimulationTime = Double.MAX_VALUE;
        /* Uses a traditional for to avoid ConcurrentModificationException,
         * e.g., in cases when Vm is destroyed during simulation execution.*/
        for (int i = 0; i < vmList.size(); i++) {
            final Vm vm = vmList.get(i);
            final double nextTime = vm.updateProcessing(currentTime, vmScheduler.getAllocatedMips(vm));
            nextSimulationTime = Math.min(nextTime, nextSimulationTime);
        }

        notifyOnUpdateProcessingListeners(nextSimulationTime);
        addStateHistory(currentTime);

        return nextSimulationTime;
    }

    
#### 
2)UtilizationModel needs to be checked as if MIPS of cloudlet are too low consecutive cloudlets are not being scheduled by broker (maybe custom solution is needed)
private IRecapCloudlet createCloudlet(Vm vm, long mi, long inputFileSize, long outputFileSize, long io,
			double submissionDelay, String applicationId, String componentId, String apiId) {
		// final long length = 10000; //in Million Structions (MI)
		// final long fileSize = 300; //Size (in bytes) before execution
		// final long outputSize = 300; //Size (in bytes) after execution
		final int numberOfCpuCores = (int) vm.getNumberOfPes(); // cloudlet will
																// use all the
																// VM's CPU
																// cores

		// Defines how CPU, RAM and Bandwidth resources are used
		// Sets the same utilization model for all these resources.
		//UtilizationModel utilization = new UtilizationModelStochastic();

		IRecapCloudlet recapCloudlet = (IRecapCloudlet) new RecapCloudlet(cloudletList.size(), mi, numberOfCpuCores)
				.setFileSize(inputFileSize).setOutputSize(outputFileSize).setUtilizationModel(new UtilizationModelDynamic(0.1)).setVm(vm)
				.addOnFinishListener(this::onCloudletFinishListener);

		recapCloudlet.setSubmissionDelay(submissionDelay);
		recapCloudlet.setApplicationId(applicationId);
		recapCloudlet.setApplicationComponentId(componentId);
		recapCloudlet.setApiId(apiId);

		return recapCloudlet;
	}