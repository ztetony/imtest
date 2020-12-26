package com.pack.cs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//点对点聊天程序
public class Myserver implements  Runnable{

    ServerSocket server =null;
    Socket clientSocket;                  //负责当前线程中c/s通信中的Socket对象
    boolean flag = true;                  //标记是否结束
    Thread connenThread;                 //向客户端发送信息的线程
    BufferedReader sin;                  //输入流对象
    DataOutputStream sout;               //输出流对象

    public static void main(String[] args) {

        Myserver myserver=new Myserver();
        myserver.serverStart();
    }

    public void serverStart()
    {
        try {
            server=new ServerSocket(8080);   //建立监听服务
            System.out.println("端口号："+server.getLocalPort());

            while (flag){
                clientSocket=server.accept();
                System.out.println("连接已经建立完毕！");
                InputStream is=clientSocket.getInputStream();
                sin=new BufferedReader(new InputStreamReader(is));
                OutputStream os=clientSocket.getOutputStream();
                sout=new DataOutputStream(os);
                connenThread =new Thread(this);
                connenThread.start();                  //启动线程向客户端发送信息

                String aLine;
                while ((aLine=sin.readLine())!=null)
                {
                    System.out.println(aLine);
                    if(aLine.equals("bye")){
                        flag=false;
                        connenThread.interrupt();
                        break;
                    }
                }

                sout.close();
                os.close();
                sin.close();
                is.close();
                clientSocket.close();
                System.exit(0);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            try {
                int ch;
                while((ch=System.in.read())!=-1)
                {
                    sout.write((byte) ch);  //从键盘接受字符并向客户端发送
                    if(ch=='\n')
                        sout.flush();        //将缓冲区内容向客户端输出

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void finalize(){
        try {
            server.close();      //停止ServerSocket服务
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   //析构方法

}