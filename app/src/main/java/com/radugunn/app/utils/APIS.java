package com.radugunn.app.utils;

import com.ciyashop.library.apicall.URLS;

public class APIS {

    //TODO:Copy and Paste URL and Key Below from Admin Panel.

    /*public final String APP_URL = "https://shop.radugann.com/";
    public final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/";
    public final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/";

    public static final String CONSUMERKEY = "T085LVh6fdGQ";
    public static final String CONSUMERSECRET = "QoBk1PuN9td5RcdojhOTZTlY9jnamFqvWBJEHmD0jj2qkVvN";
    public static final String OAUTH_TOKEN = "chlxGCrBQRFrUREDzpfRUdl3";
    public static final String OAUTH_TOKEN_SECRET = "dBuXY798CWrIh8n8zPpry06YLHu7zV7PztS4kt6sCNOla2rs";

    public static final String WOOCONSUMERKEY = "ck_d36cceb6494450ce66f7596bb57403d50f48462f";
    public static final String WOOCONSUMERSECRET = "cs_db9085e55c1f0d51c8fb2817c0e38e47935c6577";
    public static final String version="4.0.0";
    public static final String purchasekey="29141c72-a3a3-44a1-bccb-05e69d98a246";*/

    public final String APP_URL = "https://devshop.radugann.com/";
    public final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/";
    public final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/";

    public static final String CONSUMERKEY = "T085LVh6fdGQ";
    public static final String CONSUMERSECRET = "QoBk1PuN9td5RcdojhOTZTlY9jnamFqvWBJEHmD0jj2qkVvN";
    public static final String OAUTH_TOKEN = "chlxGCrBQRFrUREDzpfRUdl3";
    public static final String OAUTH_TOKEN_SECRET = "dBuXY798CWrIh8n8zPpry06YLHu7zV7PztS4kt6sCNOla2rs";

    public static final String WOOCONSUMERKEY = "ck_d36cceb6494450ce66f7596bb57403d50f48462f";
    public static final String WOOCONSUMERSECRET = "cs_db9085e55c1f0d51c8fb2817c0e38e47935c6577";
    public static final String version = "4.3.0";
    public static final String purchasekey = "valid";


    public APIS() {
        URLS.APP_URL = APP_URL;
        URLS.NATIVE_API = APP_URL + "wp-json/wc/v3/";
        URLS.WOO_MAIN_URL = WOO_MAIN_URL;
        URLS.MAIN_URL = MAIN_URL;
        URLS.version = version;
        URLS.CONSUMERKEY = CONSUMERKEY;
        URLS.CONSUMERSECRET = CONSUMERSECRET;
        URLS.OAUTH_TOKEN = OAUTH_TOKEN;
        URLS.OAUTH_TOKEN_SECRET = OAUTH_TOKEN_SECRET;
        URLS.WOOCONSUMERKEY = WOOCONSUMERKEY;
        URLS.WOOCONSUMERSECRET = WOOCONSUMERSECRET;
        URLS.PURCHASE_KEY=purchasekey;
    }
}