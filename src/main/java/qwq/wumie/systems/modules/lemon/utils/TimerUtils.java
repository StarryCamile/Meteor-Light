package qwq.wumie.systems.modules.lemon.utils;

import meteordevelopment.meteorclient.utils.world.TickRate;

public class TimerUtils {
    private long time = -1L;

    public TimerUtils reset() {
        this.time = System.nanoTime();
        return this;
    }

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }

    // Set Times
    public void setTicks(long ticks) { time = System.nanoTime() - convertTicksToNano(ticks); }


    // Get Times
    public long getTicks() { return convertNanoToTicks(time); }


    // Passed Time
    public boolean passedTicks(long ticks) { return passedNano(convertTicksToNano(ticks)); }
    public boolean passedNano(long time) { return System.nanoTime() - time >= time; }
    public boolean passedMillis(long time) { return passedNano(convertMillisToNano(time)); }


    // Tick Conversions
    public long convertMillisToTicks(long time) { return time / 50; }
    public long convertTicksToMillis(long ticks) { return ticks * 50; }
    public long convertNanoToTicks(long time) { return convertMillisToTicks(convertNanoToMillis(time)); }
    public long convertTicksToNano(long ticks) { return convertMillisToNano(convertTicksToMillis(ticks)); }


    // All Conversions To Smaller
    public long convertSecToMillis(long time) { return time * 1000L; }
    public long convertSecToNano(long time) { return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time))); }

    public long convertMillisToMicro(long time) { return time * 1000L; }
    public long convertMillisToNano(long time) { return convertMicroToNano(convertMillisToMicro(time)); }

    public long convertMicroToNano(long time) { return time * 1000L; }


    // All Conversions To Larger
    public long convertNanoToMicro(long time) { return time / 1000L; }
    public long convertNanoToMillis(long time) { return convertMicroToMillis(convertNanoToMicro(time)); }
    public long convertNanoToSec(long time) { return convertMillisToSec(convertMicroToMillis(convertNanoToMicro(time))); }

    public long convertMicroToMillis(long time) { return time / 1000L; }

    public long convertMillisToSec(long time) { return time / 1000L; }

    public static double getTPSMatch(boolean TPSSync) {
        return TPSSync ? (TickRate.INSTANCE.getTickRate() / 20) : 1;
    }
}
