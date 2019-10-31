/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;
import java.util.ArrayList;

public class Test{
    public static void main(String[] args) {
        ArrayList<MessagePackage> p = new ArrayList<>();
        p.add(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,
                            "asd", "hjjh", 443));
        p.add(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,
                            "asd", "hjjh", 121));
        p.add(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,
                            "asd", "hjjh", 312));
        p.get(1).setPort(111);
        System.out.println(p.get(1).getPort());
    }
}