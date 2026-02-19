package me.manossef.scissors.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.manossef.scissors.Scissors;

import java.io.IOException;

public class OptionValueAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

        if(!typeToken.getRawType().equals(OptionValue.class))
            return null;
        return new TypeAdapter<>() {

            public T read(JsonReader reader) throws IOException {

                if(reader.peek() == JsonToken.NULL) {

                    reader.nextNull();
                    return null;

                }
                Option option = null;
                Boolean booleanValue = null;
                Integer intValue = null;
                while(reader.hasNext()) {

                    String name = reader.nextName();
                    if(name.equals("option"))
                        option = Scissors.GSON.getAdapter(Option.class).read(reader);
                    else if(name.equals("value")) {

                        if(reader.peek() == JsonToken.BOOLEAN)
                            booleanValue = reader.nextBoolean();
                        else if(reader.peek() == JsonToken.NUMBER)
                            intValue = reader.nextInt();

                    }

                }
                if(option == null || (booleanValue == null && intValue == null))
                    return null;
                return (T) new OptionValue<>(option, option.properties().getType().equals(Boolean.class) ? booleanValue : intValue);

            }

            public void write(JsonWriter writer, T value) throws IOException {

                if(value == null) {

                    writer.nullValue();
                    return;

                }
                if(value instanceof OptionValue<?> optionValue) {

                    writer.beginObject().name("option");
                    Scissors.GSON.getAdapter(Option.class).write(writer, optionValue.option());
                    writer.name("value").value(optionValue.value().toString()).endObject();
                    return;

                }
                writer.nullValue();

            }

        };

    }

}
