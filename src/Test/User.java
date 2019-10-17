/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

/**
 *
 * @author ThayLe
 */
public class User {
    public String _id;
    public Boolean active;
    public int portClient;

    public User(String _id, Boolean active, int portClient) {
        this._id = _id;
        this.active = active;
        this.portClient = portClient;
    }
    
}
