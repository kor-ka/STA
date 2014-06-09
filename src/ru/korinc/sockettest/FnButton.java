package ru.korinc.sockettest;

import java.util.HashMap;
import java.util.LinkedHashMap;

import ru.korinc.sockettest.ST.SocketThread;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class FnButton {
	
	Context ctx;	
	ST st;
	
	//int values MUST be ordered! //should fix it by iterating map in FnSelect!!
	public final static int NO_FUNCTION=0;
	public final static int FN_SCAN=1;	
	public final static int FN_ENTER=2;
	public final static int FN_BKSPC=3; 
	public final static int FN_ESC=4;
	public final static int FN_CTRL_Z=5; 
	public final static int FN_CTRL_Y=6; 
	public final static int FN_CLICK=7; 
	public final static int FN_R_CLICK=8; 
	public final static int FN_LAUNCH_APP=9;
	public final static int FN_ARROWS=10;
	public final static int FN_F1=11;
	public final static int FN_F2=12;
	public final static int FN_F3=13;
	public final static int FN_F4=14;
	public final static int FN_F5=15;
	public final static int FN_F6=16;
	public final static int FN_F7=17;
	public final static int FN_F8=18;
	public final static int FN_F9=19;
	public final static int FN_F10=20;
	public final static int FN_F11=21;
	public final static int FN_F12=22;
	public final static int FN_CONTEXT_MENU=23;
	public static HashMap<Integer, String> fnMap;
	
	public  FnButton(ST st) {
		this.ctx=st.getBaseContext();
		this.st = st;
		initiateMap();
	}
	
	public  FnButton() {
		
		initiateMap();
	}
	
	 public void initiateMap(){
		 fnMap = new LinkedHashMap<Integer, String>();
			fnMap.put(NO_FUNCTION, "No function");
			fnMap.put(FN_SCAN, "Scan");
			fnMap.put(FN_LAUNCH_APP, "Launch app");
			fnMap.put(FN_ARROWS, "Arrows");
			fnMap.put(FN_CLICK, "Left click");
			fnMap.put(FN_R_CLICK, "Right click");
			fnMap.put(FN_ENTER, "Enter");
			fnMap.put(FN_BKSPC, "Backspace");
			fnMap.put(FN_ESC, "Escape");
			fnMap.put(FN_CTRL_Z, "Ctr+Z");
			fnMap.put(FN_CTRL_Y, "Ctrl+Y");
			fnMap.put(FN_CONTEXT_MENU, "Context menu");
			fnMap.put(FN_F1, "F1");
			fnMap.put(FN_F2, "F2");
			fnMap.put(FN_F3, "F3");
			fnMap.put(FN_F4, "F4");
			fnMap.put(FN_F5, "F5");
			fnMap.put(FN_F6, "F6");
			fnMap.put(FN_F7, "F7");
			fnMap.put(FN_F8, "F8");
			fnMap.put(FN_F9, "F9");
			fnMap.put(FN_F10, "F10");
			fnMap.put(FN_F11, "F11");
			fnMap.put(FN_F12, "F12");
	 }
	
 public void press(int function){
	 
	 int port = Integer.parseInt(st.portEt.getText().toString());
	 if(st!=null){
		 switch (function) {
			case NO_FUNCTION:
				 Toast.makeText(ctx, "no function", Toast.LENGTH_SHORT).show();
				break;

			case FN_CLICK:
											
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.click, 0, 0)).start();
				break;
			
			case FN_R_CLICK:
											
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.rclick, 0, 0)).start();
				break;	
				
			case FN_SCAN:
				 st.scan.performClick();
				break;
				
				
			case FN_LAUNCH_APP:
				 st.startVoiceRecognitionActivity(st.REQUEST_CODE_LAUNCH_APP);
				break;
				
				
			case FN_ARROWS:
				switch (st.up.getVisibility()) {
				case View.VISIBLE:
					st.up.setVisibility(View.GONE);
					st.down.setVisibility(View.GONE);
					st.left.setVisibility(View.GONE);
					st.right.setVisibility(View.GONE);
					st.esc.setVisibility(View.GONE);
					st.enter.setVisibility(View.GONE);
					break;

				case View.GONE:
					st.up.setVisibility(View.VISIBLE);
					st.down.setVisibility(View.VISIBLE);
					st.left.setVisibility(View.VISIBLE);
					st.right.setVisibility(View.VISIBLE);
					st.esc.setVisibility(View.VISIBLE);
					st.enter.setVisibility(View.VISIBLE);
					break;
				}
				break;
				
			case FN_BKSPC:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "bksps")).start();
				break;
				
			case FN_ENTER:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "enter")).start();
				break;
			
			case FN_ESC:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "esc")).start();
				break;
				
			case FN_CTRL_Z:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "ctrlz")).start();
				break;
				
			case FN_CTRL_Y:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "ctrly")).start();
				break;
				
			case FN_CONTEXT_MENU:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "contextMenu")).start();
				break;
				
			case FN_F1:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f1")).start();
				break;
			case FN_F2:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f2")).start();
				break;
			case FN_F3:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f3")).start();
				break;
			case FN_F4:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f4")).start();
				break;
			case FN_F5:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f5")).start();
				break;
			case FN_F6:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f6")).start();
				break;
			case FN_F7:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f7")).start();
				break;
			case FN_F8:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f8")).start();
				break;
			case FN_F9:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f9")).start();
				break;
			case FN_F10:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f10")).start();
				break;
			case FN_F11:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f11")).start();
				break;
			case FN_F12:
				new Thread(st.new SocketThread(st.ipEt.getText().toString(), port, st.keyboard, "f12")).start();
				break;
					
			}
	 }
	
 }
 

}
