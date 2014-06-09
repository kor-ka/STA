package ru.korinc.sockettest;

import java.util.HashMap;

import android.R.integer;
import android.content.Context;
import android.widget.Toast;

public class FnButton {
	
	Context ctx;	
	ST st;
	public final static int NO_FUNCTION=0;
	public final static int FN_SCAN=1;
	public final static int FN_ENTER=2;
	public final static int FN_BKSPC=3;
	public final static int FN_ESC=4;
	public final static int FN_CTRL_Z=5;
	public final static int FN_CTRL_Y=6;
	public final static int FN_CLICK=7;
	public final static int FN_R_CLICK=8;
	public static HashMap<Integer, String> fnMap;
	
	public  FnButton(ST st) {
		this.ctx=st.getBaseContext();
		this.st = st;
		fnMap = new HashMap<Integer, String>();
		fnMap.put(NO_FUNCTION, "No function");
		fnMap.put(FN_SCAN, "Scan");
		fnMap.put(FN_ENTER, "Enter");
		fnMap.put(FN_BKSPC, "Backspace");
		fnMap.put(FN_ESC, "Escape");
		fnMap.put(FN_CTRL_Z, "Ctr+Z");
		fnMap.put(FN_CTRL_Y, "Ctrl+Y");
		fnMap.put(FN_CLICK, "Left click");
		fnMap.put(FN_R_CLICK, "Right click");
	}
	
	public  FnButton() {
		
		fnMap = new HashMap<Integer, String>();
		fnMap.put(NO_FUNCTION, "No function");
		fnMap.put(FN_SCAN, "Scan");
		fnMap.put(FN_ENTER, "Enter");
		fnMap.put(FN_BKSPC, "Backspace");
		fnMap.put(FN_ESC, "Escape");
		fnMap.put(FN_CTRL_Z, "Ctr+Z");
		fnMap.put(FN_CTRL_Y, "Ctrl+Y");
		fnMap.put(FN_CLICK, "Left click");
		fnMap.put(FN_R_CLICK, "Right click");
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
			}
	 }
	
 }
}
