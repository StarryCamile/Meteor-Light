package qwq.wumie.systems.modules.crash;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class LoginCrash extends Module {
    public LoginCrash() {
        super(Categories.Crash, "login-crash", "Tries to crash the server on login using null packets. (By 0x150)");
    }
}
