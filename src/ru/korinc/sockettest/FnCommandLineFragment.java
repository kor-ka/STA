package ru.korinc.sockettest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A fragment representing a single Task detail screen. This fragment is either
 * contained in a {@link TaskListActivity} in two-pane mode (on tablets) or a
 * {@link TaskDetailActivity} on handsets.
 */
public class FnCommandLineFragment extends ListFragment {
	
	ArrayAdapter<String> adapter ;
	List<String> fns;
	FnButton fnb;
	EditText et;
	Button btnOk;
	SharedPreferences shp;
	Editor ed;
	Set<String> commands;
	
	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		
	    return inflater.inflate(R.layout.fn_custom_command_lay, null);
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fns = new ArrayList<String>() ;
		shp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		ed = shp.edit();
		
		commands  = shp.getStringSet("FnCommands", new HashSet<String>());
		if (commands.isEmpty()){
			commands.add("start chrome");
			commands.add("start notepad");
			commands.add("shutdown -i");			
			ed.putStringSet("FnCommands", commands);
			ed.commit();
		}
		
		
		if(commands != null){
			for (String key:commands) {
				fns.add(key);
			}
		}
				
	  adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fns);
	  
	  setListAdapter(adapter);
	  
	  OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.fnCommandLineBtnOk:
				commands.add(et.getText().toString());
				ed.putStringSet("FnCommands", commands);
				ed.commit();
				Intent intent = new Intent();
				intent.putExtra("FnResult",fnb.FN_COMMAND_LINE);
				intent.putExtra("FnResultArgs", et.getText().toString());
				getActivity().setResult(getActivity().RESULT_OK, intent);
				getActivity().finish();
				break;

			
			}
			
			
			
		}
	  };
	  
	  btnOk = (Button) getActivity().findViewById(R.id.fnCommandLineBtnOk);
	  btnOk.setOnClickListener(ocl);
	 
	  et=(EditText) getActivity().findViewById(R.id.fn_custom_command_et);
	
	  getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
         
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v,	int position, long id) {
			commands.remove(fns.get(position));
			ed.putStringSet("FnCommands", commands);
			ed.commit();
			fns.remove(position);			
			adapter.notifyDataSetChanged();
			return false;
		}
      });
	  
	}
	
	 public void onListItemClick(ListView l, View v, int position, long id) {
		  super.onListItemClick(l, v, position, id);
				
		  String command="";
		  
		  command =  fns.get(position);
		  	
		  		et.setText(command);
		  	
			
				
				
			
		  }

	
}