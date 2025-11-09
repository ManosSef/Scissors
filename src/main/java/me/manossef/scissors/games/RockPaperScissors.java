package me.manossef.scissors.games;

import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class RockPaperScissors extends Game {

    private Message message;
    private Move player1Move;
    private Move player2Move;

    public RockPaperScissors(User player1, User player2, MessageChannel channel) {

        super(player1, player2, channel);
        Scissors.DISCORD_API.addEventListener(this);
        this.start();

    }

    public void start() {

        this.getChannel().sendMessage(MessageCreateData.fromEmbeds(
            new MessageEmbed(null, "Rock paper scissors game between " + this.getPlayer1().getName() + " and " + this.getPlayer2().getName(), "Choose your move:", EmbedType.RICH, null, 0xDE6868,
                null, null, null, null, null, null, null)
        )).addComponents(ActionRow.of(Button.success("20", "\uD83E\uDEA8 Rock"), Button.success("21", "\uD83E\uDDFB Paper"), Button.success("22", "✂️ Scissors"))).queue();

    }

    public void end() {

        Scissors.DISCORD_API.removeEventListener(this);

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(this.message != null) return;
        Message message = event.getMessage();
        if(message.getAuthor().getIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(message.getEmbeds().isEmpty()) return;
        String title = message.getEmbeds().get(0).getTitle();
        if(title == null) return;
        if(!title.contains("Rock paper scissors")) return;
        this.message = message;

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if(this.message == null) return;
        Message message = event.getMessage();
        if(this.message.getIdLong() != message.getIdLong()) return;
        this.makeMove(event.getInteraction().getMember(), switch(event.getCustomId()) {

            case "20" -> Move.ROCK;
            case "21" -> Move.PAPER;
            case "22" -> Move.SCISSORS;
            default -> null;

        });
        event.getInteraction().deferEdit().queue();

    }

    private void makeMove(Member member, Move move) {

        if(move == null) return;
        if(member.getIdLong() == this.getPlayer1().getIdLong()) {

            if(this.player1Move != null) return;
            this.player1Move = move;

        } else if(member.getIdLong() == this.getPlayer2().getIdLong()) {

            if(this.player2Move != null) return;
            this.player2Move = move;

        } else return;
        this.update();

    }

    private void update() {

        StringBuilder description = new StringBuilder();
        Status status = this.getStatus();
        if(status == Status.ONGOING) {

            if(player1Move != null)
                description.append("**").append(this.getPlayer1().getAsMention()).append(" has chosen their move!**\n");
            if(player2Move != null)
                description.append("**").append(this.getPlayer2().getAsMention()).append(" has chosen their move!**\n");
            description.append("Choose your move:");

        } else {

            description.append("**").append(this.getPlayer1().getAsMention()).append(" has chosen ").append(this.player1Move.getName()).append(" and ").append(this.getPlayer2().getAsMention()).append(" has chosen ").append(this.player2Move.getName()).append("!\n");
            switch(status) {

                case PLAYER_1_WON -> description.append(this.getPlayer1().getAsMention()).append(" won!**");
                case PLAYER_2_WON -> description.append(this.getPlayer2().getAsMention()).append(" won!**");
                case DRAW -> description.append("It's a tie!**");

            }
            this.end();

        }
        this.message.editMessage(MessageEditData.fromEmbeds(
            new MessageEmbed(null, "Rock paper scissors game between " + this.getPlayer1().getName() + " and " + this.getPlayer2().getName(), description.toString(), EmbedType.RICH, null, 0xDE6868,
                null, null, null, null, null, null, null)
        )).setComponents(ActionRow.of(Button.success("20", "\uD83E\uDEA8 Rock"), Button.success("21", "\uD83E\uDDFB Paper"), Button.success("22", "✂️ Scissors"))).queue();

    }

    private Status getStatus() {

        if(player1Move == null || player2Move == null) return Status.ONGOING;
        if(player1Move == player2Move) return Status.DRAW;
        if((player1Move == Move.ROCK && player2Move == Move.SCISSORS)
            || (player1Move == Move.SCISSORS && player2Move == Move.PAPER)
            || (player1Move == Move.PAPER && player2Move == Move.ROCK)) return Status.PLAYER_1_WON;
        return Status.PLAYER_2_WON;

    }

    public enum Move {

        ROCK("rock"),
        PAPER("paper"),
        SCISSORS("scissors");

        private final String name;

        Move(String name) {

            this.name = name;

        }

        public String getName() {

            return this.name;

        }

    }

}
