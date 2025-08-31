package kz.app.appcore.utils;

import java.time.*;
import java.time.temporal.*;

/**
 * Работа с датой в БД по правилам приложения.
 */
public class DbDate {

    public final static OffsetDateTime DEND_EMPTY = OffsetDateTime.parse("3333-12-31T00:00:00+00");

    static ZoneOffset offset = ZoneOffset.of("+00:00");

    public static OffsetDateTime getNow() {
        // Значение в timestamp подразумевается в +00:00
        ZoneOffset offset = ZoneOffset.of("+00:00");
        //
        OffsetDateTime res = OffsetDateTime.now(offset);
        //
        return res;
    }

    public static OffsetDateTime fromTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        // Значение в timestamp подразумевается в +00:00
        ZoneOffset offset = ZoneOffset.of("+00:00");
        //
        OffsetDateTime res = OffsetDateTime.ofInstant(instant, offset);
        //
        return res;
    }

    public static OffsetDateTime truncMills(OffsetDateTime value) {
        return value.truncatedTo(ChronoUnit.SECONDS);
    }

}
