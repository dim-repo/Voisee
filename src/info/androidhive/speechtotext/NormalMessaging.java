package info.androidhive.speechtotext;


import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.RecognizerIntent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NormalMessaging extends Activity implements OnClickListener,OnInitListener{
	
	

	// GUI Widget
	private TextToSpeech tts;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	
	Button btnSent, btnInbox, btnDraft, btnContact ;
	TextView lblMsg, lblNo;
	ListView lvMsg;

	// Cursor Adapter
	SimpleCursorAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.to_normal);

		// Init GUI Widget
		 tts = new TextToSpeech(this, this);
        tts.setSpeechRate((float) 0.9);
        tts.setPitch(1);
		
        
		btnInbox = (Button) findViewById(R.id.btnInbox);
		btnInbox.setOnClickListener(this);

		btnSent = (Button) findViewById(R.id.btnSentBox);
		btnSent.setOnClickListener(this);

		btnDraft = (Button) findViewById(R.id.btnDraft);
		btnDraft.setOnClickListener(this);
		
		btnContact = (Button) findViewById(R.id.btnContact);
		btnContact.setOnClickListener(this);

		lvMsg = (ListView) findViewById(R.id.lvMsg);
		

	}

	@Override
	public void onClick(View v) {

		if (v == btnInbox) {

			// Create Inbox box URI
			Uri inboxURI = Uri.parse("content://sms/inbox");

			// List required columns
			String[] reqCols = new String[] { "_id", "address", "body" };

			// Get Content Resolver object, which will deal with Content
			// Provider
			ContentResolver cr = getContentResolver();

			// Fetch Inbox SMS Message from Built-in Content Provider
			Cursor c = cr.query(inboxURI, reqCols, null, null, null);

			// Attached Cursor with adapter and display in listview
			adapter = new SimpleCursorAdapter(this, R.layout.row, c,
					new String[] { "body", "address" }, new int[] {
							R.id.lblMsg, R.id.lblNumber });
			lvMsg.setAdapter(adapter);

		}

		if (v == btnSent) {

			// Create Sent box URI
			Toast.makeText(getApplicationContext(), "Text To Speech is not initialized", Toast.LENGTH_LONG).show();
			Uri sentURI = Uri.parse("content://sms/sent");

			// List required columns
			String[] reqCols = new String[] { "_id", "address", "body" };

			// Get Content Resolver object, which will deal with Content
			// Provider
			ContentResolver cr = getContentResolver();

			// Fetch Sent SMS Message from Built-in Content Provider
			Cursor c = cr.query(sentURI, reqCols, null, null, null);

			// Attached Cursor with adapter and display in listview
			adapter = new SimpleCursorAdapter(this, R.layout.row, c,
					new String[] { "body", "address" }, new int[] {
							R.id.lblMsg, R.id.lblNumber });
			lvMsg.setAdapter(adapter);

		}

		if (v == btnDraft) {
			// Create Draft box URI
			Uri draftURI = Uri.parse("content://sms/draft");

			// List required columns
			String[] reqCols = new String[] { "_id", "address", "body" };

			// Get Content Resolver object, which will deal with Content
			// Provider
			ContentResolver cr = getContentResolver();

			// Fetch Sent SMS Message from Built-in Content Provider
			Cursor c = cr.query(draftURI, reqCols, null, null, null);

			// Attached Cursor with adapter and display in listview
			adapter = new SimpleCursorAdapter(this, R.layout.row, c,
					new String[] { "body", "address" }, new int[] {
							R.id.lblMsg, R.id.lblNumber });
			lvMsg.setAdapter(adapter);

		}
		if (v == btnContact) {	
			Intent i=new Intent(NormalMessaging.this,Contact.class);
			NormalMessaging.this.startActivity(i);
		}

	}

	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {

			 try {
				 
					Thread.sleep(2000);
					 promptSpeechInput();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	            Toast.makeText(this, "Text To Speech is not initialized", Toast.LENGTH_LONG).show();
	        }
		
	}
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result.get(0).equals("box")){
                	Toast.makeText(getApplicationContext(), result.get(0).toString(), Toast.LENGTH_LONG).show();
                	Uri inboxURI = Uri.parse("content://sms/inbox");

        			// List required columns
        			String[] reqCols = new String[] { "_id", "address", "body" };

        			// Get Content Resolver object, which will deal with Content
        			// Provider
        			ContentResolver cr = getContentResolver();

        			// Fetch Inbox SMS Message from Built-in Content Provider
        			Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        			// Attached Cursor with adapter and display in listview
        			adapter = new SimpleCursorAdapter(this, R.layout.row, c,
        					new String[] { "body", "address" }, new int[] {
        							R.id.lblMsg, R.id.lblNumber });
        			lvMsg.setAdapter(adapter);
        			tts.speak("we are now at inbox", TextToSpeech.QUEUE_FLUSH, null);
                }
                else if(result.get(0).equals("contact")){
                	Intent i=new Intent(NormalMessaging.this,Contact.class);
        			NormalMessaging.this.startActivity(i);
        			tts.speak("we are now at contacts", TextToSpeech.QUEUE_FLUSH, null);
    				
                }
                else{
                	try {
       				 tts.speak("unknown command", TextToSpeech.QUEUE_FLUSH, null);
       					Thread.sleep(2000);
       					 promptSpeechInput();
       				} catch (InterruptedException e) {
       					// TODO Auto-generated catch block
       					e.printStackTrace();
       				}

                }

			}
			break;
		}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

