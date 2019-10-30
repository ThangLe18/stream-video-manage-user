/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author ThayLe
 */
public class UserState {
    public String userID;
    public String userName;
    public InputStream ctr_is;
    public OutputStream ctr_os;
    public InputStream video_is;
    public OutputStream video_os;
    public String state;
    public String desID;

    public UserState(String userID, String userName, InputStream ctr_is, OutputStream ctr_os, InputStream video_is, OutputStream video_os, String state, String desID) {
        this.userID = userID;
        this.userName = userName;
        this.ctr_is = ctr_is;
        this.ctr_os = ctr_os;
        this.video_is = video_is;
        this.video_os = video_os;
        this.state = state;
        this.desID = desID;
    }

    public UserState(String userID, String userName, String state) {
        this.userID = userID;
        this.userName = userName;
        this.state = state;
    }
    
    
}
