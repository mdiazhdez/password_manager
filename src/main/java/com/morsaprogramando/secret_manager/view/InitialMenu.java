package com.morsaprogramando.secret_manager.view;

import java.io.IOException;

public enum InitialMenu {
    INSTANCE;

    public sealed interface Action permits Open, Create, Quit {}

    public enum Open implements Action { INSTANCE }
    public enum Create implements Action { INSTANCE }
    public enum Quit implements Action { INSTANCE }

    public Action getAction() {
        try {
            Utils.clearScreen();

            Utils.println("1. Open existing keystore");
            Utils.println("2. Create new keystore");
            Utils.println("3. Quit");

            int option = Utils.readInt("Select an option: ");

            return switch (option) {
                case 1 -> Open.INSTANCE;
                case 2 -> Create.INSTANCE;
                case 3 -> Quit.INSTANCE;
                default -> {
                    Utils.println("Uknown action, exiting...");
                    yield Quit.INSTANCE;
                }
            };

        } catch (IOException e) {
            throw new RuntimeException(e);
        }  catch (Exception e) {
            Utils.println("Not a valid option! Exiting...");
            System.exit(1);
        }

        return null;
    }
}
