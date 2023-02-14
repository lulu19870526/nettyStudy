package four.bio.solution;

import java.io.OutputStream;
import java.net.Socket;

/**
 *对于粘包的情况，要对粘在一起的包进行拆包。对于拆包的情况，要对被拆开的包进行粘包，
 * 即将一个被拆开的完整应用包再组合成一个完整包。
 * 比较通用的做法就是每次发送一个应用数据包前在前面加上四个字节的包长度值，
 * 指明这个应用包的真实长度
 *
 *
 * @Author: zengqx
 * @Date: 2023/2/14 22:37
 */
public class SocketClient {

    /**
     *客户端。每次发送一个字符串前，都将字符串转为字节数组，
     * 在原数据字节数组前再加上一个四个字节的代表该数据的长度，然后将组合的字节数组发送出去；
     */
    public static void main(String[] args) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        String message = "这是一个整包!!!";
        byte[] contentBytes = message.getBytes("UTF-8");
        System.out.println("contentBytes.length = " + contentBytes.length);
        int length = contentBytes.length;
        byte[] lengthBytes = Utils.int2Bytes(length);
        byte[] resultBytes = new byte[4 + length];
        System.arraycopy(lengthBytes, 0, resultBytes, 0, lengthBytes.length);
        System.arraycopy(contentBytes, 0, resultBytes, 4, contentBytes.length);

        for (int i = 0; i < 10; i++) {
            outputStream.write(resultBytes);
        }
        Thread.sleep(20000);
        outputStream.close();
        socket.close();
    }


}
class Utils {
    //int数值转为字节数组
    public static byte[] int2Bytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24 & 0xFF);
        result[1] = (byte) (i >> 16 & 0xFF);
        result[2] = (byte) (i >> 8 & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    //字节数组转为int数值
    public static int bytes2Int(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }
}