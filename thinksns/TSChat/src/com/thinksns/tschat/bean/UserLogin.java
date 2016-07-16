package com.thinksns.tschat.bean;

/**
 * Created by ZhiYiForMac on 15/12/3.
 */
public class UserLogin extends Entity {
    private int uid;
    private String userName;

    private String token;

    private String tokenSecret;

    private String userFace;

    public UserLogin() {

    }

    public UserLogin(int uid, String username, String token, String tokenSecret, String face) {
        setUid(uid);
        setUserName(username);
        setUserFace(face);
        setToken(token);
        setSecretToken(tokenSecret);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretToken() {
        return tokenSecret;
    }

    public void setSecretToken(String token_secret) {
        this.tokenSecret = token_secret;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }
}
