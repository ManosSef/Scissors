package me.manossef.scissors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util {

    public static String getMessageLink(Message message) {

        String guildId = message.getGuildId();
        return "https://discord.com/channels/" + (guildId == null ? "@me" : guildId) + "/" + message.getChannelId() + "/" + message.getId();

    }

    public static void writeValue(JsonWriter out, JsonElement value) throws IOException {

        if(value == null || value.isJsonNull())
            out.nullValue();
        else if(value.isJsonPrimitive()) {

            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if(primitive.isNumber())
                out.value(primitive.getAsNumber());
            else if(primitive.isBoolean())
                out.value(primitive.getAsBoolean());
            else
                out.value(primitive.getAsString());

        } else if(value.isJsonArray()) {

            out.beginArray();
            for(JsonElement element : value.getAsJsonArray())
                writeValue(out, element);
            out.endArray();

        } else {

            if(!value.isJsonObject())
                throw new IllegalArgumentException("Couldn't write " + value.getClass());
            out.beginObject();
            for(Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {

                out.name(entry.getKey());
                writeValue(out, entry.getValue());

            }
            out.endObject();

        }

    }

    public static <T> T getJsonFromFile(String fileName, Class<T> type) {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(SharedConstants.FILE_DIRECTORY + fileName));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            reader.close();
            return Scissors.GSON.fromJson(builder.toString(), type);

        } catch(IOException | JsonSyntaxException e) {

            System.err.println("Failed to read the " + fileName + " file.");
            return null;

        }

    }

    public static void saveJsonToFile(String fileName, Object object) {

        try {

            new File(SharedConstants.FILE_DIRECTORY).mkdirs();
            JsonWriter writer = new JsonWriter(new FileWriter(SharedConstants.FILE_DIRECTORY + fileName));
            writer.setIndent("  ");
            writeValue(writer, Scissors.GSON.toJsonTree(object));
            writer.close();

        } catch(IOException e) {

            System.err.println("Failed to save the " + fileName + " file.");

        }

    }

    public static void loadWords(String fileName, List<String> list, String logError) {

        try {

            list.clear();
            Reader fileReader = SharedConstants.IS_STAGING ? new FileReader("staging_resources/" + fileName) : new InputStreamReader(Objects.requireNonNull(Scissors.class.getResourceAsStream("/" + fileName)));
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while((line = reader.readLine()) != null) list.add(line.toLowerCase());
            reader.close();

        } catch(IOException e) {

            e.printStackTrace();
            DevGuild.logStatus(logError);

        }

    }

    public static boolean isLong(String input) {

        try {

            Long.parseLong(input);
            return true;

        } catch(NumberFormatException e) {

            return false;

        }

    }

}
