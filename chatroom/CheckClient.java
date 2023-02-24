package homework1;


import java.io.*;
import java.net.Socket;
import java.util.*;

//判断用户所输入的用户名密码是否正确
public class CheckClient implements Runnable{
    private Socket toClientSocket = null;
    private Scanner in;//读入
    private PrintStream ps;//输出
    private final TreeMap<String,String> name_password;//用户名和密码


    public CheckClient(Socket toClientSocket, TreeMap<String,String> name_password) {
        this.toClientSocket = toClientSocket;
        this.name_password = name_password;
    }

    @Override
    public void run() {
        //给客户端输出“输入用户名和密码”的提示
        try{
            ps = new PrintStream(new PrintStream(toClientSocket.getOutputStream()));//获取输出流
            in = new Scanner(toClientSocket.getInputStream());
            ps.println(("----请输入用户名和密码（用回车分隔）----"));
            String username = in.nextLine();//读入用户所输入的用户名
            String password = in.nextLine();//读入用户所输入的密码

            //判断密码是否正确
            boolean match_res = match(username,password);
            while(!match_res){
                ps.println("----密码错误，请继续输入密码,或输入\"quit\"放弃登录----");
                String temp = in.nextLine();
                if(temp.equals("quit")){
                    ps.println("----您已放弃登录----");
                    //关闭资源
                    ps.println("##shutdown");
                    ps.close();
                    in.close();
                    toClientSocket.close();
                    break;
                }
                else
                    match_res = match(username,temp);
            }

            //密码正确，进入聊天室
            if(match_res){
                welcome(username);
                new Thread(new ClientChatThread(toClientSocket,username)).start();//进入聊天室
            }

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public boolean match(String username, String password){//判断密码是否正确
        String true_password = name_password.get(username);
        return true_password.equals(password);
    }

    public void welcome(String username) throws IOException {
        System.out.println(username+"已进入聊天室");//在服务器显示用户登录的信息
        ps.println("----密码正确,欢迎进入聊天室----");//给该客户发送欢迎信息
        //将该用户列入在线用户
        Server.online_Client.add(username);
        Server.allSocket.add(toClientSocket);

        //给其他客户发送该用户进入聊天的欢迎信息
        broadcastWelcome(username);
    }

    public void broadcastWelcome(String this_username) throws IOException {
        for (Socket clientSocket: Server.allSocket) {
            if(!clientSocket.equals(toClientSocket)){//不用发给自己
                ps = new PrintStream(clientSocket.getOutputStream());//获取与其他客户输出流
                ps.println("----欢迎"+this_username+"进入聊天室----");
            }

        }
    }
}



