package com.fish.fishapp.contact;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DiscussArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView textViewMessage;
	private List<ChatMessage> messages = new ArrayList<ChatMessage>();
	private LinearLayout wrapper;

	@Override
	public void add(ChatMessage object) {
		messages.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.messages.size();
	}

	public ChatMessage getItem(int index) {
		return this.messages.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_discuss, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		ChatMessage message = getItem(position);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		TextView textViewAt = (TextView) wrapper.findViewById(R.id.textViewAt);
		textViewAt.setText(sdf.format(message.createdAt));
		
		textViewMessage = (TextView) row.findViewById(R.id.comment);

		textViewMessage.setText(message.content);

		textViewMessage.setBackgroundResource(message.left ? R.drawable.bocadillo_groc : R.drawable.bocadillo_verd);
		App.getInstance().log("Left is:" + message.left);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (message.left){
			params.gravity = Gravity.LEFT;	
		} else {
			params.gravity = Gravity.RIGHT;
		}
		
		textViewMessage.setLayoutParams(params);
            
		//textViewMessage.setGravity(message.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}