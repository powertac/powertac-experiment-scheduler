package org.powertac.rachma.weather;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class WeatherConfigurationSerializer extends StdSerializer<WeatherConfiguration> {

    public WeatherConfigurationSerializer() {
        super(WeatherConfiguration.class);
    }

    @Override
    public void serialize(WeatherConfiguration weather, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("location", weather.getLocation());
        gen.writeNumberField("startTime", weather.getStartTime().toEpochMilli());
        gen.writeEndObject();
    }

}
