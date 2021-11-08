package cn.lixiangdong.voicechatroom;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    BufferedOutputStream[] bos={};
    ServerSocket serverSocket;
    public Server() {
        try {
            System.out.println("开始监听端口");
            serverSocket=new ServerSocket(8088);
            System.out.println("监听成功");
        } catch (IOException e) {
            System.out.println("监听失败，请检查端口是否被占用");
            e.printStackTrace();
        }

    }

    public void function(){
        try {
            ClientProcessing clientProcessing;
            while (true) {
                System.out.println("等待连接");
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + "已连接");
                //将socket交给新线程
                clientProcessing=new ClientProcessing(socket);
                Thread thread=new Thread(clientProcessing);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        Server server=new Server();
        server.function();
    }
    class ClientProcessing implements Runnable{
        Socket socket;
        public ClientProcessing(Socket socket) {
            this.socket=socket;
        }

        @Override
        public void run() {
            try {
                //接收
                BufferedInputStream bis=new BufferedInputStream(socket.getInputStream());

                //发送
                //扩容数组
                bos= Arrays.copyOf(bos,bos.length+1);
                bos[bos.length-1]=new BufferedOutputStream(socket.getOutputStream());

                byte[]b=new byte[1024];
                while (bis.read(b,0,b.length)!=-1){
                    for (int i=0;i<bos.length;i++){
                        bos[i].write(b,0,b.length);
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
