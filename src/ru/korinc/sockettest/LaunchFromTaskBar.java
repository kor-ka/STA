package ru.korinc.sockettest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.print.attribute.standard.Finishings;

import ru.korinc.sockettest.ST.SocketThread;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LaunchFromTaskBar extends Activity {
	ListView lv;
	ArrayAdapter<String> adapter ;
	List<String> map;
	FnButton fbn;
	final int GET_TASKBAR_APPS = 1;
	final int OPEN_TASKBAR_APP = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch_from_task_bar);
		new Thread(new SocketThread(getIntent().getStringExtra("ip"), getIntent().getIntExtra("port",4444), GET_TASKBAR_APPS)).start();
		
		map = new ArrayList<String>();
		map.add("...");
		lv= (ListView) findViewById(R.id.launchFromTaskBarLv);
		// ������� �������
	  adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, map);
	  
	    // ����������� ������� ������
	    lv.setAdapter(adapter);
	    
	  //����������� ������
		  lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					new Thread(new SocketThread(getIntent().getStringExtra("ip"), getIntent().getIntExtra("port",4444), OPEN_TASKBAR_APP, map.get(position))).start();
					finish();
				}
				
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch_from_task_bar, menu);
		return true;
	}
	
	class SocketThread implements Runnable {

		String ip;
		int port;
		Socket socket;
		int mode;
		String appToLaunch;

		public SocketThread(String ip, int port, int mode) {
			this.ip = ip;
			this.port = port;
			this.mode = mode;
		}

		public SocketThread(String ip, int port, int mode, String appToLaunch) {
			this.ip = ip;
			this.port = port;
			this.mode = mode;
			this.appToLaunch = appToLaunch;
		}
		

		@Override
		public void run() {
			try {

				InetAddress ipAddress = InetAddress.getByName(ip);
				socket = new Socket();
				socket.connect(new InetSocketAddress(ipAddress, port), 10000);

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
						
						switch (mode) {
						case GET_TASKBAR_APPS:
							out.writeUTF("launchFromTaskBarList");
							
							

							final String line = in.readUTF();

							
							runOnUiThread(new Runnable() {
								public void run() {
									String line2 = line.substring(0,line.length()-1);
									
									map = Arrays.asList(line2.split(":"));
									 adapter = new ArrayAdapter<String>(getBaseContext(),
										        android.R.layout.simple_list_item_1, map);
										  lv.setAdapter(adapter);
									//adapter.notifyDataSetInvalidated();
								}
							});	
							
							break;

						case OPEN_TASKBAR_APP:
							out.writeUTF("commandLine::" + "start \"\" " +"\"%userprofile%/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar/"+appToLaunch+".lnk\"");
							
							break;
						}
						
						out.flush();
						
						socket.close();
						// socket = null;
						// Toast.makeText(getBaseContext(), line + "",
						// Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				}
			}
		}

	}

}
