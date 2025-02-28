package tn.esprit.models;

public class Session {
    private static utilisateur currentUser;

    public static utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(utilisateur user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logOut() {
        currentUser = null;
    }
}
