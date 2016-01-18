package com.fish.fishapp.contact;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ChatActivity extends Activity {
	private DiscussArrayAdapter adapter;
	private ListView lv;
	//private LoremIpsum ipsum;
	private EditText editText1;
	//private static Random random;
	
	private String chat_id;
	private Date lastRefresh;
	
	Timer timer;
	MyTimerTask myTimerTask;
	
	@Override
	public void onPause(){
		super.onPause();
		if (timer!=null){
			timer.cancel();
		}
		
	}
	
	@Override
	public void onResume(){
		
		super.onResume();
		/*
		if (timer!=null){
			 timer.schedule(myTimerTask, 1, 3000);
		}
		*/
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		if (getIntent()!=null && getIntent().getExtras()!=null){ 
			Bundle b = getIntent().getExtras();
			chat_id = b.getString("chat_id");
		} else {
			//lost forever
			this.finish();
			return;
		}
		

		lv = (ListView) findViewById(R.id.listView1);

		

		

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					Server.Chat_sendMessage(chat_id,editText1.getText().toString());
					refreshMessages();
					editText1.setText("");
					return true;
				}
				return false;
			}
		});

		refreshMessages();
		
		//delay 1ms, repeat in 3000ms
		timer = new Timer();
		myTimerTask = new MyTimerTask();
	    timer.schedule(myTimerTask, 4000, 4000);
	}

	

	public void clickSend(View view){
		Server.Chat_sendMessage(chat_id,editText1.getText().toString());
		refreshMessages();
		editText1.setText("");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
	public void refreshMessages(){

		
		ArrayList<ChatMessage> messages = Server.Chat_listMessages(chat_id);
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
		if (messages!=null){
			for (int i = messages.size()-1; i >=0; i--) {
				App.getInstance().log("userid:" + App.getInstance().usuari.id);
				App.getInstance().log("sender:" + messages.get(i).sender);
				if (messages.get(i).sender.compareTo(App.getInstance().usuari.id)==0) {
					App.getInstance().log("left false");
					messages.get(i).left=false; //mis mensajes a la derecha
				} else {
					App.getInstance().log("left true");
					messages.get(i).left=true; //los del otro a la izquierda
				}
				
				
				adapter.add(messages.get(i));
			}
		}
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		lastRefresh=new Date();
	}
	class MyTimerTask extends TimerTask {

		  @Override
		  public void run() {
			  if (new Date().getTime() - lastRefresh.getTime() >=4000){
				   runOnUiThread(new Runnable(){
		
				    @Override
				    public void run() {
				    	refreshMessages();
				    }});
			  }
	  }
		  
	}
	
	
}