package request.impl;

import com.google.gson.annotations.Expose;
import request.api.RequestStatus;

public class AllocationRequest implements Comparable<AllocationRequest> {
    private int requestID;
    @Expose
    private String simulationName;
    @Expose
    private int numOfRuns;
    private RequestStatus status;
    @Expose
    private int secondsToRun;
    @Expose
    private int ticksToRun;
    @Expose
    private String username;

    public AllocationRequest(String simulationName, int numOfRuns, int amountTick, int amountTime, String username) {
        this.requestID = -1;
        this.simulationName = simulationName;
        this.numOfRuns = numOfRuns;
        this.status = RequestStatus.WAITING;
        this.ticksToRun = amountTick;
        this.secondsToRun = amountTime;
        this.username = username;

    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void approveRequest() {
        if (this.status.equals(RequestStatus.WAITING)) {
            this.status = RequestStatus.APPROVED;
        }
    }

    public void denyRequest() {
        if (this.status.equals(RequestStatus.WAITING)) {
            this.status = RequestStatus.DENIED;
        }
    }

    @Override
    public int compareTo(AllocationRequest o) {
        return this.requestID - o.requestID;
    }

    @Override
    public String toString() {
        return "Request ID: " + requestID;

    }
    public String describeRequest() {
        StringBuilder result = new StringBuilder()
                .append("Simulation Name: " + simulationName)
                .append("\nRequest ID: " + requestID)
                .append("\nNumber of runs: " + numOfRuns);
        if (secondsToRun == Integer.MAX_VALUE && ticksToRun == Integer.MAX_VALUE) {
            result.append("\nTermination: By User");
        } else if (secondsToRun == Integer.MAX_VALUE) {
            result.append("\nTermination: By ").append(ticksToRun).append(" Ticks");
        } else if (ticksToRun == Integer.MAX_VALUE) {
            result.append("\nTermination: By ").append(secondsToRun).append(" Seconds");
        } else {
            result.append("\nTermination: By ").append(ticksToRun).append(" Ticks and ").append(secondsToRun).append(" Seconds");
        }
        result.append("\nStatus: " + this.status);
        result.append("\nRequested by: " + this.username);
        return String.valueOf(result);
    }

    public int getRequestID() {
        return requestID;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public int getNumOfRuns() {
        return numOfRuns;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public int getSecondsToRun() {
        return secondsToRun;
    }

    public int getTicksToRun() {
        return ticksToRun;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AllocationRequest) {
            AllocationRequest other = (AllocationRequest) obj;
            if(this.username.equalsIgnoreCase(other.username)) {
                return this.requestID == other.requestID;
            }
        }
        return false;
    }
}