package homework1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

//服务器
public class Server {
    public static ArrayList<String> online_Client = new ArrayList<String>();//所有线上的用户的用户名,其他类也可以修改
    public static ArrayList<Socket> allSocket = new ArrayList<Socket>();//所有连接到的Socket


    public static void main(String[] args) throws IOException {
        //读入一个装有所有用户名和密码的文件
        TreeMap<String,String> name_password = new TreeMap<String,String>();
        File file = new File("name_password.txt");
        BufferedReader BR;
        try{
            BR = new BufferedReader(new FileReader(file));
            String name,password;
            name = BR.readLine();
            password = BR.readLine();

            while(name != null){
                name_password.put(name,password);//将读入的用户名和密码相应记入map
                name = BR.readLine();
                password = BR.readLine();
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        //等待控制台输入命令
        new Thread (new ServerCmdThread(name_password)).start();

        //在固定端口（8300）侦听是否有客户端接入
        ServerSocket listenSocket = new ServerSocket(8300);
        Socket toClientSocket = null;//用于与客户端通信的socket
        System.out.println("服务器已启动，等待用户接入");

        while (true){
                toClientSocket = listenSocket.accept(); //当侦听socket侦听到客户端尝试接入，将这个客户端的信息记下
                System.out.printf("服务器检测到"+"%s"+"尝试接入\n",toClientSocket.getInetAddress().getHostAddress());

                //启动检查客户所输入的用户名密码的线程
                new Thread(new CheckClient(toClientSocket,name_password)).start();
        }
    }

}


