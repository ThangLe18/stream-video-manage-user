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
    public InputStream ctr_is;
    public OutputStream ctr_os;
    public InputStream video_is;
    public OutputStream video_os;

    public UserState(String userID, InputStream ctr_is, OutputStream ctr_os) {
        this.userID = userID;
        this.ctr_is = ctr_is;
        this.ctr_os = ctr_os;
    }
    
    
}
