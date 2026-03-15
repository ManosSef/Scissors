package me.manossef.scissors.puzzles;

import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
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
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class Wordle extends Puzzle {

    private static final List<String> WORDS = new ArrayList<>();
    private static final List<String> ANSWERS = new ArrayList<>();
    private static final int MAX_GUESSES = 6;

    static {

        Util.loadWords("wordle_words.txt", WORDS, "Failed to initialize the list of words for Wordle games. All " + monospace(SharedConstants.COMMAND_PREFIX + "wordle") + " commands will fail during this session.");
        Util.loadWords("wordle_answers.txt", ANSWERS, "Failed to initialize the list of answers for Wordle games. All " + monospace(SharedConstants.COMMAND_PREFIX + "wordle") + " commands will fail during this session.");

    }

    private Message message;
    private String answer;
    private List<String> guesses;
    private boolean hardMode;
    private boolean[] knownGreens;
    private List<Character> knownYellows;

    public Wordle(MessageChannel channel, boolean hardMode) {

        this(channel, hardMode, ANSWERS.get(Scissors.RANDOM.nextInt(ANSWERS.size())));

    }

    public Wordle(MessageChannel channel, boolean hardMode, String answer) {

        super(channel);
        if(!canStart()) return;
        this.hardMode = hardMode;
        this.answer = answer;
        Scissors.DISCORD_API.addEventListener(this);
        this.start();

    }

    public void start() {

        this.guesses = new ArrayList<>();
        if(this.hardMode) {

            this.knownGreens = new boolean[5];
            this.knownYellows = new ArrayList<>();

        }
        this.getChannel().sendMessage(MessageCreateData.fromEmbeds(new MessageEmbed(null, "Wordle" + (this.hardMode ? " (hard mode)" : ""),
            "⬛⬛⬛⬛⬛\n".repeat(MAX_GUESSES) + "Reply to this message with a 5-letter word to guess it!", EmbedType.RICH, null, 0x5865F2, null, null, null,
            null, null, null, null))).queue();

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
            if(!title.contains("Wordle")) return;
            this.message = message;
            return;

        }
        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return;
        if(referencedMessage.getIdLong() != this.message.getIdLong()) return;
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        if(!WORDS.contains(content.toLowerCase())) return;
        if(!checkHardMode(message)) return;
        if(message.getType().canDelete() && message.getChannel() instanceof GuildChannel) {

            try {

                message.delete().queue();

            } catch(InsufficientPermissionException ignored) {
            }

        }
        this.guessWord(content);
        this.updateMessage();
        if(this.isSolved() || this.isLost()) this.end();

    }

    private void guessWord(String word) {

        String guess = word.toLowerCase();
        if(this.guesses.contains(guess)) return;
        this.guesses.add(guess);
        if(this.hardMode) {

            WordleColor[] colors = this.getColors(guess);
            this.knownYellows.clear();
            for(int i = 0; i < 5; i++) {

                if(colors[i] == WordleColor.GREEN) this.knownGreens[i] = true;
                if(colors[i] != WordleColor.NONE) this.knownYellows.add(this.answer.charAt(i));

            }

        }

    }

    private boolean checkHardMode(Message message) {

        if(this.hardMode) return true;
        String guess = message.getContentRaw().toLowerCase();
        for(int i = 0; i < 5; i++) {

            if(this.knownGreens[i] && guess.charAt(i) != this.answer.charAt(i)) {

                message.reply("Your guess must have the letter " + this.answer.charAt(i) + " at position " + i).queue();
                return false;

            }

        }
        List<Character> yellows = new ArrayList<>(this.knownYellows);
        for(Character letter : this.knownYellows) {

            int count = 0;
            while(yellows.remove(letter)) count++;
            int found = 0;
            for(char ch : guess.toCharArray())
                if(letter == ch) found++;
            if(found < count) {

                message.reply("Your guess must contain at least " + count + " of the letter " + letter).queue();
                return false;

            }

        }
        return true;

    }

    private void updateMessage() {

        StringBuilder builder = new StringBuilder();
        if(!this.guesses.isEmpty()) {

            for(String guess : this.guesses)
                builder.append(this.formatGuess(guess)).append("\n");

        }
        builder.append("⬛⬛⬛⬛⬛\n".repeat(MAX_GUESSES - this.guesses.size()));
        if(this.isSolved()) builder.append(bold(this.getFinalComment()));
        else if(this.isLost()) builder.append(bold("Failed! The answer was " + this.answer.toUpperCase()));
        else builder.append("Reply to this message with a 5-letter word to guess it!");
        this.message.editMessage(MessageEditData.fromEmbeds(new MessageEmbed(null, "Wordle" + (this.hardMode ? " (hard mode)" : ""), builder.toString(), EmbedType.RICH, null, 0x5865F2, null, null,
            null, null, null, null, null))).queue();

    }

    private String formatGuess(String guess) {

        WordleColor[] colors = this.getColors(guess);
        StringBuilder builder = new StringBuilder();
        for(WordleColor color : colors) {

            builder.append(switch(color) {

                case GREEN -> "\uD83D\uDFE9";
                case YELLOW -> "\uD83D\uDFE8";
                case NONE -> "⬛";

            });

        }
        builder.append(" ").append(guess.toUpperCase());
        return builder.toString();

    }

    private WordleColor[] getColors(String guess) {

        WordleColor[] colors = new WordleColor[5];
        for(int i = 0; i < 5; i++)
            if(guess.charAt(i) == this.answer.charAt(i))
                colors[i] = WordleColor.GREEN;
        for(int i = 0; i < 5; i++) {

            if(colors[i] != null)
                if(colors[i].equals(WordleColor.GREEN))
                    continue;
            char letter = this.answer.charAt(i);
            for(int j = 0; j < 5; j++)
                if(guess.charAt(j) == letter && colors[j] == null) {

                    colors[j] = WordleColor.YELLOW;
                    break;

                }

        }
        for(int i = 0; i < 5; i++)
            if(colors[i] == null)
                colors[i] = WordleColor.NONE;
        return colors;

    }

    private String getFinalComment() {

        return switch(this.guesses.size()) {

            case 1 -> "Genius";
            case 2 -> "Magnificent";
            case 3 -> "Impressive";
            case 4 -> "Splendid";
            case 5 -> "Great";
            case 6 -> "Phew";
            default -> throw new IllegalStateException("what");

        };

    }

    private boolean isSolved() {

        if(guesses.isEmpty()) return false;
        return guesses.get(guesses.size() - 1).equals(answer);

    }

    private boolean isLost() {

        return guesses.size() == MAX_GUESSES && !this.isSolved();

    }

    public static boolean canStart() {

        return !WORDS.isEmpty() && !ANSWERS.isEmpty();

    }

    private enum WordleColor {

        GREEN,
        YELLOW,
        NONE

    }

}
