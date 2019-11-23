
package Lib;

import Lib.TypeProtocol;
import java.io.Serializable;
import java.net.Socket;


public class MessagePackage implements Serializable {

    private static final long serialVersionUID = 1L;
    private TypeProtocol header;
    private String destUid, srcUid;
    private int port;
    private String username;
    private String password;

    public MessagePackage(TypeProtocol header, String destUid, String srcUid, int port, String username, String password) {
        this.header = header;
        this.destUid = destUid;
        this.srcUid = srcUid;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public MessagePackage(TypeProtocol header, String destUid, String srcUid, int port) {
        this.header = header;
        this.destUid = destUid;
        this.srcUid = srcUid;
        this.port = port;
    }
    public TypeProtocol getHeader() {
        return header;
    }
    public void setHeader(TypeProtocol header) {
        this.header = header;
    }
    public String getDestUid() {
        return destUid;
    }
    public void setDestUid(String destUid) {
        this.destUid = destUid;
    }
    public String getSrcUid() {
        return srcUid;
    }
    public void setSrcUid(String srcUid) {
        this.srcUid = srcUid;
    }
    public MessagePackage(TypeProtocol header, String destUid, String srcUid) {
        super();
        this.header = header;
        this.destUid = destUid;
        this.srcUid = srcUid;
    }
    public MessagePackage(TypeProtocol header) {
        super();
        this.header = header;
    }
    public MessagePackage(TypeProtocol header, String srcUid) {
        super();
        this.header = header;
        this.srcUid = srcUid;
    }

}
