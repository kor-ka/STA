package ru.korinc.sockettest;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
		for(int i=0; i<fnb.fnMap.size(); i++ ){
			fns.add(fnb.fnMap.get(i));
		}
		
		lv= (ListView) findViewById(R.id.listViewFnSelect);
		
	  adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, fns);
	  
	   lv.setAdapter(adapter);
	   
	   lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("FnResult", position);
				setResult(RESULT_OK, intent);
				
				finish();
			}
		});
	  
	}

	

}
