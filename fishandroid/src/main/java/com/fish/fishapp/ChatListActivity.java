package com.fish.fishapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fish.fishapp.contact.Chat;
import com.fish.fishapp.utils.Server;

import java.util.ArrayList;

public class ChatListActivity extends Activity {

	ListView listViewChats;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listViewChats = (ListView) this.findViewById(R.id.listViewChats);
		
		ArrayList<Chat> chats = Server.Chat_list();
		if (chats==null){
			finish();
		} else {
			ChatListAdapter adapter = new ChatListAdapter(this, chats);
			listViewChats.setAdapter(adapter);
		}
		    
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ChatListAdapter extends ArrayAdapter<Chat> {
		  private final Context context;
		  private final ArrayList<Chat> chats;

		  public ChatListAdapter(Context context, ArrayList<Chat> chats) {
		    super(context, R.layout.chatlistrow, chats);
		    this.context = context;
		    this.chats = chats;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.chatlistrow, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.textViewDescription);
		    textView.setText("Este es el chat: " + chats.get(position).id);
		    
		    return rowView;
		  }
	}

}
