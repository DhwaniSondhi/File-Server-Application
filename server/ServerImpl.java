package server;

// post--1hr
// bonus 2---30min
//content-length

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerImpl implements Runnable{
	String[] inputList=null;
	OutputStream outputStream=null;
	HashMap<String,String> headers=new HashMap<String,String>();
	boolean methodNotRight=false;
	boolean notFound=false;
	boolean badRequest=false;
	/*0: inline
	  1: attachment
	  2: attachment; filename
	*/
	boolean contentDisposition=false;
	String outputData="";

	public ServerImpl(){}
	public ServerImpl(String[] inLineList, OutputStream outputStream){
		this.inputList=inLineList;
		int loop=0;
		for(String line:inLineList) {
			inputList[loop]=line;
			loop++;
		}
		this.outputStream=outputStream;
	}

	public void get(String[] firstLines) throws IOException {
		if(HttpServer.printDebugMessage) {
			System.out.println("The client asked for a get request");
		}
		File fileOrFolder=new File(firstLines[1].trim());
		boolean fileExists=fileOrFolder.exists();
		if(fileExists  &&  fileOrFolder.isDirectory()) {
			for(File file:fileOrFolder.listFiles()) {
				outputData+=file.getName()+"\n";
			}
			headers.put("Content-Type", "text");
		}else if(fileExists  &&  fileOrFolder.isFile()) {
			BufferedReader reader=new BufferedReader(new FileReader(fileOrFolder));
			String fileLine="";
			while((fileLine=reader.readLine())!=null) {
				outputData+=fileLine+"\n";
			}
			int extensionIndex=fileOrFolder.getName().lastIndexOf(".")+1;
			String extension=fileOrFolder.getName().substring(extensionIndex);
			if(extension.toUpperCase().equalsIgnoreCase("JSON")) {
				headers.put("Content-Type", "Json");
			}else if(extension.toUpperCase().equalsIgnoreCase("XML")) {
				headers.put("Content-Type", "XML");
			}else if(extension.toUpperCase().equalsIgnoreCase("TXT")) {
				headers.put("Content-Type", "text");
			}else if(extension.toUpperCase().equalsIgnoreCase("HTML")) {
				headers.put("Content-Type", "html");
			}else {
				headers.put("Content-Type", extension.toUpperCase());
			}
		}else if(!fileExists) {
			notFound=true;
		}
	}
	public synchronized void post(String[] firstLines,String saveData) throws IOException {
		if(HttpServer.printDebugMessage) {
			System.out.println("The client asked for a post request.");
		}
		headers.put("Content-Type", "text");
		File file=new File(firstLines[1].trim());
		if(!file.exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fileWriter=new FileWriter(file,false);
		fileWriter.write(saveData);
		fileWriter.close();

	}

	public void receiveData(){
		String[] firstLines=inputList[0].trim().split(" ");
		boolean get=firstLines[0].trim().toUpperCase().equalsIgnoreCase("GET");
		boolean post=firstLines[0].trim().toUpperCase().equalsIgnoreCase("POST");
		boolean bool2=firstLines[1].trim().indexOf(HttpServer.filePath)>-1;

		int loop=1;
		for(;loop<inputList.length;loop++) {
			String[] header=inputList[loop].trim().split(":");
			headers.put(header[0].trim(), header[1].trim());
			if(inputList[loop].toUpperCase().indexOf("HOST")>-1) {
				break;
			}
			if(header[0].equalsIgnoreCase("Content-Disposition")) {
				contentDisposition=true;
			}
		}
		if(!contentDisposition) {
			headers.put("Content-Disposition", "inline");
		}
		
		if((get  ||  post)  &&  bool2) {
			String inputData="";
			String saveData="";
			try{
				if(get) {
					get(firstLines);
				}else if(post) {
					for(;loop<inputList.length;loop++) {
						if(inputList[loop].trim().length()>0) {
							saveData+=inputList[loop]+"\n";
						}
					}
					post(firstLines,saveData);
				}
			}catch(IOException e) {
				badRequest=true;
				//e.printStackTrace();
			}

		}else {
			if(get) {
				System.out.println("The client asked for a get request.");
			}else if(post) {
				System.out.println("The client asked for a post request.");
			}
			methodNotRight=true;
		}
	}
	public void sendData() throws IOException {
		String sendData="";
		for(Map.Entry<String, String> pair:headers.entrySet()) {
			if(!pair.getKey().toUpperCase().equalsIgnoreCase("HOST")) {
				sendData+=pair.getKey()+": "+pair.getValue()+"\r\n";
			}
		}
		if(methodNotRight) {
			String addingData="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\r\n" 
					+"<title>405 Method Not Allowed</title>\r\n" 
					+"<h1>Method Not Allowed</h1>\r\n" 
					+"<p>The method is not allowed for the requested URL.</p>";
			sendData="HTTP/1.1 405 METHOD NOT ALLOWED\r\n"
					+sendData
					+"Content-Length: "+addingData.length()+"\r\n"
					+"Content-Type: html"+"\r\n"
					+"Host: "+headers.get("Host")+"\r\n"
					+"\r\n"
					+addingData;
		}else if(notFound){
			String addingData="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\r\n"  
					+"<title>404 Not Found</title>\r\n"  
					+"<h1>Not Found</h1>\r\n"  
					+"<p>The requested URL was not found on the server.  If you entered the URL manually please check your spelling and try again.</p>\r\n"; 
			sendData="HTTP/1.1 404 NOT FOUND\r\n"
					+sendData
					+"Content-Length: "+addingData.length()+"\r\n"
					+"Content-Type: html"+"\r\n"
					+"Host: "+headers.get("Host")+"\r\n"
					+"\r\n"
					+addingData;
		}else if(badRequest){
			sendData="HTTP/1.1 400 BAD_REQUEST\r\n"
					+sendData
					+"Content-Length: 0"+"\r\n"
					+"Content-Type: html"+"\r\n"
					+"Host: "+headers.get("Host")+"\r\n"
					+"\r\n";
		}else {
			sendData="HTTP/1.1 200 OK\r\n"
					+sendData
					+"Content-Length: "+outputData.length()+"\r\n"
					+"Host: "+headers.get("Host")+"\r\n"
					+"\r\n"
					+outputData;
		}
		DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
		dataOutputStream.writeUTF(sendData);

	}
	@Override
	public void run() {

		try {
			receiveData();
			sendData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
