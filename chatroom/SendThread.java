package homework1;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SendThread implements Runnable{
    private Socket clientSocket;
    private PrintStream ps;//网络输出流
    Scanner in = new Scanner(System.in);

    public SendThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String send;//要发送的信息
        try {
            ps = new PrintStream(clientSocket.getOutputStream());
            while(true){
                send = in.nextLine();
                ps.println(send);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
