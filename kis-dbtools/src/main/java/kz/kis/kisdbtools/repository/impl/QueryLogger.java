package kz.kis.kisdbtools.repository.impl;

/**
 * Утилиты для логгирования запроса
 */
public class QueryLogger {

    private long timeStart;
    private long timeStop;

    public void logStart() {
        timeStart = System.currentTimeMillis();
    }

    public void logStop() {
        this.timeStop = System.currentTimeMillis();
    }

    public String durationMsec() {
        long duration = this.timeStop - timeStart;
        String durationStr = duration + " ms";
        return durationStr;
    }

}
