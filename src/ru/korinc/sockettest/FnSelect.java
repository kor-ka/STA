package ru.korinc.sockettest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FnSelect extends Activity {
	ListView lv;
	ArrayAdapter<String> adapter ;
	List<String> fns;
	FnButton fnb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fn_select);
		
		fnb = new FnButton();
		
		fns = new ArrayList<String>() ;
		
		for (Map.Entry<Integer, String> entry : fnb.fnMap.entrySet()) { 
			fns.add(entry.getValue());
		}
		
		lv= (ListView) findViewById(R.id.listViewFnSelect);
		
	  adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, fns);
	  
	   lv.setAdapter(adapter);
	   
	   lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent();
				int fnKey=1;
				int i= 0;
				for (Map.Entry<Integer, String> entry : fnb.fnMap.entrySet()) { 
					
					if (i==position) {
						fnKey=entry.getKey();
					}
					i++;
				}
				intent.putExtra("FnResult",fnKey);
				setResult(RESULT_OK, intent);
				
				finish();
			}
		});
	  
	}

	

}
