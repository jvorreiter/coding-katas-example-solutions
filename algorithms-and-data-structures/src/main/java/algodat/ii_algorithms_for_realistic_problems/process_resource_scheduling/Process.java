package algodat.ii_algorithms_for_realistic_problems.process_resource_scheduling;

public class Process {
    private final Resource[] requiredResources;
    private final int id;

    private int startTime;
    private final int executionTime;

    public Process(int id, int startTime, int executionTime, Resource... requiredResources) {
        this.id = id;
        this.startTime = startTime;
        this.executionTime = executionTime;
        this.requiredResources = requiredResources;
    }

    public Resource[] getRequiredResources() {
        return requiredResources;
    }

    public int getId() {
        return id;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public int getEndTime() {
        return this.startTime + this.executionTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
