
package manageUser;

import java.io.Serializable;


public class MessagePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private TypeProtocol header;

    private String destUid, srcUid;

    /**
     * @return the header
     */
    public TypeProtocol getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(TypeProtocol header) {
        this.header = header;
    }

    /**
     * @return the destUid
     */
    public String getDestUid() {
        return destUid;
    }

    /**
     * @param destUid the destUid to set
     */
    public void setDestUid(String destUid) {
        this.destUid = destUid;
    }

    /**
     * @return the srcUid
     */
    public String getSrcUid() {
        return srcUid;
    }

    /**
     * @param srcUid the srcUid to set
     */
    public void setSrcUid(String srcUid) {
        this.srcUid = srcUid;
    }

    /**
     * @param header
     * @param destUid
     * @param srcUid
     */
    public MessagePackage(TypeProtocol header, String destUid, String srcUid) {
        super();
        this.header = header;
        this.destUid = destUid;
        this.srcUid = srcUid;
    }

    /**
     * @param header
     */
    public MessagePackage(TypeProtocol header) {
        super();
        this.header = header;
    }

    /**
     * @param header
     * @param srcUid
     */
    public MessagePackage(TypeProtocol header, String srcUid) {
        super();
        this.header = header;
        this.srcUid = srcUid;
    }

}
