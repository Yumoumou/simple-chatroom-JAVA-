package homework1;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientChatThread implements Runnable{
    private Scanner in1;//输入流，用于读入用户想要发送的信息
    private PrintStream ps1;//输出流，用于将用户的信息转发给所有用户
    private Socket toClientSocket;
    private String this_username;//当前发送信息的用户的用户名

    public ClientChatThread(Socket toClientSocket, String username) {
        this.this_username = username;
        this.toClientSocket = toClientSocket;
    }

    @Override
    public void run() {
        try {
            in1 = new Scanner(toClientSocket.getInputStream());//获取输入流
            ps1 = new PrintStream(toClientSocket.getOutputStream());//获取与当前客户的输出流
            //System.out.println("Chat线程启动");

            boolean can_run = true;
            while(can_run && in1.hasNext()){
                String temp = in1.nextLine();//存入用户输入的命令
                if(temp.charAt(0) == '@'){//如果用户输入的是个控制命令
                    //System.out.println("是个控制命令");
                    String cmd = temp.substring(1);//返回第二个字符开始的子字符串 也就是控制命令
                    switch(cmd){
                        case "list" -> {//列出所有在线用户
                            ps1.println("----目前有这些用户在线：----");
                            for (String onlineClient: Server.online_Client) {
                                ps1.println("    "+onlineClient);
                            }
                        }
                        case "quit" -> {//退出聊天程序
                            //PrintStream ps = new PrintStream(toClientSocket.getOutputStream());
                            ps1.println("----您已退出聊天室----");

                            Server.allSocket.remove(toClientSocket);//服务器删除该socket在线的记录
                            Server.online_Client.remove(this_username);//服务器删除该用户在线的记录

                            //关闭资源
                            ps1.println("##shutdown");
                            ps1.close();
                            in1.close();
                            toClientSocket.close();

                            System.out.println(this_username+"已退出聊天室");//在服务器端输出该用户退出的信息
                            broadcast_quit(this_username);//将推出消息广播给其他用户

                            can_run = false;
                        }
                        default -> ps1.println("----命令不存在----");
                    }
                }else{//输入的是普通字符串 视为需要发送的信息
                    broadcast(temp, this_username);//将消息转发给所有客户
                }
            }
            //System.out.println("Chat线程结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String temp, String this_username) throws IOException {//广播（转发）信息
        for (Socket clientSocket: Server.allSocket) {
            if(!clientSocket.equals(toClientSocket)){//不用发给自己
                PrintStream ps = new PrintStream(clientSocket.getOutputStream());//获取与其他客户输出流
                ps.println(this_username+"说： "+temp);
            }
        }
    }

    public void broadcast_quit(String this_username) throws IOException {//广播退出消息
        for (Socket clientSocket: Server.allSocket) {
                PrintStream ps = new PrintStream(clientSocket.getOutputStream());//获取与其他客户输出流
                ps.println("----"+this_username+"已退出聊天室----");
        }
    }
}
