package cn.lixiangdong.voicechatroom;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private AudioFormat format;
    /**
     * 初始化socket,连接服务器
     */
    public Client() {
        try {
            System.out.println("正在连接......");
            socket = new Socket("118.190.156.3",8088);
            System.out.println("连接成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void function(){
        //接收线程
        Receive receive=new Receive();
        Thread thread=new Thread(receive);
        thread.start();


        byte[] b=new byte[1024];
        try {
            format = new AudioFormat(22050, 16, 1, true, false);
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format);
            targetDataLine.open(format);
            targetDataLine.start();
            BufferedOutputStream bos=new BufferedOutputStream(socket.getOutputStream());
            while ((targetDataLine.read(b,0,b.length))!=-1){
                bos.write(b,0,b.length);
            }

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Client client=new Client();
        client.function();
    }

    public class Receive implements Runnable{
        @Override
        public void run() {
            SourceDataLine sourceDataLine = null;
            try {
                byte[] b=new byte[1024];
                sourceDataLine = AudioSystem.getSourceDataLine(format);
                sourceDataLine.open(format);
                sourceDataLine.start();
                BufferedInputStream bis=new BufferedInputStream(socket.getInputStream());
                while ((sourceDataLine.write(b,0,b.length))!=-1){
                    bis.read(b);
                }
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
