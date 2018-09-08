package com.yogurtpowered.dash.utils;

import java.util.Scanner;

public class InputHelper {
    public static int getInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            try {
                System.out.print(message);
                String line = scanner.nextLine();

                int choice = Integer.parseInt(line);

                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    continue;
                }
            } catch (NumberFormatException nfe) {
                continue;
            }
        }
    }
}
