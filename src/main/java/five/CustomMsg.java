package five;

/**
 * 在当今比较流行的水平拆分的架构之下，RPC协议很是流行，这样可以使各个项目解耦，
 * 使得更加灵活，每个项目之间通过远程调用交互，相互之间定义一个通讯私有协议，
 * 然后解析，这样就可以进行数据接口交互
 *
 * 协议类
 *
 * 我们规定两个系统通过Netty去发送这样的一个格式的信息，CustomMsg中包含这样的几类信息：
 * 1）type表示发送端的系统类型
 * 2）flag表示发送信息的类型，是业务数据，还是心跳包数据
 * 3）length表示主题body的长度
 * 4）body表示主题信息
 *
 * 有了这样的相互规定，发送端与接收端按照这种格式去编码和解码数据，这样就很容易的进行数据交互
 * Netty提供的类叫做LengthFieldBasedFrameDecoder
 */
public class CustomMsg {

    //类型  系统编号 0xAB 表示A系统，0xBC 表示B系统
    private byte type;

    //信息标志  0xAB 表示心跳包    0xBC 表示超时包  0xCD 业务信息包
    private byte flag;

    //主题信息的长度
    private int length;

    //主题信息
    private String body;

    public CustomMsg() {

    }

    public CustomMsg(byte type, byte flag, int length, String body) {
        this.type = type;
        this.flag = flag;
        this.length = length;
        this.body = body;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
