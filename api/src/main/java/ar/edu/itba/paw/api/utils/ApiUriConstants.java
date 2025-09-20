package ar.edu.itba.paw.api.utils;

public class ApiUriConstants {

    /* Use at class level path */
    public static final String EMPTY = "";

    /* Base */
    public static final String API_BASE = EMPTY + "/api";

    /* AUTH */
    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String LOGIN = AUTH_BASE + "/login";
    public static final String REGISTER = AUTH_BASE + "/register";
    public static final String LOGOUT = AUTH_BASE + "/logout";

    /* USERS */
    public static final String USERS_BASE = API_BASE + "/users";
    public static final String USER_BY_ID = USERS_BASE + "/{id}";


}
