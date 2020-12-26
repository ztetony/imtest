package com.pack.cs;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MyClient implements  Runnable{


    Socket clientSocket;
    boolean flag=true;
    Thread connenThread;  //向服务器发送消息
    BufferedReader cin;
    DataOutputStream cout;

    public static void main(String[] args) {
         new MyClient().clientStart();

    }

    public  void  clientStart(){
        try {
            System.out.println("客户端连接中");
            clientSocket=new Socket("localhost",9090);     //这是本机连接
            System.out.println(clientSocket.getInetAddress()+":"+clientSocket.getLocalPort());
            System.out.println("已建立连接");
            while (flag){
                InputStream is=clientSocket.getInputStream();
                cin=new BufferedReader(new InputStreamReader(is));
                OutputStream os=clientSocket.getOutputStream();
                cout=new DataOutputStream(os);
                connenThread=new Thread(this);
                connenThread.start();                                //启动线程，向服务器发送信息
                String aLine;
                while ((aLine=cin.readLine())!=null){             //接收服务器端的数据
                    System.out.println(aLine);
                    if (aLine.equals("bye")){
                        flag=false;
                        connenThread.interrupt();
                        break;
                    }
                }

                cout.close();
                os.close();
                cin.close();
                is.close();
                clientSocket.close();     //关闭Socket连接
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while (true){
            int ch;
            try {

                Scanner scanner = new Scanner(System.in);
                while(scanner.hasNextLine()) {
                    String str = scanner.nextLine();
                    cout.write(str.getBytes());
                    cout.flush();         //将缓冲区内容向输出流发送

                }
//                while((ch=System.in.read())!=-1){       //从键盘接收字符并向服务器发送
//                    cout.write((byte)ch);
//                    if(ch=='\n'){
//                        cout.flush();         //将缓冲区内容向输出流发送
//                    }
//                }


            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }
}