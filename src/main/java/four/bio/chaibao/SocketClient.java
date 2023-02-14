package four.bio.chaibao;

import java.io.OutputStream;
import java.net.Socket;

/**
 * 从抓包过程就能看出，客户端发送一个字符串，被拆成了两个TCP数据报进行传输。
 *
 * @Author: zengqx
 * @Date: 2023/2/14 22:31
 */
public class SocketClient {

    private final static String CONTENT = "这是一个很长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串这是一个很.....长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串这是一个很长很长的字符串";//测试时大于5461文字，由于篇幅所限，只用这一段作为代表

    public static void main(String[] args) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        //String finalContent = CONTENT+""+CONTENT+""+CONTENT+""+CONTENT;
        outputStream.write(CONTENT.getBytes("UTF-8"));
        Thread.sleep(20000);
        outputStream.close();
        socket.close();
    }
}
