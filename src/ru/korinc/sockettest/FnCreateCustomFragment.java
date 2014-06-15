package ru.korinc.sockettest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A fragment representing a single Task detail screen. This fragment is either
 * contained in a {@link TaskListActivity} in two-pane mode (on tablets) or a
 * {@link TaskDetailActivity} on handsets.
 */
public class FnCreateCustomFragment extends ListFragment {
	
	ArrayAdapter<String> adapter ;
	List<String> fns;
	FnButton fnb;
	TextView tv;
	Button btnOk;
	ImageButton del;
	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		
	    return inflater.inflate(R.layout.fn_custom_select_lay, null);
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fnb = new FnButton();
		
		fns = new ArrayList<String>() ;
		
		for (Map.Entry<Integer, String> entry : fnb.fnMap.entrySet()) { 
			fns.add(entry.getValue());
		}
		
		
	  adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fns);
	  
	  setListAdapter(adapter);
	  
	  OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.fnCustomBtnOk:
				Intent intent = new Intent();
				intent.putExtra("FnResult",fnb.FN_CUSTOM);
				intent.putExtra("FnResultArgs", tv.getText().toString());
				getActivity().setResult(getActivity().RESULT_OK, intent);
				getActivity().finish();
				break;

			case R.id.fnCustomDelElement:
				String s = tv.getText().toString();
				
				String[] args = s.split(" \\+ ");
				
				int argsLength= args.length;
				
				if(argsLength>1){
					String afterDel=args[0];
					for(int  i=1; i<argsLength-1;i++){
						afterDel += " + "+args[i];
					}
					tv.setText(afterDel);
				}else{
					
					tv.setText("Select something");
				}
				
				break;
			}
			
			
			
		}
	  };
	  
	  btnOk = (Button) getActivity().findViewById(R.id.fnCustomBtnOk);
	  btnOk.setOnClickListener(ocl);
	  del = (ImageButton) getActivity().findViewById(R.id.fnCustomDelElement);
	  del.setOnClickListener(ocl);
	  tv=(TextView) getActivity().findViewById(R.id.fnCustomSelectTV);
		 tv.setText("Select something");
	  
	}
	
	 public void onListItemClick(ListView l, View v, int position, long id) {
		  super.onListItemClick(l, v, position, id);
				
		  String fnKey="";
		  
		  fnKey =  fns.get(position);
		  	if(tv.getText().toString().equals("Select something")){
		  		tv.setText(fnKey);
		  	}else{
		  		tv.setText(tv.getText().toString()+" + "+fnKey);
		  	}
			
				
				
			
		  }


}