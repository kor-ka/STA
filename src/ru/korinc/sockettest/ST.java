package ru.korinc.sockettest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ST extends Activity implements OnClickListener {
	Thread listener;
	EditText ipEt;
	EditText portEt;
	EditText clientPortEt;
	EditText aEt;
	EditText bEt;
	Button reg;
	Button send;
	Socket socket;
	SocketThread st;
	ServerSocket	ss;
	ListView lv;
	ArrayAdapter<String> adapter;
	ArrayList<String> results;
	final static int ab=0;
	final static int register=1;
	final static int wat=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_st);

		ipEt = (EditText) findViewById(R.id.etIp);
		portEt = (EditText) findViewById(R.id.etSocket);
		clientPortEt = (EditText) findViewById(R.id.etPort);
		aEt = (EditText) findViewById(R.id.etA);
		bEt = (EditText) findViewById(R.id.etB);
		reg = (Button) findViewById(R.id.bSend);
		send = (Button) findViewById(R.id.bReg);
		lv = (ListView) findViewById(R.id.listView1);

		reg.setOnClickListener(this);
		send.setOnClickListener(this);
		
		results=new ArrayList<String>();
		
		adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, results);
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.st, menu);
		return true;
	}

	class SocketThread implements Runnable {

		String ip;
		int port;
		int mode;
		int a;
		int b;

		public SocketThread(String ip, int port, int mode, int a, int b) {
			this.ip = ip;
			this.port = port;
			this.mode=mode;
			this.a=a;
			this.b=b;

		}

		@Override
		public void run() {
			try {

				InetAddress ipAddress = InetAddress.getByName(ip);
				socket = new Socket(ipAddress, port);
			
				
				send();
				
			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
		}
		
		
		public void send() {
			
			while (true) {

				if (socket != null) {

		
					try {
						
						InputStream sin = socket.getInputStream();
						OutputStream sout = socket.getOutputStream();

						
						DataInputStream in = new DataInputStream(sin);
						DataOutputStream out = new DataOutputStream(sout);
						
						switch(mode){
							case ab:
								out.writeUTF("ab:"+a+ ":"+b);
											 
								break;
								
							case register:
							
								out.writeUTF("registerMe:"+clientPortEt.getText().toString());
								break; 
						}

						 

					
						
						out.flush(); 
										
						final String line = in.readUTF(); 
											
						runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(getBaseContext(), line, Toast.LENGTH_SHORT).show();
								}
							});						
						socket.close();
						socket = null;
			
					} catch (IOException e) {
			
						e.printStackTrace();
					}
					break;
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.bSend){
			try{
				
				int a = Integer.parseInt(aEt.getText().toString());
				int b = Integer.parseInt(bEt.getText().toString());
				bEt.setText(Integer.parseInt(bEt.getText().toString())+1+"");
				int port = Integer.parseInt(portEt.getText().toString());
				new Thread(new SocketThread(ipEt.getText().toString(), port, ab, a, b)).start();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			

		}else	if(v.getId()==R.id.bReg){

			
			try {
				
				int port = Integer.parseInt(clientPortEt.getText().toString());
				int serverport = Integer.parseInt(portEt.getText().toString());
				
				if (ss == null){
					ss = new ServerSocket(port);
					Listener lstnr = new Listener(ss);
					new Thread(lstnr).start();
					clientPortEt.setEnabled(false);
				}
				
				new Thread(new SocketThread(ipEt.getText().toString(), serverport, register, 0, 0)).start();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			

		}
	}
	
	public class Listener extends Thread {
		protected ServerSocket listenSocket;
		DataOutputStream out;
		Socket socket;
		
		public Listener(ServerSocket listenSocket) {
			this.listenSocket = listenSocket;
			
		}

		public void run() {

			while (true) {
				try {

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getBaseContext(), "waiting", Toast.LENGTH_SHORT).show();
						}
					});	
					
					socket = listenSocket.accept();

					InputStream sin = socket.getInputStream();
					OutputStream sout = socket.getOutputStream();

					DataInputStream in = new DataInputStream(sin);
					DataOutputStream out = new DataOutputStream(sout);

					

					final String line = in.readUTF();
					
					runOnUiThread(new Runnable() {
						public void run() {
					
							if(line.contains("results:")){
								results.clear();
								if(line.length()>8){
									List<String> list =new ArrayList<String> (Arrays.asList(line.substring(9).split(":")));
									results = (ArrayList<String>) list;	
								}
								
								Toast.makeText(getBaseContext(), "incoming: "+results.size(), Toast.LENGTH_SHORT).show();
								adapter.clear();
								for(String s:results){
									adapter.add(s);
								}
								adapter.notifyDataSetChanged();
							}else{
								Toast.makeText(getBaseContext(), line, Toast.LENGTH_SHORT).show();
							}
					
							
						}
					});	
					
				} catch (final IOException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});	
					e.printStackTrace();
				}

			}

		}
	}
}
