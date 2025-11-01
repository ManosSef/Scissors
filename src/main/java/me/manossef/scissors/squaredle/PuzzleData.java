package me.manossef.scissors.squaredle;

public record PuzzleData(String[] board, String wordScores, String optionalWordScores, Object difficulty,
                         WordOfTheDay wordOfTheDay, Credits credits) {

    public String[] words() {

        if(this.wordScores.isEmpty()) return new String[0];
        return PuzzleUtil.decryptString(this.wordScores).split(",");

    }

    public String[] optionalWords() {

        if(this.optionalWordScores.isEmpty()) return new String[0];
        return PuzzleUtil.decryptString(this.optionalWordScores).split(",");

    }

    public Object difficulty() {

        if(difficulty == null) return null;
        if(!(difficulty instanceof Double)) return null;
        return difficulty;

    }

    public String censoredWOTD() {

        String word = wordOfTheDay.term;
        int length = word.length();
        String n = "*".repeat(length);
        int p = 2;
        if(12 <= length) p = 3;
        n = word.substring(0, p) + n.substring(p);
        p = 0;
        if(10 <= length) p = 3;
        else if(8 <= length) p = 2;
        else if(7 == length) p = 1;
        n = n.substring(0, length - p) + word.substring(length - p);
        return n;

    }

    public record WordOfTheDay(String term, String definition, String definitionMd, String pronunciation) {
    }

    public record Credits(String author, String sponsor) {
    }

}