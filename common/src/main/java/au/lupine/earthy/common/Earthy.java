package au.lupine.earthy.common;

import au.lupine.emcapiclient.EMCAPIClient;
import au.lupine.emcapiclient.object.wrapper.Server;

public class Earthy {

    public static final String ID = "earthy";

    private static final EMCAPIClient API = new EMCAPIClient(Server.AURORA);

    public static EMCAPIClient getAPI() {
        return API;
    }
}
