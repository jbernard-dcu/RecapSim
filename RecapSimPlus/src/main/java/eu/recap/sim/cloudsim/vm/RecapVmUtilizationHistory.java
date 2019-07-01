package eu.recap.sim.cloudsim.vm;

import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.UtilizationHistory;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Stores resource utilization data for a specific {@link Vm}.
 * With added disk and network result storage 
 * @author Sergej Svorobej
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.4
 */
public class RecapVmUtilizationHistory implements UtilizationHistory {
    private boolean enabled;
    private int maxHistoryEntries;

    /**
     * @see #getHistory()
     */
    //CPU
    private final SortedMap<Double, Double> history;
    //Network
    private final SortedMap<Double, Double> networkHistory;
    //Disk
    private final SortedMap<Double, Double> diskHistory;
    private final Vm vm;

    /**
     * The previous time that cloudlets were processed.
     */
    private double previousTime;

    /**
     * Instantiates the class to store resource utilization history for a specific {@link Vm}.
     *
     * @param vm the vm to instantiates the object to store utilization history
     * @param enabled true if the history must be enabled by default, enabling usage
     *                history to be collected and stored;
     *                false if it must be disabled to avoid storing any history,
     *                in order to reduce memory usage
     */
    public RecapVmUtilizationHistory(final Vm vm, final boolean enabled) {
        this.history = new TreeMap<>();
        this.networkHistory = new TreeMap<>();
        this.diskHistory = new TreeMap<>();
        
        this.vm = vm;
        this.enabled = enabled;
        this.setMaxHistoryEntries(Integer.MAX_VALUE);
    }

    public RecapVmUtilizationHistory(final Vm vm) {
        this(vm, true);
    }

    @Override
    public double getUtilizationMad() {
        if (history.isEmpty()) {
            return 0;
        }

        final int maxEntries = getNumEntriesToComputeStats();
        final double median = MathUtil.median(getHistory().values());
        final double[] deviationSum = new double[maxEntries];
        for (int i = 0; i < maxEntries; i++) {
            deviationSum[i] = Math.abs(median - getHistory().get(i));
        }

        return MathUtil.median(deviationSum);
    }

    /**
     * Gets the actual number of entries to be used to compute statistics.
     * @return
     */
    private int getNumEntriesToComputeStats() {
        return Math.min(getMaxHistoryEntries(), getHistory().size());
    }

    @Override
    public double getUtilizationMean() {
        final int maxEntries = getNumEntriesToComputeStats();
        final double usagePercentMean = getHistoryLimitedStream(maxEntries)
            .mapToDouble(usagePercent -> usagePercent)
            .average()
            .orElse(0);

        return usagePercentMean * vm.getMips();
    }

    private Stream<Double> getHistoryLimitedStream(final int maxEntries) {
        return getHistory().values().stream().limit(maxEntries);
    }

    @Override
    public double getUtilizationVariance() {
        if (history.isEmpty()) {
            return 0;
        }

        final double mean = getUtilizationMean();
        final int maxEntries = getNumEntriesToComputeStats();
        return getHistoryLimitedStream(maxEntries)
            .mapToDouble(usagePercent -> usagePercent * vm.getMips())
            .map(usageValue -> usageValue - mean)
            .map(usageValue -> usageValue * usageValue)
            .average().orElse(0);
    }

    @Override
    public void addUtilizationHistory(double time) {
        if (!enabled || isNotTimeToAddHistory(time)) {
            return;
        }

        final double utilization = vm.getCpuPercentUsage(vm.getCloudletScheduler().getPreviousTime());
        time = vm.isIdle() ? time : (int)time;
        addUtilizationHistoryValue(time, utilization);
        this.previousTime = time;
    }

    /**
     * Checks if it isn't time to add a value to the utilization history.
     * The utilization history is not updated in any one of the following conditions is met:
     * <ul>
     * <li>the simulation clock was not changed yet;</li>
     * <li>the time passed is smaller than one second and the VM is not idle;</li>
     * <li>the floor time is equal to the previous time and VM is not idle.</li>
     * </ul>
     *
     * <p>If the time is smaller than one second and the VM became idle,
     * the history will be added so that we know what is the resource
     * utilization when the VM became idle.
     * This way, we can see clearly in the history when the VM was busy
     * and when it became idle.</p>
     *
     * <p>If the floor time is equal to the previous time and VM is not idle,
     * that means not even a second has passed. This way,
     * that utilization will not be stored.</p>
     *
     * @param time the current simulation time
     * @return true if it's time to add utilization history, false otherwise
     */
    private boolean isNotTimeToAddHistory(final double time) {
        return time <= 0 ||
               isElapsedTimeSmall(time) ||
               isNotEntireSecondElapsed(time);
    }

    private boolean isElapsedTimeSmall(final double time) {
        return time - previousTime < 1 && !vm.isIdle();
    }

    private boolean isNotEntireSecondElapsed(final double time) {
        return Math.floor(time) == previousTime && !vm.isIdle();
    }

    /**
     * Adds a CPU utilization percentage history value.
     *
     * @param time the time this utilization was collected
     * @param utilizationPercent the CPU utilization percentage to add
     */
    private void addUtilizationHistoryValue(final double time, final double utilizationPercent) {
        history.put(time, utilizationPercent);
        if (getHistory().size() > maxHistoryEntries) {
            history.remove(maxHistoryEntries);
        }
    }

    @Override
    public SortedMap<Double, Double> getHistory() {
        return Collections.unmodifiableSortedMap(history);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public int getMaxHistoryEntries() {
        return maxHistoryEntries;
    }

    @Override
    public void setMaxHistoryEntries(final int maxHistoryEntries) {
        this.maxHistoryEntries = maxHistoryEntries;
    }

    @Override
    public Vm getVm() {
        return vm;
    }
}
