package kz.app.appcore.utils;

import kz.app.appcore.utils.consts.FD_PeriodType_consts;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

//@Component
public class PeriodGenerator {

    public static final long arabicFull = 3;
    public static final long arabicShort = 4;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String t(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }

    /**
     * По дате начала, дате конца, типу периода и шаблону формирует его наименование
     *
     * @param dbeg       начало
     * @param dend       конец
     * @param periodType тип периода
     * @param pattern    id шаблона
     * @return назнвание периода
     */
    public String getPeriodName(LocalDate dbeg, LocalDate dend, long periodType, long pattern) {
        String r_text = getTextCustom(dbeg, dend);

        if (pattern == arabicFull) {
            // цифры арабские, текст полный
            r_text = getTextArabicFull(dbeg, dend, periodType);
        } else if (pattern == arabicShort) {
            // цифры арабские, текст короткий
            r_text = getTextArabicShort(dbeg, dend, periodType);
        }
        return r_text;
    }

    //генерирует полное название периода с арабскими числами
    protected String getTextArabicFull(LocalDate dbeg, LocalDate dend, long periodType) {
        String r_text = getTextCustom(dbeg, dend);

        if (periodType == FD_PeriodType_consts.halfyear) {
            // Полугодие
            if (isHalfYear1(dbeg, dend)) {
                r_text = t("1-полугодие {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isHalfYear2(dbeg, dend)) {
                r_text = t("2-полугодие {0} г.", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.quarter) {
            // Квартал
            if (isQuarter1(dbeg, dend)) {
                r_text = t("1-квартал {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter2(dbeg, dend)) {
                r_text = t("2-квартал {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter3(dbeg, dend)) {
                r_text = t("3-квартал {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter4(dbeg, dend)) {
                r_text = t("4-квартал {0} г.", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.year) {
            // Год
            if (isYear(dbeg, dend)) {
                r_text = t("{0} год", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.month) {
            // Месяц
            if (isMonth(dbeg, dend)) {
                r_text = t("{0} {1} год", getMonthName(dbeg.getMonthValue() - 1, false, false), String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.day) {
            // День
            String day = String.valueOf(dbeg.getDayOfMonth());
            String month = fullMonthName(dbeg.getMonth());
            r_text = day + " " + month + " " + dbeg.getYear();
        }

        return r_text;
    }

    //генерирует короткое название периода (сокращённые слова и формы)
    protected String getTextArabicShort(LocalDate dbeg, LocalDate dend, long periodType) {
        String r_text = getTextCustom(dbeg, dend);

        if (periodType == FD_PeriodType_consts.halfyear) {
            // Полугодие
            if (isHalfYear1(dbeg, dend)) {
                r_text = t("1-пол. {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isHalfYear2(dbeg, dend)) {
                r_text = t("2-пол. {0} г.", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.quarter) {
            // Квартал
            if (isQuarter1(dbeg, dend)) {
                r_text = t("1-кв. {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter2(dbeg, dend)) {
                r_text = t("2-кв. {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter3(dbeg, dend)) {
                r_text = t("3-кв. {0} г.", String.valueOf(dbeg.getYear()));
            } else if (isQuarter4(dbeg, dend)) {
                r_text = t("4-кв. {0} г.", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.year) {
            // Год
            if (isYear(dbeg, dend)) {
                r_text = t("{0} г.", String.valueOf(dbeg.getYear()));
            }
        } else if (periodType == FD_PeriodType_consts.month) {
            // Месяц
            if (isMonth(dbeg, dend)) {
                r_text = t("{0} {1} г.", getMonthName(dbeg.getMonthValue() - 1, true, false), String.valueOf(dbeg.getYear()));
            }
        }

        return r_text;
    }

    /**
     * Возвращает имя месяца на русском языке
     *
     * @param MonthNumber - номер месяца (начинается с нуля, например Январь - 0, Декабрь - 11)
     * @param isShort:    true - краткое имя, false - полное имя
     * @param decline     - true - склонять название месяца в падеж, false - не склонять
     * @return name of month
     */
    protected String getMonthName(int MonthNumber, boolean isShort, boolean decline) {
        String r_text = "";

        if (isShort) {
            switch (MonthNumber) {
                case 0 -> r_text = "Янв";
                case 1 -> r_text = "Фев";
                case 2 -> r_text = "Мар";
                case 3 -> r_text = "Апр";
                case 4 -> r_text = "Май";
                case 5 -> r_text = "Июн";
                case 6 -> r_text = "Июл";
                case 7 -> r_text = "Авг";
                case 8 -> r_text = "Сен";
                case 9 -> r_text = "Окт";
                case 10 -> r_text = "Ноя";
                case 11 -> r_text = "Дек";
                default -> r_text = "";
            }
        } else {
            switch (MonthNumber) {
                case 0 -> r_text = decline ? "Января" : "Январь";
                case 1 -> r_text = decline ? "Февраля" : "Февраль";
                case 2 -> r_text = decline ? "Марта" : "Март";
                case 3 -> r_text = decline ? "Апреля" : "Апрель";
                case 4 -> r_text = decline ? "Мая" : "Май";
                case 5 -> r_text = decline ? "Июня" : "Июнь";
                case 6 -> r_text = decline ? "Июля" : "Июль";
                case 7 -> r_text = decline ? "Августа" : "Август";
                case 8 -> r_text = decline ? "Сентября" : "Сентябрь";
                case 9 -> r_text = decline ? "Октября" : "Октябрь";
                case 10 -> r_text = decline ? "Ноября" : "Ноябрь";
                case 11 -> r_text = decline ? "Декабря" : "Декабрь";
                default -> r_text = "";
            }
        }
        return r_text;
    }

    //проверка ГОДА
    protected Boolean isYear(LocalDate dbeg, LocalDate dend) {
        return dbeg.equals(dbeg.withDayOfYear(1)) && dend.equals(dend.withMonth(12).with(TemporalAdjusters.lastDayOfMonth()));
    }

    //проверка ПЕРВАЯ ПОЛОВИНА ПОЛУГОДИЯ
    protected Boolean isHalfYear1(LocalDate dbeg, LocalDate dend) {
        LocalDate firstDay = dbeg.withDayOfYear(1);
        LocalDate endHalf = firstDay.plusMonths(5).with(TemporalAdjusters.lastDayOfMonth());
        return dbeg.equals(firstDay) && dend.equals(endHalf);
    }

    //проверка ВТОРАЯ ПОЛОВИНА ПОЛУГОДИЯ
    protected Boolean isHalfYear2(LocalDate dbeg, LocalDate dend) {
        LocalDate secondBegin = dbeg.withDayOfYear(1).plusMonths(6);
        LocalDate endYear = dend.withMonth(12).with(TemporalAdjusters.lastDayOfMonth());
        return dbeg.equals(secondBegin) && dend.equals(endYear);
    }

    //проверка ПЕРВЫЙ КВАРТАЛ
    protected Boolean isQuarter1(LocalDate dbeg, LocalDate dend) {
        LocalDate qBegin = dbeg.withDayOfYear(1);
        LocalDate qEnd = qBegin.plusMonths(3).minusDays(1);
        return dbeg.equals(qBegin) && dend.equals(qEnd);
    }

    //проверка ВТОРОЙ КВАРТАЛ
    protected Boolean isQuarter2(LocalDate dbeg, LocalDate dend) {
        LocalDate qBegin = dbeg.withDayOfYear(1).plusMonths(3);
        LocalDate qEnd = dbeg.withDayOfYear(1).plusMonths(6).minusDays(1);
        return dbeg.equals(qBegin) && dend.equals(qEnd);
    }

    //проверка ТРЕТИЙ КВАРТАЛ
    protected Boolean isQuarter3(LocalDate dbeg, LocalDate dend) {
        LocalDate qBegin = dbeg.withDayOfYear(1).plusMonths(6);
        LocalDate qEnd = dbeg.withDayOfYear(1).plusMonths(9).minusDays(1);
        return dbeg.equals(qBegin) && dend.equals(qEnd);
    }

    //проверка ЧЕТВЕРТЫЙ КВАРТАЛ
    protected Boolean isQuarter4(LocalDate dbeg, LocalDate dend) {
        LocalDate qBegin = dbeg.withDayOfYear(1).plusMonths(9);
        LocalDate qEnd = dend.withMonth(12).with(TemporalAdjusters.lastDayOfMonth());
        return dbeg.equals(qBegin) && dend.equals(qEnd);
    }

    //проверка МЕСЯЦ
    protected Boolean isMonth(LocalDate dbeg, LocalDate dend) {
        return dbeg.getDayOfMonth() == 1 && dend.equals(dbeg.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /*
     * название периода при ошибочном вводе данных
     */
    protected String getTextCustom(LocalDate dbeg, LocalDate dend) {
        return dbeg.format(DF) + " - " + dend.format(DF);
    }

    private String fullMonthName(Month m) {
        // Номинативные формы месяцоа (для вывода даты: "1 Январь 2020")
        switch (m) {
            case JANUARY:
                return "Январь";
            case FEBRUARY:
                return "Февраль";
            case MARCH:
                return "Март";
            case APRIL:
                return "Апрель";
            case MAY:
                return "Май";
            case JUNE:
                return "Июнь";
            case JULY:
                return "Июль";
            case AUGUST:
                return "Август";
            case SEPTEMBER:
                return "Сентябрь";
            case OCTOBER:
                return "Октябрь";
            case NOVEMBER:
                return "Ноябрь";
            case DECEMBER:
                return "Декабрь";
            default:
                return "";
        }
    }

}
