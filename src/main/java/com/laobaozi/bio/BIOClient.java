package com.laobaozi.bio;

import com.laobaozi.common.InputUtil;
import com.laobaozi.common.ServerInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class BIOClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(InetAddress.getByName(ServerInfo.HOST), ServerInfo.PORT);
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        scanner.useDelimiter("\n");
        while (true) {
            String line = InputUtil.getLine("Input something:").trim();
            socket.getOutputStream().write((line + "\n").getBytes());
            if (line.equalsIgnoreCase("bye")) {
                break;
            }
            System.out.println("Read resp from remote:" + scanner.nextLine());
        }
        socket.close();
    }

}
