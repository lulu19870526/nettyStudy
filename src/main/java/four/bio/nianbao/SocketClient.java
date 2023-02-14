package four.bio.nianbao;

import java.io.OutputStream;
import java.net.Socket;

/**
 *按照原来的理解，在客户端每次发送一段字符串“这是一个整包!!!”, 分别发送了10次。
 * 服务端应该也会是分10次接收，会打印10行同样的字符串。
 * 但结果却是这样不寻常的结果，这就是由于粘包导致的结果。
 *
 * @Author: zengqx
 * @Date: 2023/2/14 22:25
 */
public class SocketClient {
    public static void main(String[] args) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        String message = "这是一个整包!!!";
        for (int i = 0; i < 10; i++) {
            //Thread.sleep(1);
            outputStream.write(message.getBytes("UTF-8"));
        }
        Thread.sleep(20000);
        outputStream.close();
        socket.close();
    }
}
