package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String readText;
        while (true) {
            try {
                readText = reader.readLine();
            }
            catch (IOException e) {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
                continue;
            }
            return readText;
        }
    }

    public static int readInt() {
        int readNumber;
        while (true) {
            try {
                readNumber = Integer.parseInt(readString());
            }
            catch (NumberFormatException e) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                continue;
            }
            return readNumber;
        }
    }
}
