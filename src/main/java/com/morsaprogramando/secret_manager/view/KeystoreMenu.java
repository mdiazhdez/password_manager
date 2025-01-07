package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class KeystoreMenu {

    private final PasswordManagerService passwordManagerService;
    private final List<StoredPassword> passwords;
    private final FileManagerService fileManagerService;
    private State currentState = State.CHOOSE;

    public void render() {

        while (true) {
            Utils.clearScreen();

            if (currentState == State.EXIT) return;

            displayPasswords();

            switch (currentState) {
                case CHOOSE -> printChooseMenu();
                case CREATE_PASS -> printCreatePassMenu();
                case READ_PASS -> {
                }
                case DEL_PASS -> {
                }
                case SAVE -> {
                }
            }
        }

    }

    private void displayActions() {

    }

    private void displayPasswords() {
        if (passwords.isEmpty()) {
            Utils.println("No passwords yet, create your first password.");
            printDelimiter();
        }

        for (StoredPassword password : passwords) {
            System.out.printf("%-20s %-20s %-10s%n",
                    password.title(),
                    password.username(),
                    "******");
        }
    }

    private void printCreatePassMenu() {

    }

    private void printChooseMenu() {
        Utils.println("1. Add a new password");
        Utils.println("2. View existing passwords");
        Utils.println("3. Delete a password");
        Utils.println("4. Save changes");
        Utils.println("5. Exit");

        try {
            int option = Utils.readInt("Select an option: ");
            this.currentState = State.fromNumber(option);
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            Utils.println("Not a valid option! Exiting...");
            System.exit(1);
        }
    }

    private static void printDelimiter() {
        String delimiter = """
        ═══════════════════════════════════════════════════════
        """;
        Utils.println(delimiter);
    }

    private enum State {
        CHOOSE(0), CREATE_PASS(1), READ_PASS(2), DEL_PASS(3), SAVE(4), EXIT(5);

        final int option;

        State(int option) {
            this.option = option;
        }

        static State fromNumber(int option) {
            return switch (option) {
                case 0 -> CHOOSE;
                case 1 -> CREATE_PASS;
                case 2 -> READ_PASS;
                case 3 -> DEL_PASS;
                case 4 -> SAVE;
                case 5 -> EXIT;
                default -> throw new IllegalArgumentException("Invalid option: " + option);
            };
        }
    }
}
