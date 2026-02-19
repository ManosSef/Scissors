package me.manossef.scissors.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OptionAdapter extends TypeAdapter<Option<?>> {

    public Option<?> read(JsonReader reader) throws IOException {

        if(reader.peek() == JsonToken.NULL) {

            reader.nextNull();
            return null;

        }
        String name = reader.nextString();
        return Options.fromName(name);

    }

    public void write(JsonWriter writer, Option<?> value) throws IOException {

        if(value == null) {

            writer.nullValue();
            return;

        }
        String name = value.getName();
        writer.value(name);

    }

}