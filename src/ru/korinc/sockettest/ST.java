package ru.korinc.sockettest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyCharacterMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class ST extends Activity implements OnClickListener {
	Thread listener;
	EditText ipEt;
	EditText portEt;
	EditText clientPortEt;
	EditText aEt;
	EditText bEt;
	EditText keyboardEt;
	Button scan;
	Button send;
	Button b1;
	Button b2;
	Button b3;
	Button b4;
	ImageButton up;
	ImageButton down;
	ImageButton left;
	ImageButton right;
	Button esc;
	Button enter;
	SharedPreferences shp;
	Editor ed;
	float fullmovex;
	float fullmovey;
	boolean isDouble = false;
	KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
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
	final static int keyboard=7;
	final static int launch=8;
	private static final int REQUEST_CODE = 1234;
	private static final int REQUEST_CODE_VOICE_INPUT = 12345;
	private static final int REQUEST_CODE_B1 = 12346;
	private static final int REQUEST_CODE_B2 = 12347;
	private static final int REQUEST_CODE_B3 = 12348;
	private static final int REQUEST_CODE_B4 = 12349;
	private static final String FN_SAVE_B1 = "fnB1";
	private static final String FN_SAVE_B2 = "fnB2";
	private static final String FN_SAVE_B3 = "fnB3";
	private static final String FN_SAVE_B4 = "fnB4";
	
	FnButton fnb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 try {
	            ViewConfiguration config = ViewConfiguration.get(this);
	            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	            if(menuKeyField != null) {
	                menuKeyField.setAccessible(true);
	                menuKeyField.setBoolean(config, false);
	            }
	        } catch (Exception ex) {
	            // Ignore
	        }
	        
		shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ed = shp.edit();
		setContentView(R.layout.activity_st);

		ipEt = (EditText) findViewById(R.id.etIp);
		portEt = (EditText) findViewById(R.id.etSocket);
		clientPortEt = (EditText) findViewById(R.id.etPort);
		aEt = (EditText) findViewById(R.id.etA);
		bEt = (EditText) findViewById(R.id.etB);
		keyboardEt = (EditText) findViewById(R.id.etKeyboard);
		
		scan = (Button) findViewById(R.id.bScan);
		send = (Button) findViewById(R.id.bSend);
		
		b1 = (Button) findViewById(R.id.buttonB1);
		b2 = (Button) findViewById(R.id.buttonB2);
		b3 = (Button) findViewById(R.id.buttonB3);
		b4 = (Button) findViewById(R.id.buttonB4);
		
		up = (ImageButton) findViewById(R.id.buttonUp);
		down = (ImageButton) findViewById(R.id.buttonDown);
		left = (ImageButton) findViewById(R.id.buttonLeft);
		right = (ImageButton) findViewById(R.id.buttonRight);
		
		esc = (Button) findViewById(R.id.buttonEsc);
		enter = (Button) findViewById(R.id.buttonEnter);
		
		ll = (LinearLayout) findViewById(R.id.ll);
		
		tv = (TextView) findViewById(R.id.tv);
		
		fnb = new FnButton(this);
		
		ipEt.setText(shp.getString("ip", ""));
		portEt.setText(shp.getString("port", "1234"));
						
		keyboardEt.setText("<>");
		keyboardEt.setSelection(keyboardEt.getText().length());
		keyboardEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>0 && keyboardEt.getText().toString().length()>2){
					String pressed = s.toString().replace("<>","");
			//		int spaces = pressed.length() - pressed.replaceAll(" ", "").length();
					
					int port = Integer.parseInt(portEt.getText().toString());	
					if(s.length()==pressed.length()|(s.length()==3&&pressed.length()==1)){
						
						new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, pressed)).start();
					}
					
					keyboardEt.setText("<>");
					keyboardEt.setSelection(keyboardEt.getText().length());
				} else if(keyboardEt.getText().toString().equals("<")){
					
					int port = Integer.parseInt(portEt.getText().toString());		
					new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "bksps")).start();
					keyboardEt.setText("<>");
					keyboardEt.setSelection(keyboardEt.getText().length());
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
				
			}
		});
		
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
		
		
		OnLongClickListener olclFn = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				int reqToSend=0;
				switch (v.getId()) {
				case R.id.buttonB1:
					reqToSend = REQUEST_CODE_B1;
					break;					
				
				case R.id.buttonB2:
					reqToSend = REQUEST_CODE_B2;
					break;					
				
				case R.id.buttonB3:
					reqToSend = REQUEST_CODE_B3;
					break;					
				
				case R.id.buttonB4:
					reqToSend = REQUEST_CODE_B4;
					break;					
			}
				Intent intent = new Intent(getBaseContext(), FnSelect.class);
				startActivityForResult(intent, reqToSend);
				return false;
			}
		};
		
		b1.setOnClickListener(this);
		b1.setText(fnb.fnMap.get(shp.getInt(FN_SAVE_B1, fnb.NO_FUNCTION)));
		b1.setOnLongClickListener(olclFn);
		b2.setOnClickListener(this);
		b2.setText(fnb.fnMap.get(shp.getInt(FN_SAVE_B2, fnb.NO_FUNCTION)));
		b2.setOnLongClickListener(olclFn);
		b3.setOnClickListener(this);
		b3.setText(fnb.fnMap.get(shp.getInt(FN_SAVE_B3, fnb.NO_FUNCTION)));
		b3.setOnLongClickListener(olclFn);
		b4.setOnClickListener(this);
		b4.setText(fnb.fnMap.get(shp.getInt(FN_SAVE_B4, fnb.NO_FUNCTION)));
		b4.setOnLongClickListener(olclFn);
		
		scan.setOnClickListener(this);
		send.setOnClickListener(this);
		
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		esc.setOnClickListener(this);
		enter.setOnClickListener(this);
		
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
						
						//Make module
						if (fullmovex<0) {
							fullmovex=fullmovex*-1;
						}
						
						if (fullmovey<0) {
							fullmovey=fullmovey*-1;
						}
						
						//Click
						if((timeUp-timeDown)<200 && (fullmovex<30 & fullmovey<30) && !isDouble){
							port = Integer.parseInt(portEt.getText().toString());							
							new Thread(new SocketThread(ipEt.getText().toString(), port, click, 0, 0)).start();
							
						}
						
						//Long click relise
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
		
		if(shp.getBoolean("enterOnVoiceInput",true)){
			Menu settings= menu.getItem(2).getSubMenu();
			settings.getItem(0).setChecked(true);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int port = Integer.parseInt(portEt.getText().toString());	
		
		switch (item.getItemId()) {
		case R.id.action_scan:
			scan.performClick();
			break;
		case R.id.enterAfterVoiceInput:
				if(shp.getBoolean("enterOnVoiceInput",true)){
					item.setChecked(false);
				}else{
					item.setChecked(true);
				}
				ed.putBoolean("enterOnVoiceInput", item.isChecked());
				ed.commit();
			break;
			
		case R.id.map:
			Intent intent = new Intent(this, MappingList.class);
			startActivity(intent);
			break;
			
		case R.id.arrows:
			switch (up.getVisibility()) {
			case View.VISIBLE:
				up.setVisibility(View.GONE);
				down.setVisibility(View.GONE);
				left.setVisibility(View.GONE);
				right.setVisibility(View.GONE);
				esc.setVisibility(View.GONE);
				enter.setVisibility(View.GONE);
				break;

			case View.GONE:
				up.setVisibility(View.VISIBLE);
				down.setVisibility(View.VISIBLE);
				left.setVisibility(View.VISIBLE);
				right.setVisibility(View.VISIBLE);
				esc.setVisibility(View.VISIBLE);
				enter.setVisibility(View.VISIBLE);
				break;
			}
			break;


		case R.id.keyboard:
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
			InputMethodManager.HIDE_IMPLICIT_ONLY);
			break;
		
		case R.id.launchApp:
			startVoiceRecognitionActivity(REQUEST_CODE);
			
			break;
			
		case R.id.voiceInput:
			startVoiceRecognitionActivity(REQUEST_CODE_VOICE_INPUT);

			break;
			
	case R.id.contextMenu:
			
		new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "contextMenu")).start();
		break;
		
	
	}
		
		return super.onOptionsItemSelected(item);
	}
	
	

	
	class SocketThread implements Runnable {

		String ip;
		int port;
		int mode;
		int a;
		int b;
		String chr;
		Socket socket;

		public SocketThread(String ip, int port, int mode, int a, int b) {
			this.ip = ip;
			this.port = port;
			this.mode=mode;
			this.a=a;
			this.b=b;

		}
		
		public SocketThread(String ip, int port, int mode, String chr) {
			this.ip = ip;
			this.port = port;
			this.mode=mode;
			this.chr = chr;

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
								
							case keyboard:
								
								out.writeUTF("keyboard::"+chr);
								break;
								
							case launch:
								
								out.writeUTF("launch:"+chr);
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
		int port = Integer.parseInt(portEt.getText().toString());
		switch (v.getId()) {
		case R.id.bSend:
				try{
				
				int a = Integer.parseInt(aEt.getText().toString());
				int b = Integer.parseInt(bEt.getText().toString());
				bEt.setText(Integer.parseInt(bEt.getText().toString())+1+"");
				
				new Thread(new SocketThread(ipEt.getText().toString(), port, ab, a, b)).start();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			break;

		case R.id.bScan:
			
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
			
			break;
			
		case R.id.buttonUp:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "up")).start();
			break;
			
		case R.id.buttonDown:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "down")).start();
			break;
			
		case R.id.buttonLeft:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "left")).start();
			break;
			
		case R.id.buttonRight:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "right")).start();
			break;
			
		case R.id.buttonEsc:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "esc")).start();
			break;
			
		case R.id.buttonEnter:
					
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "enter")).start();
			break;	
			
		case R.id.buttonB1:
			int bindedFunction1 = shp.getInt(FN_SAVE_B1, fnb.NO_FUNCTION);
			if(bindedFunction1 == fnb.NO_FUNCTION){
				Intent intentB1 = new Intent(this, FnSelect.class);
				startActivityForResult(intentB1, REQUEST_CODE_B1);
			}else{
				fnb.press(bindedFunction1);
			}
			break;
		
		case R.id.buttonB2:
			int bindedFunction2 = shp.getInt(FN_SAVE_B2, fnb.NO_FUNCTION);
			if(bindedFunction2 == fnb.NO_FUNCTION){
				Intent intentB2 = new Intent(this, FnSelect.class);
				startActivityForResult(intentB2, REQUEST_CODE_B2);
			}else{
				fnb.press(bindedFunction2);
			}
			break;
		
		case R.id.buttonB3:
			int bindedFunction3 = shp.getInt(FN_SAVE_B3, fnb.NO_FUNCTION);
			if(bindedFunction3 == fnb.NO_FUNCTION){
				Intent intentB3 = new Intent(this, FnSelect.class);
				startActivityForResult(intentB3, REQUEST_CODE_B3);
			}else{
				fnb.press(bindedFunction3);
			}
			break;
			
		case R.id.buttonB4:
			int bindedFunction4 = shp.getInt(FN_SAVE_B4, fnb.NO_FUNCTION);
			if(bindedFunction4 == fnb.NO_FUNCTION){
				Intent intentB4 = new Intent(this, FnSelect.class);
				startActivityForResult(intentB4, REQUEST_CODE_B4);
			}else{
				fnb.press(bindedFunction4);
			}
			break;
			
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		 if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		    {		
			 ArrayList<String> matches = intent.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	           
			  String m_Text =matches.get(0);
			
			  m_Text = shp.getString(m_Text, m_Text);
			  
		        int port = Integer.parseInt(portEt.getText().toString());
		        new Thread(new SocketThread(ipEt.getText().toString(), port, launch, m_Text)).start();
		    }
		    
		if (requestCode == REQUEST_CODE_VOICE_INPUT && resultCode == RESULT_OK)
		{		
			ArrayList<String> matches = intent.getStringArrayListExtra(
				RecognizerIntent.EXTRA_RESULTS);

			String m_Text =matches.get(0);

			m_Text = shp.getString(m_Text, m_Text);

			int port = Integer.parseInt(portEt.getText().toString());
			new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, m_Text)).start();
			if(shp.getBoolean("enterOnVoiceInput",true)){
				new Thread(new SocketThread(ipEt.getText().toString(), port, keyboard, "\n")).start();
			}
			
		}
		
				   
		if (requestCode == 0) {
    		if (resultCode == RESULT_OK) {
        			String contents = intent.getStringExtra("SCAN_RESULT");
        			String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
        			String[] adressParts = contents.split(":");
        			String IP = adressParts[0];
        			String port = adressParts[1];
        			
        			ipEt.setText(IP);
        			portEt.setText(port);
        			
        			ed.putString("ip", IP);
        			ed.putString("port", port);
        			ed.commit();
    		}else{
    			
    		}
		}
		
		if(resultCode==RESULT_OK)
		switch (requestCode) {
		case REQUEST_CODE_B1:
			ed.putInt(FN_SAVE_B1, intent.getIntExtra("FnResult", fnb.NO_FUNCTION));
			ed.commit();
			b1.setText(fnb.fnMap.get(intent.getIntExtra("FnResult", fnb.NO_FUNCTION)));
			break;
			
		case REQUEST_CODE_B2:
			ed.putInt(FN_SAVE_B2, intent.getIntExtra("FnResult", fnb.NO_FUNCTION));
			ed.commit();
			b2.setText(fnb.fnMap.get(intent.getIntExtra("FnResult", fnb.NO_FUNCTION)));
			break;
			
		case REQUEST_CODE_B3:
			ed.putInt(FN_SAVE_B3, intent.getIntExtra("FnResult", fnb.NO_FUNCTION));
			ed.commit();
			b3.setText(fnb.fnMap.get(intent.getIntExtra("FnResult", fnb.NO_FUNCTION)));
			break;
			
		case REQUEST_CODE_B4:
			ed.putInt(FN_SAVE_B4, intent.getIntExtra("FnResult", fnb.NO_FUNCTION));
			ed.commit();
			b4.setText(fnb.fnMap.get(intent.getIntExtra("FnResult", fnb.NO_FUNCTION)));
			break;
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
	
	@Override
	public void onBackPressed() {
		if(up.getVisibility()==View.GONE){
			super.onBackPressed();
		} else{
			up.setVisibility(View.GONE);
			down.setVisibility(View.GONE);
			left.setVisibility(View.GONE);
			right.setVisibility(View.GONE);
			esc.setVisibility(View.GONE);
			enter.setVisibility(View.GONE);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		ed.putString("ip", ipEt.getText().toString());
		ed.putString("port", portEt.getText().toString());
		ed.commit();
		super.onDestroy();
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

		private void startVoiceRecognitionActivity(int requesrCode)
		{
		    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
				switch(requesrCode){
					case REQUEST_CODE:
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What app to launch?");
					break;
					
					case REQUEST_CODE_VOICE_INPUT:
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak...");
						break;
				}
		    
		    startActivityForResult(intent, requesrCode);
		}
	
}
