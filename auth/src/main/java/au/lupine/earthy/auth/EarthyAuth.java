package au.lupine.earthy.auth;

import io.javalin.Javalin;

public class EarthyAuth {

    public static void main(String[] args) {
        Javalin javalin = Javalin.create();

        javalin.get("auth", ctx -> {
            ctx.json(true);
        });
    }
}
