package engine.general.object;

import java.sql.Time;

public class Termination {
    private final int ticks;
    private final long howManySecondsToRun;

    private boolean isUserInteractive = false;

    public Termination(int ticks, int howManySecondsToRun) {
        this.ticks = ticks;
        this.howManySecondsToRun = howManySecondsToRun * 1000L;
    }

    public Termination(boolean isUserInteractive) {
        this.ticks = 0;
        this.howManySecondsToRun = 0;
        this.isUserInteractive = true;
    }

    public boolean getTermination(int currentTick, long outerTimeInMillis) {
        if(isUserInteractive) {
            return true;
        } else {
            long currentTime = System.currentTimeMillis();
            currentTime -= outerTimeInMillis;
            return (currentTick <= this.ticks && currentTime <= this.howManySecondsToRun);
        }
    }

    public boolean getIsInteractive() { return this.isUserInteractive;}

    public int getAllTicks() {
        return ticks;
    }

    public long getHowManySecondsToRun() {
        return howManySecondsToRun/1000L;
    }
}
