
package homework1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

//客户端
public class Client {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        Socket clientSocket = null;//客户端socket


        clientSocket = new Socket(InetAddress.getLocalHost(),8300);//尝试与服务端通信


        new Thread (new ReadThread(clientSocket)).start(); //读入服务器端的输入提示
        new Thread (new SendThread(clientSocket)).start();//客户端向服务器端发送信息


    }


}




