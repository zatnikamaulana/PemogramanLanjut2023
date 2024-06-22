
package com.tutorial;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner Input = new Scanner(System.in);
        int[][] matrix = new int[3][3];

        System.out.println("masukan angka untuk matrix biasa 3x3:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print("Masukan Masuk baris ke " + i + " [" + i + "][" + j + "]:");
                matrix[i][j] = Input.nextInt();

            }
        }
        System.out.println("\n======================================");
        System.out.println("Matrix Biasa 3x3:");
        printMatrix(matrix);

        int[][] MatrixTraspose = traspose(matrix);
        System.out.println("\n=======================================");
        System.out.println("Matrix Traspose 3x3:");

        printMatrix(MatrixTraspose);

        int[][] PenjumlahanTransposes = TambahkanMatrix(matrix, MatrixTraspose);
        System.out.println("\n========================================");
        System.out.println("Hasil Matrix traspose + Matrix Biasa:");
        printMatrix(PenjumlahanTransposes);
    }

    public static int[][] traspose(int[][] matrix) {
        int[][] MatrixTraspose = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                MatrixTraspose[j][i] = matrix[i][j];
            }
        }
        return MatrixTraspose;
    }

    public static int[][] TambahkanMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] resultMatrix = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return resultMatrix;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(matrix[i][j] + " " );

            }
            System.out.println();
        }
    }
}


