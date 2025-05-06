package com.morsaprogramando.secret_manager.view;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils{
    static public BufferedReader keyboard =
            new BufferedReader(new InputStreamReader(System.in));

    static public String readLine(String message) throws IOException {
        print(message); return keyboard.readLine().trim();
    }
    static public String readPassword(String message) throws IOException {
        print(message);

        StringBuilder password = new StringBuilder();
        Console console = System.console();

        if (console != null) {
            char[] pwdArray = console.readPassword();
            for (int i = 0; i < pwdArray.length; i++) {
                password.append(pwdArray[i]);
            }
        } else {
            password.append(keyboard.readLine());
        }

        return password.toString().trim();
    }
    static public int readInt(String message)throws IOException{
        return Integer.parseInt(readLine(message));
    }
    static public int readInt()throws IOException{
        return readInt("");
    }
    static public double readDouble(String x)throws IOException{
        return Double.parseDouble(readLine(x));
    }
    static public double readDouble()throws IOException{
        return readDouble("");
    }
    static public void print(String x){
        System.out.print(x);
    }
    static public void print(int x){
        System.out.print(x);
    }
    static public void print(double x){
        System.out.print(x);
    }
    static public void println(String x){
        System.out.println(x);
    }
    static public void println(int x){
        System.out.println(x);
    }
    static public void println(double x){
        System.out.println(x);
    }
    static public void clearScreen(){
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows-specific clear command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI escape codes for Unix-like systems
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    static public int random (int x, int y){
        return x + (int)(Math.random() * (y-x+1));
    }
}
