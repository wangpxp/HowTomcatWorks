package ex01.pyrmont;

import jdk.nashorn.internal.ir.RuntimeNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// 一个Web服务器，只等发送指定目录的静态资源请求
public class HttpServer {

    /**
     * Web_Root是HTML和其他文件的目录
     */
    public static final String WEB_ROOT = 
            System.getProperty("user.dir") + File.separator + "webroot";
    
    //关闭命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    
    //接受关闭命令
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    //await()方法在指定端口上等待HTTP请求，对其进行处理，然后发送响应信息回客户端
    private void await() {
        // 创建serverSocket实例来接受请求
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                socket = serverSocket.accept(); // 接收到客户端Socket的请求时，服务器端也会产生一个Socket
                input = socket.getInputStream();
                output = socket.getOutputStream();

                Request request = new Request(input);
                request.parse();

                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                // 关闭Socket
                socket.close();

                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
