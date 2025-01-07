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

            String action = Utils.readLine("Choose [o:open, c:create, q:quit]? ");

            return switch (action.toLowerCase()) {
                case "o" -> Open.INSTANCE;
                case "c" -> Create.INSTANCE;
                case "q" -> Quit.INSTANCE;
                default -> {
                    Utils.println("Uknown action, exiting...");
                    yield Quit.INSTANCE;
                }
            };

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
