package com.morsaprogramando.secret_manager;

import com.morsaprogramando.secret_manager.controller.Controller;
import com.morsaprogramando.secret_manager.ui.MainUI;

public class Main {

    public static void main(String[] args) {
        if (Boolean.getBoolean("enableUI") || (args != null && args.length > 0 && args[0].equalsIgnoreCase("-UI"))) {
            MainUI.main(args);
        } else {
            Controller.INSTANCE.execute();
        }
    }
}
