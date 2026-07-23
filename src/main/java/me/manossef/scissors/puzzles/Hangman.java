package me.manossef.scissors.puzzles;

import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.Util;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.codeblock;

public class Hangman extends Puzzle {
    private static final List<String> WORDS = new ArrayList<>();

    static {
        Util.loadWords("hangman_words.txt", WORDS, "Failed to initialize the list of words for hangman games. All " + Commands.format("hangman") + " commands will fail during this session.");
    }

    private Message message;
    private String word;
    private char[] revealedLetters;
    private int mistakesLeft;
    private List<String> guesses;
    private Difficulty difficulty;

    public Hangman(MessageChannel channel, Difficulty difficulty) {
        super(channel);
        if(!canStart()) return;
        this.difficulty = difficulty;
        Scissors.DISCORD_API.addEventListener(this);
        this.start();
    }

    public void start() {
        this.word = WORDS.get(Scissors.RANDOM.nextInt(WORDS.size()));
        this.revealedLetters = new char[this.word.length()];
        for(int i = 0; i < word.length(); i++) this.revealedLetters[i] = '_';
        this.mistakesLeft = this.difficulty.getMaxMistakes();
        this.guesses = new ArrayList<>();
        this.getChannel().sendMessage(MessageCreateData.fromEmbeds(new MessageEmbed(null, "Hangman", "# " + new String(this.revealedLetters).toUpperCase().replace("_", "\\_") + "\n" + this.getHangmanDrawing()
            + "\n\nReply to this message with a letter or word to guess it!\n" + this.getMistakesSentence(), EmbedType.RICH, null, 0x5865F2, null,
            null, null, null, null, null, null, 0))).queue();
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
        if(message.getType().canDelete() && message.getChannel() instanceof GuildChannel) {
            try {
                message.delete().queue();
            } catch(InsufficientPermissionException ignored) {
            }
        }
        if(content.length() == 1) this.guessLetter(content);
        else this.guessWord(content);
        this.updateMessage();
        if(this.isSolved() || this.isLost()) this.end();
    }

    private void guessLetter(String letter) {
        if(this.guesses.contains(letter.toLowerCase())) return;
        char guess = Character.toLowerCase(letter.charAt(0));
        int finds = 0;
        for(int i = 0; i < this.word.length(); i++) {
            if(this.word.charAt(i) == guess) {
                this.revealedLetters[i] = guess;
                finds++;
            }
        }
        if(finds == 0) this.mistakesLeft--;
        this.guesses.add(String.valueOf(guess));
    }

    private void guessWord(String word) {
        String guess = word.toLowerCase();
        if(this.guesses.contains(guess)) return;
        if(this.word.equals(guess))
            this.revealedLetters = this.word.toCharArray();
        else this.mistakesLeft--;
        this.guesses.add(guess);
    }

    private void updateMessage() {
        this.message.editMessage(MessageEditData.fromEmbeds(new MessageEmbed(null, "Hangman", "# " + new String(this.revealedLetters).toUpperCase().replace("_", "\\_") + "\n" + this.getHangmanDrawing()
            + (this.guesses.isEmpty() ? "" : "\nPrevious guesses: " + this.guesses.toString().replaceAll("[\\[\\]]", "").toUpperCase()) + (this.isSolved() ? "\n\n" + bold("Solved!") : this.isLost()
            ? "\n\n" + bold("Failed! The answer was " + this.word.toUpperCase()) : "\n\nReply to this message with a letter or word to guess it!\n" + this.getMistakesSentence()), EmbedType.RICH, null,
            0x5865F2, null, null, null, null, null, null, null, 0))).queue();
    }

    private String getMistakesSentence() {
        return "You lose if you make " + this.mistakesLeft + (this.mistakesLeft == this.difficulty.getMaxMistakes() ? "" : " more") + (this.mistakesLeft == 1 ? " mistake!" : " mistakes!");
    }

    private boolean isSolved() {
        return this.word.equals(new String(this.revealedLetters));
    }

    private boolean isLost() {
        return this.mistakesLeft <= 0;
    }

    public static boolean canStart() {
        return !WORDS.isEmpty();
    }

    private String getHangmanDrawing() {
        int drawing = this.mistakesLeft * (6 / this.difficulty.getMaxMistakes());
        return switch(drawing) {
            case 0 -> codeblock("""
                \n___________
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
                \n___________
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
                \n___________
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
                \n___________
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
                \n___________
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
                \n___________
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
                \n___________
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
            default -> throw new IllegalStateException();
        };
    }

    public enum Difficulty {
        NORMAL(6),
        HARD(3),
        IMPOSSIBLE(1);

        private final int mistakes;

        Difficulty(int mistakes) {
            this.mistakes = mistakes;
        }

        public int getMaxMistakes() {
            return this.mistakes;
        }
    }
}