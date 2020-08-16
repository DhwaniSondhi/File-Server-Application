package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HttpServer {
	static boolean printDebugMessage=false;
	static int port=9000;
	static String filePath="/";
	static boolean upAbleToUnderstand=false;
	/*
	 * Initiate the server
	 */
	public static void initiateServer() throws IOException{
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server started on port : " + port);
		while(true) {
			Socket socket = serverSocket.accept();
			DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
			String ans=dataInputStream.readUTF();
			String inputList[] = ans.split("\\r?\\n");
			new Thread(new ServerImpl(/*socket,*/inputList,socket.getOutputStream())).start();
		}

	}
	/*
	 * The start up method
	 */
	public static void main(String[] args){
		Scanner scan=new Scanner(System.in);
		System.out.println("Please enter the command");
		String[] inputArgs=scan.nextLine().trim().split(" ");
		if(inputArgs[0].equalsIgnoreCase("httpfs")) {
			for(int i=1;i<inputArgs.length;i++) {
				if(inputArgs[i].equalsIgnoreCase("-v")) {
					printDebugMessage=true;
				}else if(inputArgs[i].equalsIgnoreCase("-p")) {
					port = (int)Integer.parseInt(inputArgs[++i]);
				}else if(inputArgs[i].equalsIgnoreCase("-d")) {
					filePath=inputArgs[++i].trim();
					if(!filePath.endsWith("/")) {
						filePath=filePath+"/";
					}
				}
			}
			try {
				initiateServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		scan.close();
	}
}

