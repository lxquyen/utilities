package com.steve.utilities.presentation.letcode;

public class MinPathSum {
    private void show(int[][] grid) {
        int columns = grid[0].length;
        for (int[] ints : grid) {
            for (int j = 0; j < columns; j++) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
    }

    private int minPathSum(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        System.out.println("Step 1");
        for (int i = 1; i < n; i++) {
            grid[0][i] += grid[0][i - 1];
            System.out.println("[0][" + i + "] = " + grid[0][i]);
        }

        for (int i = 1; i < m; i++) {
            grid[i][0] += grid[i - 1][0];
            System.out.println("[" + i + "][0] = " + grid[i][0]);
        }
        show(grid);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
            }
        }
        show(grid);
        return grid[m - 1][n - 1];
    }

    public static void main(String[] args) {
        MinPathSum solution = new MinPathSum();
        int[][] grid = new int[][]{{1, 3, 1}, {1, 1, 1}, {4, 1, 1}};
        solution.show(grid);
        int result = solution.minPathSum(grid);
        System.out.println("Sum = " + result);
    }
}

