/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 *
 * @author ThayLe
 */
public class UserStateDataSend implements Serializable{
    public String userID;
    public String userName;
    public String state;
    public String desID;

    public UserStateDataSend(String userID, String userName, String state, String desID) {
        this.userID = userID;
        this.userName = userName;
        this.state = state;
        this.desID = desID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDesID() {
        return desID;
    }

    public void setDesID(String desID) {
        this.desID = desID;
    }
    
}
