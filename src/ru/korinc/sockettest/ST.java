package ru.korinc.sockettest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.*;

public class ST extends Activity implements OnClickListener {
	Thread listener;
	EditText ipEt;
	EditText portEt;
	EditText aEt;
	EditText bEt;
	Button reg;
	Button send;
	Socket socket;
	SocketThread st;
	final static int ab=0;
	final static int register=1;
	final static int wat=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_st);

		ipEt = (EditText) findViewById(R.id.etIp);
		portEt = (EditText) findViewById(R.id.etSocket);
		aEt = (EditText) findViewById(R.id.etA);
		bEt = (EditText) findViewById(R.id.etB);
		reg = (Button) findViewById(R.id.bSend);
		send = (Button) findViewById(R.id.bReg);

		reg.setOnClickListener(this);
		send.setOnClickListener(this);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.st, menu);
		return true;
	}

	class SocketThread implements Runnable {

		String ip;
		int port;
		int mode;

		public SocketThread(String ip, int port, int mode) {
			this.ip = ip;
			this.port = port;
			this.mode=mode;

		}

		@Override
		public void run() {
			try {

				InetAddress ipAddress = InetAddress.getByName(ip);
				socket = new Socket(ipAddress, port);
			
				
				send();
				
			} catch (IOException e) {
			//	Toast.makeText(getBaseContext(), e.getMessage(),
			//			Toast.LENGTH_LONG).show();// TODO Auto-generated catch
													// block
				e.printStackTrace();
			}
			
		}
		
		
		public void send() {
			
			while (true) {

				if (socket != null) {

				//	Toast.makeText(getBaseContext(), "socket ������",Toast.LENGTH_LONG).show();
					try {
						
						InputStream sin = socket.getInputStream();
						OutputStream sout = socket.getOutputStream();

						
						DataInputStream in = new DataInputStream(sin);
						DataOutputStream out = new DataOutputStream(sout);
						
						switch(mode){
							case ab:
								out.writeUTF("ab:"+aEt.getText().toString() + "lolParseMe"
											 + bEt.getText().toString());
											 
								runOnUiThread(new Runnable() {
										public void run() {

								aEt.setText(1+(int)(Math.random()*(100-1)+1)+"");
								bEt.setText(1+(int)(Math.random()*(100-1)+1)+"");
								
										}
									});		
								break;
								
							case register:
							
								out.writeUTF("registerMe:"+Utils.getIPAddress(true));
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
					//	Toast.makeText(getBaseContext(), line + "",	Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
			
				int port = Integer.parseInt(portEt.getText().toString());
				new Thread(new SocketThread(ipEt.getText().toString(), port, ab)).start();
		
			

		}else	if(v.getId()==R.id.bReg){

			int port = Integer.parseInt(portEt.getText().toString());
			ServerSocket ss;
			try {
				
				ss = new ServerSocket(port);
				Listener lstnr = new Listener(ss, port);
				new Thread(lstnr).start();
				new Thread(new SocketThread(ipEt.getText().toString(), port, register)).start();
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
		int port;
		public Listener(ServerSocket listenSocket, int port) {
			this.listenSocket = listenSocket;
			this.port = port;
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
							Toast.makeText(getBaseContext(), line, Toast.LENGTH_SHORT).show();
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
