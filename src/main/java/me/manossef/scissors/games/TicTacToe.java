package me.manossef.scissors.games;

import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class TicTacToe extends Game {

    private static final CustomEmoji TICTACTOE_EMOJI = Emoji.fromCustom("tictactoe", 1436395840276135936L, true);
    private Message message;
    private char[][] grid;

    public TicTacToe(User player1, User player2, MessageChannel channel) {

        super(player1, player2, channel);
        Scissors.DISCORD_API.addEventListener(this);
        this.start();

    }

    public void start() {

        this.getChannel().sendMessage(MessageCreateData.fromEmbeds(
            new MessageEmbed(null, "Tic-tac-toe game between " + this.getPlayer1().getAsMention() + " and " + this.getPlayer2().getAsMention(), this.getGridFormat(), EmbedType.RICH, null, 0xDE6868,
                null, null, null, null, null, null, null)
        )).addComponents(
            ActionRow.of(Button.success("100", TICTACTOE_EMOJI), Button.success("101", TICTACTOE_EMOJI), Button.success("102", TICTACTOE_EMOJI)),
            ActionRow.of(Button.success("110", TICTACTOE_EMOJI), Button.success("111", TICTACTOE_EMOJI), Button.success("112", TICTACTOE_EMOJI)),
            ActionRow.of(Button.success("120", TICTACTOE_EMOJI), Button.success("121", TICTACTOE_EMOJI), Button.success("122", TICTACTOE_EMOJI))
        ).queue();
        this.grid = new char[][]{{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};

    }

    public void end() {

        Scissors.DISCORD_API.removeEventListener(this);

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(this.message != null) return;
        Message message = event.getMessage();
        if(message.getAuthor().getIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(!message.getEmbeds().isEmpty()) return;
        String title = message.getEmbeds().get(0).getTitle();
        if(title == null) return;
        if(!title.contains("Tic-tac-toe")) return;
        this.message = message;

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if(this.message == null) return;
        Message message = event.getMessage();
        if(this.message.getIdLong() != message.getIdLong()) return;
        this.makeMove(event.getInteraction().getMember(), event.getCustomId().charAt(1), event.getCustomId().charAt(2));

    }

    private void makeMove(Member member, int row, int column) {

        if(grid[row][column] != ' ') return;
        char mark;
        if(member.getIdLong() == this.getPlayer1().getIdLong()) mark = 'X';
        else if(member.getIdLong() == this.getPlayer2().getIdLong()) mark = 'O';
        else return;
        grid[row][column] = mark;
        this.update();

    }

    private void update() {

        Status status = this.getStatus();
        String statusText = switch(status) {

            case DRAW -> "\n**It's a tie!**";
            case ONGOING -> "";
            case PLAYER_1_WON -> "\n**" + this.getPlayer1().getAsMention() + " won!**";
            case PLAYER_2_WON -> "\n**" + this.getPlayer2().getAsMention() + " won!**";

        };
        this.message.editMessage(MessageEditData.fromEmbeds(
            new MessageEmbed(null, "Tic-tac-toe game between " + this.getPlayer1().getAsMention() + " and " + this.getPlayer2().getAsMention(), this.getGridFormat() + statusText, EmbedType.RICH, null, 0xDE6868,
                null, null, null, null, null, null, null)
        )).setComponents(
            ActionRow.of(Button.success("100", TICTACTOE_EMOJI).withDisabled(grid[0][0] != ' '), Button.success("101", TICTACTOE_EMOJI).withDisabled(grid[0][1] != ' '), Button.success("102", TICTACTOE_EMOJI).withDisabled(grid[0][2] != ' ')),
            ActionRow.of(Button.success("110", TICTACTOE_EMOJI).withDisabled(grid[1][0] != ' '), Button.success("111", TICTACTOE_EMOJI).withDisabled(grid[1][1] != ' '), Button.success("112", TICTACTOE_EMOJI).withDisabled(grid[1][2] != ' ')),
            ActionRow.of(Button.success("120", TICTACTOE_EMOJI).withDisabled(grid[2][0] != ' '), Button.success("121", TICTACTOE_EMOJI).withDisabled(grid[2][1] != ' '), Button.success("122", TICTACTOE_EMOJI).withDisabled(grid[2][2] != ' '))
        ).queue();
        if(status != Status.ONGOING) this.end();

    }

    private Status getStatus() {

        if(grid[0][0] == grid[0][1] && grid[0][1] == grid[0][2]) {

            if(grid[0][0] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[1][0] == grid[1][1] && grid[1][1] == grid[1][2]) {

            if(grid[1][0] == 'X') return Status.PLAYER_1_WON;
            if(grid[1][0] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[2][0] == grid[2][1] && grid[2][1] == grid[2][2]) {

            if(grid[2][0] == 'X') return Status.PLAYER_1_WON;
            if(grid[2][0] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[0][0] == grid[1][0] && grid[1][0] == grid[2][0]) {

            if(grid[0][0] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[0][1] == grid[1][1] && grid[1][1] == grid[2][1]) {

            if(grid[0][1] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][1] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[0][2] == grid[1][2] && grid[1][2] == grid[2][2]) {

            if(grid[0][2] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][2] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {

            if(grid[0][0] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Status.PLAYER_2_WON;

        }
        if(grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {

            if(grid[0][2] == 'X') return Status.PLAYER_1_WON;
            if(grid[0][2] == 'O') return Status.PLAYER_2_WON;

        }
        for(char[] row : grid) for(char c : row) if(c == ' ') return Status.ONGOING;
        return Status.DRAW;

    }

    private String getGridFormat() {

        return String.format("""
            ```%s|%s|%s
            -+-+-
            %s|%s|%s
            -+-+-
            %s|%s|%s```""", grid[0][0], grid[0][1], grid[0][2], grid[1][0], grid[1][1], grid[1][2], grid[2][0], grid[2][1], grid[2][2]);

    }

}
