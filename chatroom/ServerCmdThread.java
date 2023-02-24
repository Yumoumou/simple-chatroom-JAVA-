package homework1;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class ServerCmdThread implements Runnable{
    private Scanner sc;
    private PrintStream ps;
    private TreeMap<String,String> name_password;

    public ServerCmdThread(TreeMap<String, String> name_password) {

        this.name_password = name_password;
    }

    @Override
    public void run() {
        sc = new Scanner(System.in);
        System.out.println("等待输入服务器命令");
        String cmd;
        while(sc.hasNext()){
            cmd = sc.nextLine();
            switch(cmd){
                case "list" -> {//列出全部在线用户
                    System.out.println("目前所有在线的用户有：");
                    for (String online_user: Server.online_Client) {
                        System.out.println(online_user);
                    }
                }
                case "listall" -> {//列出全部用户
                    System.out.println("所有用户列表：");
                    String[] keys = name_password.keySet().toArray(new String[0]);
                    for (String username : keys) {
                        System.out.println(username);
                    }
                }
                case "quit" -> {//退出系统
                    try{
                        for (Socket socket_connected: Server.allSocket) {//给每个客户发送shutdown命令
                            ps = new PrintStream(socket_connected.getOutputStream());
                            ps.println("##shutdown");
                        }

                        //清除所有在线的记录
                        Server.allSocket.clear();
                        Server.online_Client.clear();
                        sc.close();
                        System.out.println("服务器已关闭");
                        System.exit(0);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
