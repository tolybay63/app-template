package kz.app.appcore.utils;

import kz.app.appcore.utils.consts.FD_PeriodType_consts;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;

//@Component
public class UtPeriod {

    /**
     * Возвращает корректный dbeg для PeriodType по заданной дате и количеству периодов
     *
     * @param date точка отсчета
     * @param periodType тип периода
     * @param countPeriod количество периодов (если 0 — текущий период для date,
     *                    если >0 — добавляются периоды, если <0 — отнимаются)
     * @return дата начала периода
     **/
    public LocalDate calcDbeg(LocalDate date, long periodType, int countPeriod) {
        if (periodType == FD_PeriodType_consts.month) {
            return calcDbegMonth(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.year) {
            return calcDbegYear(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.halfyear) {
            return calcDbegHalfYear(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.quarter) {
            return calcDbegQuarter(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.decade) {
            return calcDbegDecada(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.week) {
            return calcDbegWeek(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.day) {
            return calcDbegDay(countPeriod, date);
        }
        throw new IllegalArgumentException("Unknown periodType: " + periodType);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество месяцев
    private LocalDate calcDbegMonth(int countPeriod, LocalDate dend) {
        return dend.plusMonths(countPeriod).withDayOfMonth(1);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество лет
    private LocalDate calcDbegYear(int countPeriod, LocalDate dend) {
        return dend.plusYears(countPeriod).withDayOfYear(1);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество полугодий
    private LocalDate calcDbegHalfYear(int countPeriod, LocalDate dend) {
        LocalDate curDbeg;
        if (dend.getMonthValue() <= 6) {
            curDbeg = LocalDate.of(dend.getYear(), 1, 1);
        } else {
            curDbeg = LocalDate.of(dend.getYear(), 7, 1);
        }
        return curDbeg.plusMonths((long) countPeriod * 6L);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество кварталов
    private LocalDate calcDbegQuarter(int countPeriod, LocalDate dend) {
        LocalDate curDbeg;
        int m = dend.getMonthValue();
        if (m <= 3) {
            curDbeg = LocalDate.of(dend.getYear(), 1, 1);
        } else if (m <= 6) {
            curDbeg = LocalDate.of(dend.getYear(), 4, 1);
        } else if (m <= 9) {
            curDbeg = LocalDate.of(dend.getYear(), 7, 1);
        } else {
            curDbeg = LocalDate.of(dend.getYear(), 10, 1);
        }
        return curDbeg.plusMonths((long) countPeriod * 3L);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество декад
    private LocalDate calcDbegDecada(int countPeriod, LocalDate date) {
        int curTenDay;
        int day = date.getDayOfMonth();
        if (day <= 10) {
            curTenDay = 1;
        } else if (day <= 20) {
            curTenDay = 2;
        } else {
            curTenDay = 3;
        }
        LocalDate startOfMonth = date.withDayOfMonth(1);
        long shift = (long) countPeriod * 10L + 10L * (curTenDay - 1);
        return startOfMonth.plusDays(shift);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество дней
    private LocalDate calcDbegDay(int countPeriod, LocalDate dend) {
        return dend.plusDays(countPeriod);
    }

    /**
     * Возвращает корректный dend для PeriodType по заданной дате и количеству периодов
     *
     * @param date точка отсчета
     * @param periodType тип периода
     * @param countPeriod количество периодов (если 0 — текущий период для date,
     *                    если >0 — добавляются периоды, если <0 — отнимаются)
     * @return дата конца периода
     */
    public LocalDate calcDend(LocalDate date, long periodType, int countPeriod) {
        if (periodType == FD_PeriodType_consts.month) {
            return calcDendMonth(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.year) {
            return calcDendYear(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.halfyear) {
            return calcDendHalfYear(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.quarter) {
            return calcDendQuarter(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.decade) {
            return calcDendDecada(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.week) {
            return calcDendWeek(countPeriod, date);
        } else if (periodType == FD_PeriodType_consts.day) {
            return calcDendDay(countPeriod, date);
        }
        throw new IllegalArgumentException("Unknown periodType: " + periodType);
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество месяцев
    private LocalDate calcDendMonth(int lagCurrentDate, LocalDate date) {
        return date.plusMonths(lagCurrentDate).with(TemporalAdjusters.lastDayOfMonth());
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество лет
    private LocalDate calcDendYear(int lagCurrentDate, LocalDate date) {
        LocalDate d = date.plusYears(lagCurrentDate);
        return LocalDate.of(d.getYear(), 12, 31);
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество полугодий
    private LocalDate calcDendHalfYear(int lagCurrentDate, LocalDate date) {
        LocalDate res = date;
        LocalDate curDend;
        if (res.getMonthValue() <= 6) {
            curDend = LocalDate.of(res.getYear(), 6, 30);
        } else {
            curDend = LocalDate.of(res.getYear(), 12, 31);
        }
        LocalDate shifted = curDend.plusMonths((long) lagCurrentDate * 6L);
        return shifted.with(TemporalAdjusters.lastDayOfMonth());
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество кварталов
    private LocalDate calcDendQuarter(int lagCurrentDate, LocalDate date) {
        LocalDate res = date;
        int plusMonth;
        int m = res.getMonthValue();
        if (m <= 3) {
            plusMonth = 3 - m;
        } else if (m <= 6) {
            plusMonth = 6 - m;
        } else if (m <= 9) {
            plusMonth = 9 - m;
        } else {
            plusMonth = 12 - m;
        }
        LocalDate curQuarterEnd = res.plusMonths(plusMonth).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate shifted = curQuarterEnd.plusMonths((long) lagCurrentDate * 3L);
        return shifted.with(TemporalAdjusters.lastDayOfMonth());
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество декад
    private LocalDate calcDendDecada(int lagCurrentDate, LocalDate date) {
        int curTenDay;
        int day = date.getDayOfMonth();
        if (day <= 10) {
            curTenDay = 1;
        } else if (day <= 20) {
            curTenDay = 2;
        } else {
            curTenDay = 3;
        }
        LocalDate startOfMonth = date.withDayOfMonth(1);
        long shift = (long) lagCurrentDate * 10L + 10L * (curTenDay - 1) + 9L;
        return startOfMonth.plusDays(shift);
    }

    // вычисляет dbeg по lagCurrentDate, когда lagCurrentDate количество недель
    private LocalDate calcDbegWeek(int lagCurrentDate, LocalDate date) {
        int curWeekDay;
        int day = date.getDayOfMonth();
        if (day <= 7) {
            curWeekDay = 1;
        } else if (day <= 14) {
            curWeekDay = 2;
        } else if (day <= 21) {
            curWeekDay = 3;
        } else {
            curWeekDay = 4;
        }
        LocalDate d0 = date.withDayOfMonth(1).plusDays((long) lagCurrentDate * 7L);
        while (getDayNumberWeek(d0) != 1) {
            d0 = d0.minusDays(1);
        }
        long diff = ChronoUnit.DAYS.between(d0, date);
        if (diff < 7)
            return d0;
        else
            return d0.plusDays((long) curWeekDay * 7L);
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество недель
    private LocalDate calcDendWeek(int lagCurrentDate, LocalDate date) {
        int curWeekDay;
        int day = date.getDayOfMonth();
        if (day <= 7) {
            curWeekDay = 1;
        } else if (day <= 14) {
            curWeekDay = 2;
        } else if (day <= 21) {
            curWeekDay = 3;
        } else {
            curWeekDay = 4;
        }
        LocalDate d0 = date.withDayOfMonth(1).plusDays((long) lagCurrentDate * 7L);
        while (getDayNumberWeek(d0) != 1) {
            d0 = d0.minusDays(1);
        }
        long diff = ChronoUnit.DAYS.between(d0, date);
        if (diff < 7)
            return d0.plusDays(6);
        else
            return d0.plusDays((long) curWeekDay * 7L + 6L);
    }

    // вычисляет dend по lagCurrentDate, когда lagCurrentDate количество дней
    private LocalDate calcDendDay(int lagCurrentDate, LocalDate date) {
        return date.plusDays(lagCurrentDate);
    }

    protected static int getDayNumberWeek(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue();
    }

}
