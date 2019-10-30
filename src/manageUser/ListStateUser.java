/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ThayLe
 */
public class ListStateUser implements Serializable{
    public ArrayList<UserState> listUserState; 

    public ArrayList<UserState> getListUserState() {
        return listUserState;
    }

    public void setListUserState(ArrayList<UserState> listUserState) {
        this.listUserState = listUserState;
    }

    public ListStateUser(ArrayList<UserState> listUserState) {
        this.listUserState = listUserState;
    }

    
    
}
