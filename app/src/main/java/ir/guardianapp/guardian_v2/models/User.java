package ir.guardianapp.guardian_v2.models;

public class User {

    // Singleton
    private static User single_instance = null;

    // private constructor restricted to this class itself
    private User () {}

    // static method to create instance of Singleton class
    public static User getInstance () {
        if (single_instance == null)
            single_instance = new User();

        return single_instance;
    }

    private String username = "user";
    private String password = "pass";
    private String phoneNum = "09120000000";
    private String token = "";

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getToken() {
        return token;
    }
}
