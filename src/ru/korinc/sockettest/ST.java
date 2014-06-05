package ru.korinc.sockettest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.*;

public class ST extends Activity implements OnClickListener {
	Thread listener;
	EditText ipEt;
	EditText portEt;
	EditText clientPortEt;
	EditText aEt;
	EditText bEt;
	Button scan;
	Button send;
	
	float fullmovex;
	float fullmovey;
	boolean isDouble = false;
	
	SocketThread st;
	ServerSocket	ss;
	LinearLayout ll;
	TextView tv;
	ArrayAdapter<String> adapter;
	ArrayList<String> results;
	final static int ab=0;
	final static int register=1;
	final static int wat=2;
	final static int click=3;
	final static int dndDown=4;
	final static int dndUp=5;
	final static int rclick=6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_st);

		ipEt = (EditText) findViewById(R.id.etIp);
		portEt = (EditText) findViewById(R.id.etSocket);
		clientPortEt = (EditText) findViewById(R.id.etPort);
		aEt = (EditText) findViewById(R.id.etA);
		bEt = (EditText) findViewById(R.id.etB);
		scan = (Button) findViewById(R.id.bScan);
		send = (Button) findViewById(R.id.bSend);
		ll = (LinearLayout) findViewById(R.id.ll);
		tv = (TextView) findViewById(R.id.tv);
		
		
		ll.setLongClickable(true);
		ll.setClickable(true);
		ll.setOnLongClickListener(new View.OnLongClickListener(){

			
		long timeLongDownOld=System.currentTimeMillis();
		long timeLongDown=System.currentTimeMillis();
				@Override
				public boolean onLongClick(View p1)
				{
					
					if(fullmovey<5 & fullmovex<5){
					//	Toast.makeText(getBaseContext(), "long", Toast.LENGTH_SHORT).show();
						int port = Integer.parseInt(portEt.getText().toString());
						
						new Thread(new SocketThread(ipEt.getText().toString(), port, dndDown, 0, 0)).start();
						isDouble = true;
						timeLongDown=System.currentTimeMillis();
						if(timeLongDown-timeLongDownOld<1000){
							new Thread(new SocketThread(ipEt.getText().toString(), port, rclick, 0, 0)).start();
						}
						timeLongDownOld=System.currentTimeMillis();
						return true;
					}
					
					return false;
				}
				
			
		});
		

		scan.setOnClickListener(this);
		send.setOnClickListener(this);
		
		scan.performClick();
		
		results=new ArrayList<String>();
		
		OnTouchListener otl = new OnTouchListener(){
			float oldx;
			float oldy;
			float movex;
			float movey;
			
			float downx;
			float downy;
			float x;
			float y;
			String sDown;
			String sMove;
			String sUp;
			long timeDownOld=System.currentTimeMillis();
			long timeDown=System.currentTimeMillis();
			long timeUp=System.currentTimeMillis();
		
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getX();
				y = event.getY();
				
				
				int port;
				int a;
				int b;

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: 
						timeDown = System.currentTimeMillis();
						sDown = "Down: " + x + "," + y + "|" + timeDown;
						sMove = ""; sUp = "";
						oldx=x;
						oldy=y;
						downx=x;
						downy=y;
						
						if(timeDown - timeDownOld < 500 && (fullmovex<10 & fullmovey<10) ){
							//Toast.makeText(getBaseContext(), movex+"|"+movey, Toast.LENGTH_LONG).show();
							isDouble = true;
							//send dnd down
							port = Integer.parseInt(portEt.getText().toString());
							//new Thread(new SocketThread(ipEt.getText().toString(), port, dndDown, 0, 0)).start();
							
						}
						
						timeDownOld = timeDown;
						
						//Toast.makeText(getBaseContext(), "TouchDown:"+timeDown.toString(), Toast.LENGTH_SHORT).show();
						break;
						
					case MotionEvent.ACTION_MOVE: 
						sMove = "Move: x_" + x + "\nMove: y_" + y;
						movex=(x-oldx);
						movey=(y-oldy);
						
						// Need for control long click
						fullmovex=x-downx;
						fullmovey=y-downy;
						if (fullmovex<0) {
							fullmovex=fullmovex*-1;
						}
						
						if (fullmovey<0) {
							fullmovey=fullmovey*-1;
						}
						//
						
						a = Math.round(movex);
						b = Math.round(movey);
						
						port = Integer.parseInt(portEt.getText().toString());
						
						new Thread(new SocketThread(ipEt.getText().toString(), port, ab, a, b)).start();
						oldx=x;
						oldy=y;
						break;
					case MotionEvent.ACTION_UP: 
					case MotionEvent.ACTION_CANCEL:  
						timeUp = System.currentTimeMillis();
						sMove = "";
						sUp = "Up: " + x + "," + y + "|" + timeUp;
						fullmovex=x-downx;
						fullmovey=y-downy;
						if (fullmovex<0) {
							fullmovex=fullmovex*-1;
						}
						
						if (fullmovey<0) {
							fullmovey=fullmovey*-1;
						}
						if((timeUp-timeDown)<100 && (fullmovex<20 & fullmovey<20) && !isDouble){
							port = Integer.parseInt(portEt.getText().toString());							
							new Thread(new SocketThread(ipEt.getText().toString(), port, click, 0, 0)).start();
							
						}
						
						if((timeUp-timeDown)<100 && (fullmovex<20 & fullmovey<20) && isDouble){
							//send dnd up
							port = Integer.parseInt(portEt.getText().toString());		
							new Thread(new SocketThread(ipEt.getText().toString(), port, dndUp, 0, 0)).start();
							
							isDouble = false;
						}
						
						break;
				}
				tv.setText(sDown + "\n" + sMove + "\n" + sUp);
				return false;
			}


		};
		
		ll.setOnTouchListener(otl);
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.st, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		scan.performClick();
		return super.onOptionsItemSelected(item);
	}

	class SocketThread implements Runnable {

		String ip;
		int port;
		int mode;
		int a;
		int b;
		Socket socket;

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
				socket = new Socket();
				socket.connect(new InetSocketAddress(ipAddress, port), 10000);
				
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

				
					try {
						
						InputStream sin = socket.getInputStream();
						OutputStream sout = socket.getOutputStream();

						
						DataInputStream in = new DataInputStream(sin);
						DataOutputStream out = new DataOutputStream(sout);
						
						switch(mode){
							case ab:
								out.writeUTF("ab:"+a+ "lolParseMe"+b);
											 
								break;
								
							case click:
								out.writeUTF("click:");
											 
								break;	
								
							case rclick:
								out.writeUTF("rclick:");

								break;
								
							case dndDown:
								out.writeUTF("dndDown:");
											 
								break;	
								
							case dndUp:
								out.writeUTF("dndUp:");
											 
								break;	
								
							case register:
							
								out.writeUTF("registerMe:"+clientPortEt.getText().toString());
								break; 
						}

						 

					
						
						out.flush(); 
										
						final String line = in.readUTF(); 
										
						socket.close();
					//	socket = null;
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
			try{
				
				int a = Integer.parseInt(aEt.getText().toString());
				int b = Integer.parseInt(bEt.getText().toString());
				bEt.setText(Integer.parseInt(bEt.getText().toString())+1+"");
				int port = Integer.parseInt(portEt.getText().toString());
				new Thread(new SocketThread(ipEt.getText().toString(), port, ab, a, b)).start();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			

		}else	if(v.getId()==R.id.bScan){

			
			boolean installed  =   appInstalledOrNot("com.google.zxing.client.android");
			if(installed){
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
    			intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
    			
    			
			startActivityForResult(intent, 0);
			}else{String url = "https://play.google.com/store/apps/details?id=com.google.zxing.client.android";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);}
			

		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
    		if (resultCode == RESULT_OK) {
        			String contents = intent.getStringExtra("SCAN_RESULT");
        			String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
        			String[] adressParts = contents.split(":");
        			String IP = adressParts[0];
        			String port = adressParts[1];
        			
        			ipEt.setText(IP);
        			portEt.setText(port);
    		}else{
    			
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
	
	
	//Utils
	//check is app installed
		private boolean appInstalledOrNot(String uri)
		{
			PackageManager pm = getPackageManager();
			boolean app_installed = false;
			try
			{
				pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
				app_installed = true;
			}
			catch (PackageManager.NameNotFoundException e)
			{
				app_installed = false;
			}
			return app_installed ;
	    }

	
	
}
