package ru.korinc.sockettest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class VoiceFnMappingList extends Activity{
	ListView lv;
	SharedPreferences shp;
	Editor ed;
	
	EditText keyEt;
	Button b1;
	Set<String> keys;
	List<String> map;
	ArrayAdapter<String> adapter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_fn_mapping_list);		
		
		shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ed = shp.edit();
		
		keyEt = (EditText) findViewById(R.id.editKey);
		b1 = (Button) findViewById(R.id.voice_fn_button);
		
		
		keys  = shp.getStringSet("VoiceFnMap", new HashSet<String>());
		if (keys.isEmpty()){
			keys.add("хром");
			ed.putString("VoiceFnArg:"+"хром", "satart \"chrome\"");
			ed.putInt("VoiceFn:"+"хром", FnButton.FN_COMMAND_LINE);
			ed.putStringSet("VoiceFnMap", keys);
			ed.commit();
		}
		
		map = new ArrayList<String>() ;
		if(keys != null){
			for (String key:keys) {
				String descrOfCammand = shp.getString("VoiceFnArg:"+key, "null");
				if(descrOfCammand.isEmpty()){
					descrOfCammand=FnButton.fnMap.get(shp.getInt("VoiceFn:"+key, 0));
				}
				map.add(key+" = "+descrOfCammand);
			}
		}
		
		OnClickListener ocl = new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				Intent intent = new Intent(getBaseContext(), FnBind.class);
					startActivityForResult(intent, 0);
							
			}
		};
		
		
		
		b1.setOnClickListener(ocl);				
	
		
		lv= (ListView) findViewById(R.id.listViewMap);
		// пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
	  adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, map);
	  
	    // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
	    lv.setAdapter(adapter);
	    
	  //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
		  lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					String[] keyAndValue = map.get(position).split(" = ");
					String keyToRemove = keyAndValue[0];
					if(map.get(position).contains(" = ")){
						ed.putString("VoiceFnArg:"+keyToRemove, null);
						ed.putInt("VoiceFn:"+keyToRemove, 0);
						keys.remove(keyToRemove);
						ed.putStringSet("VoiceFnMap", keys);
						ed.commit();
						map.remove(position);
						adapter.notifyDataSetChanged();
						
						keyEt.setText(keyAndValue[0]);
						b1.setText("No function");
					}
					
				}
			});
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==0&&resultCode==RESULT_OK){
			ed.putString("VoiceFnArg:"+keyEt.getText().toString(),  data.getStringExtra("FnResultArgs"));
			ed.putInt("VoiceFn:"+keyEt.getText().toString(), data.getIntExtra("FnResult", FnButton.NO_FUNCTION));
			keys  = shp.getStringSet("VoiceFnMap", new HashSet<String>());
			keys.add(keyEt.getText().toString());
			ed.putStringSet("VoiceFnMap", keys);
			ed.commit();
			
			String descrOfCammand = data.getStringExtra("FnResultArgs");
			if(descrOfCammand.isEmpty()){
				descrOfCammand=FnButton.fnMap.get(data.getIntExtra("FnResult", FnButton.NO_FUNCTION));
			}
			map.add(keyEt.getText().toString()+" = "+descrOfCammand);	
			
			adapter.notifyDataSetChanged();
			
			keyEt.setText("");
			b1.setText("No function");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	

}
