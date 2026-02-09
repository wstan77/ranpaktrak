package com.lbisoft.core.app.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONConfiguration {
  static final Logger LOGGER = LoggerFactory.getLogger(JSONConfiguration.class);

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper =
        createObjectMapper(
            (Arrays.asList(
                new StdDeserializer[] {
                  new NumberDeserializers.ShortDeserializer(Short.class, null),
                  new NumberDeserializers.IntegerDeserializer(Integer.class, null),
                  new NumberDeserializers.CharacterDeserializer(Character.class, null),
                  new NumberDeserializers.LongDeserializer(Long.class, null),
                  new NumberDeserializers.FloatDeserializer(Float.class, null),
                  new NumberDeserializers.DoubleDeserializer(Double.class, null),
                  new NumberDeserializers.NumberDeserializer(),
                  new NumberDeserializers.BigDecimalDeserializer(),
                  new NumberDeserializers.BigIntegerDeserializer(),
                  new DateDeserializers.CalendarDeserializer()
                })));
    // Handle Java 8 features
    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new Jdk8Module());
    // Handle Dates
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    // Ignore JSON properties received that aren't in Java model
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    mapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, true);
    mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private ObjectMapper createObjectMapper(List<StdDeserializer> jsonDeserializers) {
    ObjectMapper objectMapper = new ObjectMapper();

    SimpleModule customJacksonModule =
        new SimpleModule("customJacksonModule", Version.unknownVersion());
    for (StdDeserializer jsonDeserializer : jsonDeserializers) {
      // Wrapping given deserializers with NonBlockingDeserializer
      customJacksonModule.addDeserializer(
          jsonDeserializer.handledType(), new NonBlockingDeserializer(jsonDeserializer));
    }

    objectMapper.registerModule(customJacksonModule);
    return objectMapper;
  }

  /**
   * Deserializer that won't block if value parsing doesn't match with target type.
   *
   * @param <T> Handled type
   */
  private static class NonBlockingDeserializer<T> extends JsonDeserializer<T> {
    private final StdDeserializer<T> delegate;

    public NonBlockingDeserializer(StdDeserializer<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      try {
        return delegate.deserialize(jp, ctxt);
      } catch (JsonParseException e) {
        LOGGER.error("Failed to parse JSON field", e);
        return null;
      }
    }
  }
}
