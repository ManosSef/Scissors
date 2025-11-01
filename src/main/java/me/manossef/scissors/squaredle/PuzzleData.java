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

        char[] letters = wordOfTheDay.term.toCharArray();
        for(int d = 2; d < letters.length; d++) {

            if((1 == d % 4 || 2 == d % 4 || 3 == d) && d < letters.length - 2)
                letters[d] = '*';
            if(6 > letters.length)
                letters[3] = '*';

        }
        return new String(letters);

    }

    public record WordOfTheDay(String term, String definition, String definitionMd, String pronunciation) {
    }

    public record Credits(String author, String sponsor) {
    }

}