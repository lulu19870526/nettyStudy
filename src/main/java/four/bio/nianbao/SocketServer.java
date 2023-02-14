package four.bio.nianbao;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * https://blog.csdn.net/fanduifandui/article/details/107215247
 * 首先, 粘包与拆包这两个行为并非是NIO独有的
 * 只要你的应用层协议是基于TCP协议拟定且进行长通讯,
 * 就一定会涉及拆包粘包.开发者在写业务代码时往往感知不到的原因是底层框架已经实现了
 * 相关的细节 这一点与是否是BIO或NIO通讯无关.
 * 个人认为粘包 拆包跟IO模式完全是两个不同维度的概念.
 *
 *
 * https://zhuanlan.zhihu.com/p/77275039
 *
 * 实现服务端代码，服务监听55533端口，没有指定IP地址默认就是localhost，
 * 即本机IP环回地址 127.0.0.1，接着就等待客户端连接
 *
 * 先运行服务端代码，运行到server.accept()时阻塞，
 * 打印“server将一直等待连接的到来”来等待客户端的连接，接着再运行客户端代码；
 *
 * @Author: zengqx
 * @Date: 2023/2/14 22:23
 */
public class SocketServer {


    public static void main(String[] args) throws Exception {
        // 监听指定的端口
        int port = 55533;
        ServerSocket server = new ServerSocket(port);

        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");
        Socket socket = server.accept();
        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024 * 1024];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            String content = new String(bytes, 0, len,"UTF-8");
            System.out.println("len = " + len + ", content: " + content);
        }
        inputStream.close();
        socket.close();
        server.close();
    }
}
