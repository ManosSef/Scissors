package me.manossef.scissors.puzzles;

import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.dv8tion.jda.api.utils.MarkdownUtil.*;

public class Hangman extends Puzzle {

    private static final List<String> WORDS = new ArrayList<>();
    private static final int MAX_MISTAKES = 6;

    static {

        loadWords();

    }

    private Message message;
    private String word;
    private char[] revealedLetters;
    private int mistakesLeft;
    private List<String> guesses;

    public Hangman(MessageChannel channel) {

        super(channel);
        if(!canStart()) return;
        Scissors.DISCORD_API.addEventListener(this);
        this.start();

    }

    public void start() {

        word = WORDS.get(Scissors.RANDOM.nextInt(WORDS.size()));
        revealedLetters = new char[word.length()];
        for(int i = 0; i < word.length(); i++) revealedLetters[i] = '_';
        mistakesLeft = MAX_MISTAKES;
        guesses = new ArrayList<>();
        this.getChannel().sendMessage(MessageCreateData.fromEmbeds(new MessageEmbed(null, "Hangman", "# " + new String(revealedLetters).toUpperCase().replace("_", "\\_") + "\n" + this.getHangmanDrawing()
            + "\nPrevious guesses: " + guesses.toString().replaceAll("[\\[\\]]", "").toUpperCase(), EmbedType.RICH, null, 0x5865F2, null, null, null, null, null, null,
            null))).queue();

    }

    public void end() {

        Scissors.DISCORD_API.removeEventListener(this);

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        if(message.getAuthor().getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong()) {

            if(this.message != null) return;
            if(message.getEmbeds().isEmpty()) return;
            String title = message.getEmbeds().get(0).getTitle();
            if(title == null) return;
            if(!title.equals("Hangman")) return;
            this.message = message;
            return;

        }
        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return;
        if(referencedMessage.getIdLong() != this.message.getIdLong()) return;
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        if(!content.matches("[A-Za-z]+")) return;
        if(content.length() == 1) this.guessLetter(content);
        else this.guessWord(content);
        this.updateMessage();
        if(this.isSolved() || this.isLost()) this.end();

    }

    private void guessLetter(String letter) {

        if(guesses.contains(letter.toLowerCase())) return;
        char guess = Character.toLowerCase(letter.charAt(0));
        int finds = 0;
        for(int i = 0; i < this.word.length(); i++) {

            if(this.word.charAt(i) == guess) {

                revealedLetters[i] = guess;
                finds++;

            }

        }
        if(finds == 0) mistakesLeft--;
        guesses.add(String.valueOf(guess));

    }

    private void guessWord(String word) {

        String guess = word.toLowerCase();
        if(guesses.contains(guess)) return;
        if(this.word.equals(guess))
            this.revealedLetters = this.word.toCharArray();
        else mistakesLeft--;
        guesses.add(guess);

    }

    private void updateMessage() {

        this.message.editMessage(MessageEditData.fromEmbeds(new MessageEmbed(null, "Hangman", "# " + new String(revealedLetters).toUpperCase().replace("_", "\\_") + "\n" + this.getHangmanDrawing()
            + "\nPrevious guesses: " + guesses.toString().replaceAll("[\\[\\]]", "").toUpperCase() + (this.isSolved() ? "\n" + bold("Solved!") : this.isLost() ? "\n" + bold("Failed! The answer was " + this.word.toUpperCase()) : ""),
            EmbedType.RICH, null, 0x5865F2, null, null, null, null, null, null, null))).queue();

    }

    private boolean isSolved() {

        return this.word.equals(new String(revealedLetters));

    }

    private boolean isLost() {

        return mistakesLeft <= 0;

    }

    public static boolean canStart() {

        return !WORDS.isEmpty();

    }

    public static void loadWords() {

        try {

            WORDS.clear();
            Reader fileReader = SharedConstants.IS_STAGING ? new FileReader("staging_resources/hangman_words.txt") : new InputStreamReader(Objects.requireNonNull(Scissors.class.getResourceAsStream("hangman_words.txt")));
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while((line = reader.readLine()) != null) WORDS.add(line.toLowerCase());
            reader.close();

        } catch(IOException e) {

            e.printStackTrace();
            DevGuild.logStatus("Failed to initialize the list of words for hangman games. All " + monospace(SharedConstants.COMMAND_PREFIX + "hangman") + " commands will fail during this session.");

        }

    }

    private String getHangmanDrawing() {

        return switch(mistakesLeft) {

            case 0 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                    |      |
                \\   |   /  |
                 \\__|__/   |
                    |      |
                    |      |
                   / \\     |
                  /   \\    |
                 /     \\   |""");
            case 1 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                    |      |
                \\   |   /  |
                 \\__|__/   |
                    |      |
                    |      |
                   /       |
                  /        |
                 /         |""");
            case 2 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                    |      |
                \\   |   /  |
                 \\__|__/   |
                    |      |
                    |      |
                           |
                           |
                           |""");
            case 3 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                    |      |
                \\   |      |
                 \\__|      |
                    |      |
                    |      |
                           |
                           |
                           |""");
            case 4 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                    |      |
                    |      |
                    |      |
                    |      |
                    |      |
                           |
                           |
                           |""");
            case 5 -> codeblock("""
                ___________
                    |      |
                   ___     |
                  /   \\    |
                 |     |   |
                  \\___/    |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |""");
            case 6 -> codeblock("""
                ___________
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |
                           |""");
            default -> throw new IllegalArgumentException();

        };

    }

}
