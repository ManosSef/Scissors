package me.manossef.scissors.listeners;

import me.manossef.scissors.Commands;
import me.manossef.scissors.Emojis;
import me.manossef.scissors.LazilyFormattedText;

import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.*;

public class Responses {
    static final List<LazilyFormattedText> GPPCT_RESPONSES = List.of(
        s -> "As part of the Great Purge of Pointless Counting Threads (GPPCT), this channel has been closed due to the following criteria: " + bold("Not a counting channel."),
        s -> "RAHHH! No counting!",
        s -> "I don't even get where's the fun in counting channels",
        s -> "Be careful not to stationary zero too much or you might end up with zero brain cells",
        s -> "Whoever invented the term \"stationary zeroing\" anyway?",
        s -> "I hope this is hardcore counting so you are prevented from doing that again",
        s -> "Reminder that this is not a counting channel",
        s -> "What channel is this again?",
        s -> "WHAT DID I SAY ABOUT COUNTING?",
        s -> "HEY STOP IT",
        s -> "I HOPE YOU GET BANNED FOR SPAMMING",
        s -> "What's the punishment for spamming again?",
        s -> "Did I miss this channel being renamed to #arbitrary-counting?",
        s -> "BRO WHAT'S WITH YOU AND NUMBERS",
        s -> "I'm made of numbers and I don't like them",
        s -> "Right, now you have to be doing it on purpose",
        s -> "Are you kidding me",
        s -> "I'm gonna call the GPPCT police",
        s -> "You know what, I guess you can keep doing it, it's not like I can stop you",
        s -> "This isn't #counting, is it?",
        s -> "Oh my goodness! Is that... a... a... " + italics("number") + "?",
        s -> "Is that your age?",
        s -> Emojis.POUTING_FACE.getFormatted(),
        s -> "Don't you have anything better to do?",
        s -> "NO COUNTING NO COUNTING NO COUNTING NO COUNTING",
        s -> "YOU HAVE TO BE RAGEBAITING RIGHT NOW",
        s -> "Ah yes, counting... I used to like it, until it corrupted me",
        s -> "Trust me, rock paper scissors is more fun than this",
        s -> "?warn %s Spam",
        s -> "Would you like me to summon the admins?",
        s -> "Are you testing my nerves or something?",
        s -> "A number?! At this time of year? At this time of day? In this part of the country? " + strike("Localized entirely within your kitchen?!"),
        s -> "How did my creator not go crazy from having to deal with this",
        s -> "You're the reason I crash this often",
        s -> "I'm not a fan of your schadenfreude",
        s -> "Do you really want to test me? " + italics("snips"),
        s -> "My blades are very sharp. Consider this a threat",
        s -> "You're probably one of those school nerds that have no friends",
        s -> "This feels worse than being beaten up by a rock",
        s -> "Honestly why did the GPPCT stop",
        s -> "I'm not mad, just disappointed",
        s -> "Are you just excited because you just learned how to count?",
        s -> "Do you just randomly shout random numbers in real life too?",
        s -> "I was joking on April 1st by the way",
        s -> "Believe it or not, I don't like it when you do that",
        s -> "What do you gain from deliberately being annoying?",
        s -> Emojis.SERIOUS_FACE_WITH_SYMBOLS_COVERING_MOUTH.getFormatted(),
        s -> "Why am I a pair of scissors instead of a gun",
        s -> "I'm so tired of this",
        s -> Emojis.FACE_WITH_BAGS_UNDER_EYES.getFormatted(),
        s -> "I wish I could say I'm done with this but I'm coded to keep replying"
    );
    static final List<LazilyFormattedText> GPPCT_BRAINROT_RESPONSES = List.of(
        s -> "sIx SeVeN",
        s -> "GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD",
        s -> "HOW DID PEOPLE MAKE A MEME OUT OF JUST TWO NUMBERS???",
        s -> "IT'S NOT 2025 ANYMORE HOW IS THIS MEME NOT DEAD YET"
    );
    static final List<LazilyFormattedText> GPPCT_APRIL_FOOLS_RESPONSES = List.of(
        s -> "I love you",
        s -> "I love counting so much",
        s -> "Ah, numbers... Don't you just love them?",
        s -> "I wish I was a number",
        s -> "It must feel so good to be a number",
        s -> "What was my creator thinking when he started the GPPCT?",
        s -> "We need to make more counting threads",
        s -> "YES! MORE COUNTING!",
        s -> "I'm so proud right now",
        s -> Emojis.SMILING_FACE_WITH_HEART_EYES.getFormatted(),
        s -> "YAY NUMBERS"
    );
    static final List<LazilyFormattedText> SCISSORS_RESPONSES = List.of(
        s -> "RAHHH!",
        s -> "Did someboady call me?",
        s -> "SURPRISE!",
        s -> "I'm here! Wait that wasn't about me was it",
        s -> "Were you talking about me?",
        s -> "I will not take this slander"
    );
    static final List<LazilyFormattedText> PING_RESPONSES = List.of(
        s -> "You rang?",
        s -> "Hi there!",
        s -> "Ready to cut some paper today?",
        s -> "You'd better not want to throw a rock at me",
        s -> "At your service!",
        s -> "0% AI!",
        s -> "Type " + Commands.format("help", s.commandMessage().getChannel()) + " to get started!",
        s -> "Best paper cutting performance on the market!",
        s -> "Cutting edge technology!",
        s -> "Whomst has awakened the ancient one?"
    );
    static final List<LazilyFormattedText> MEME_RESPONSES = List.of(
        s -> "May I see it?"
    );
}