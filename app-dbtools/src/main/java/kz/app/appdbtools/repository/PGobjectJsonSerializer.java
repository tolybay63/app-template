package kz.app.appdbtools.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.postgresql.util.PGobject;

import java.io.IOException;

public class PGobjectJsonSerializer extends JsonSerializer<PGobject> {

    @Override
    public void serialize(PGobject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.getValue() == null) {
            gen.writeNull();
            return;
        }

        // Попробуем распарсить value как JSON (а не просто строку)
        try {
            gen.writeRawValue(value.getValue());
        } catch (Exception e) {
            // fallback, если не получилось — как строку
            gen.writeString(value.getValue());
        }
    }
}
