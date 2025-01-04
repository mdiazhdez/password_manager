package com.morsaprogramando.secret_manager.view;

import java.io.IOException;

public enum InitialMenu {
    INSTANCE;

    public enum Action {
        OPEN, CREATE, QUIT;
    }

    public Action getAction() {
        try {
            String action = Utils.readLine("Choose [o:open, c:create, q:quit]? ");

            switch (action.toLowerCase().trim()) {
                case "o": return Action.OPEN;
                case "c": return Action.CREATE;
                case "q": return Action.QUIT;
                default: Utils.println("Uknown action, exiting...");

                return Action.QUIT;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
