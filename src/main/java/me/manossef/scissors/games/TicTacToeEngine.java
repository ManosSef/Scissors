package me.manossef.scissors.games;

import me.manossef.scissors.Scissors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeEngine {

    public static Slot getMove(char[][] grid) {

        List<Slot> emptySlots = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(grid[i][j] == ' ') emptySlots.add(new Slot(i, j));
        for(Slot slot : emptySlots)
            if(isWinning(grid, slot)) return slot;
        for(Slot slot : emptySlots)
            if(isLosing(grid, slot)) return slot;
        return emptySlots.get(Scissors.RANDOM.nextInt(emptySlots.size()));

    }

    private static boolean isWinning(char[][] grid, Slot slot) {

        if(grid[slot.row][slot.column] != ' ') return false;
        char[][] testingGrid = deepCopyOf(grid);
        testingGrid[slot.row][slot.column] = 'O';
        return getStatus(testingGrid) == Game.Status.PLAYER_2_WON;

    }

    private static boolean isLosing(char[][] grid, Slot slot) {

        if(grid[slot.row][slot.column] != ' ') return false;
        char[][] testingGrid = deepCopyOf(grid);
        testingGrid[slot.row][slot.column] = 'X';
        return getStatus(testingGrid) == Game.Status.PLAYER_1_WON;

    }

    public static Game.Status getStatus(char[][] grid) {

        if(grid[0][0] == grid[0][1] && grid[0][1] == grid[0][2]) {

            if(grid[0][0] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[1][0] == grid[1][1] && grid[1][1] == grid[1][2]) {

            if(grid[1][0] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[1][0] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[2][0] == grid[2][1] && grid[2][1] == grid[2][2]) {

            if(grid[2][0] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[2][0] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[0][0] == grid[1][0] && grid[1][0] == grid[2][0]) {

            if(grid[0][0] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[0][1] == grid[1][1] && grid[1][1] == grid[2][1]) {

            if(grid[0][1] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][1] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[0][2] == grid[1][2] && grid[1][2] == grid[2][2]) {

            if(grid[0][2] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][2] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {

            if(grid[0][0] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][0] == 'O') return Game.Status.PLAYER_2_WON;

        }
        if(grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {

            if(grid[0][2] == 'X') return Game.Status.PLAYER_1_WON;
            if(grid[0][2] == 'O') return Game.Status.PLAYER_2_WON;

        }
        for(char[] row : grid) for(char c : row) if(c == ' ') return Game.Status.ONGOING;
        return Game.Status.DRAW;

    }

    private static char[][] deepCopyOf(char[][] original) {

        char[][] result = new char[original.length][];
        for(int i = 0; i < original.length; i++)
            result[i] = Arrays.copyOf(original[i], original[i].length);
        return result;

    }

    public record Slot(int row, int column) {
    }

}
