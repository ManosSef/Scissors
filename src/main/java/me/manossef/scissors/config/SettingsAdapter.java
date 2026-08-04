package me.manossef.scissors.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.manossef.scissors.Scissors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsAdapter extends TypeAdapter<Settings> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsAdapter.class);

    @Override
    public void write(JsonWriter out, Settings settings) throws IOException {
        Collection<OptionValue<?>> values = settings.getValues();
        out.beginObject();
        for(OptionValue<?> optionValue : values) {
            out.name(optionValue.option().getName());
            Scissors.GSON.toJson(optionValue.value(), optionValue.option().getType(), out);
        }
        out.endObject();
    }

    @Override
    public Settings read(JsonReader in) {
        try {
            Set<OptionValue<?>> values = new HashSet<>();
            Map<String, ?> map = Scissors.GSON.fromJson(in, Map.class);
            LOGGER.debug("GSON read values: {}", map);
            if(map.isEmpty()) return new Settings();
            if(map.keySet().stream().findFirst().orElseThrow().equals("values")) {
                LegacyFormat legacyFormat = Scissors.GSON.fromJson(Scissors.GSON.toJson(map), LegacyFormat.class);
                for(LegacyFormat.Entry entry : legacyFormat.values) {
                    Option<?> option = Options.getByName(entry.option);
                    OptionValue<?> optionValue = option.castValue(entry.value);
                    values.add(optionValue);
                }
                return new Settings(values);
            }
            for(Map.Entry<String, ?> entry : map.entrySet()) {
                Option<?> option = Options.getByName(entry.getKey());
                OptionValue<?> optionValue = option.castValue(entry.getValue());
                values.add(optionValue);
            }
            return new Settings(values);
        } catch(RuntimeException e) {
            throw new InvalidConfigurationException(e);
        }
    }

    private record LegacyFormat(Entry[] values) {
        private record Entry(String option, Object value) {
        }
    }
}