package homework1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ReadThread implements Runnable{
    private Scanner in;//网络输入流
    private Socket clientSocket;//与服务器连接的socket
    public ReadThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String temp;
        try {
            in = new Scanner(clientSocket.getInputStream());
            do{
                temp = in.nextLine();//读入
                if(temp.equals("##shutdown")) {//如果服务器要求关闭
                    System.out.println("已断开和服务器的连接");
                    System.exit(0);
                }else
                System.out.println(temp);
            }while(in.hasNext());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
