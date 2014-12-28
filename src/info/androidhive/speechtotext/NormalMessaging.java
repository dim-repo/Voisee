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
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
	
	Button btnSent, btnInbox, btnDraft, btnContact,btnCreate ;
	TextView lblMsg, lblNo, lblStatus;
	ListView lvMsg;
	String user;
	String state;
	String[] action;
	String number;
	String msg;


	// Cursor Adapter
	SimpleCursorAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.to_normal);
		
		
			user = getIntent().getStringExtra("user");
	
		

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
		
		btnCreate = (Button) findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(this);
		
		btnContact = (Button) findViewById(R.id.btnContact);
		btnContact.setOnClickListener(this);

		lvMsg = (ListView) findViewById(R.id.lvMsg);
		

	}
	
	public void inbox(String type){
		Uri inboxURI = Uri.parse("content://sms/"+type);

		// List required columns
		String[] reqCols = new String[] { "_id", "address", "body","read","date" };
		
		
		// Get Content Resolver object, which will deal with Content
		// Provider
		ContentResolver cr = getContentResolver();

		Cursor c;
		if(type.equalsIgnoreCase("inbox")){
				 c = cr.query(inboxURI, reqCols,
					    "read = '" +0+ "'", null, null);
				 if(c.moveToFirst()) {
					 try {
						 tts.speak("You have "+c.getCount()+", Unread message", TextToSpeech.QUEUE_FLUSH, null);
							Thread.sleep(3000);
						 tts.speak("this are the senders my friend", TextToSpeech.QUEUE_FLUSH, null);
						 	Thread.sleep(2000);
						for(int i=0; i < c.getCount(); i++) {
							try {
								tts.speak(c.getString(c.getColumnIndexOrThrow("address")).toString()+
					        			   ". date."+c.getString(c.getColumnIndexOrThrow("date")), TextToSpeech.QUEUE_FLUSH, null);
									Thread.sleep(10000);
									promptSpeechInput();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			               c.moveToNext();
						}
						
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			     }
				 else{
					 
					 try {
						tts.speak("You have no unread message", TextToSpeech.QUEUE_FLUSH, null);
						Thread.sleep(3000);
						promptSpeechInput();
					 } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					 
				 }
		 }
		 else{
			 c = cr.query(inboxURI, reqCols, null, null, null);
		 }
		
		
		// Attached Cursor with adapter and display in listview
		adapter = new SimpleCursorAdapter(this, R.layout.row, c,
				new String[] { "body", "address" }, new int[] {
						R.id.lblMsg, R.id.lblNumber});
		
		lvMsg.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {

		if (v == btnInbox) {
			tts.speak("now loading inbox", TextToSpeech.QUEUE_FLUSH, null);
			this.inbox("inbox");
		}
		if (v == btnSent) {

			tts.speak("now loading sentbox", TextToSpeech.QUEUE_FLUSH, null);
			this.inbox("sent");
		}
		if (v == btnDraft) {
			tts.speak("now loading draft", TextToSpeech.QUEUE_FLUSH, null);
        	this.inbox("draft");
		}
		if (v == btnContact) {	
			Intent i=new Intent(NormalMessaging.this,Contact.class);
			NormalMessaging.this.startActivity(i);
			this.finish();
		}
		if (v == btnCreate) {	
			createSms("none","normal");
			this.finish();
		}

	}

	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {
			 
			 try {
				 if(!user.equals("normal")){
					 number = "";
					 state = "main";
					 action[0] = "none";
					 tts.speak("We are now at dashboard", TextToSpeech.QUEUE_FLUSH, null);
					Thread.sleep(4000);
					 promptSpeechInput();
				 }
			} 
			 catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			 }
	     }
		 
		 else {
	            Toast.makeText(getApplicationContext(), "Text To Speech is not initialized", Toast.LENGTH_LONG).show();
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
				
                if(result.get(0).equalsIgnoreCase("you")){	
                		
                		try {
                			state = "inbox";
                			tts.speak("now loading inbox", TextToSpeech.QUEUE_FLUSH, null);
							Thread.sleep(2000);
							this.inbox("inbox");    	        
	        	            promptSpeechInput();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    

                }
                else if(result.get(0).equalsIgnoreCase("me")) {
        			try {
        				state = "sentbox";
        				tts.speak("now loading sentbox", TextToSpeech.QUEUE_FLUSH, null);
                    	this.inbox("sent");
        	            Thread.sleep(2000);
        	            promptSpeechInput();
        	        } catch (InterruptedException ie) {
        	            // ... Error message...
        	        }
        		}
                else if(result.get(0).equalsIgnoreCase("save")) {
                	tts.speak("now loading draft", TextToSpeech.QUEUE_FLUSH, null);
                	this.inbox("draft");
        			try {
        				state = "save";
        	            Thread.sleep(2000);
        	            promptSpeechInput();
        	        } catch (InterruptedException ie) {
        	            // ... Error message...
        	        }
        		}
                else if(result.get(0).equalsIgnoreCase("number")){
                	Intent i=new Intent(NormalMessaging.this,Contact.class);
        			NormalMessaging.this.startActivity(i);
        			this.finish();
        			tts.speak("we are now at contacts", TextToSpeech.QUEUE_FLUSH, null);
        			try {
        	            Thread.sleep(2000);
        	            promptSpeechInput();
        	        } catch (InterruptedException ie) {
        	            // ... Error message...
        	        }
    				
                }
                else if(result.get(0).equalsIgnoreCase("new message")){
                	createSms("none","blind");
        			tts.speak("preparing S M S", TextToSpeech.QUEUE_FLUSH, null);
        			this.finish();
    				
                }
                else if(result.get(0).equalsIgnoreCase("go home")){
                	Intent i=new Intent(NormalMessaging.this,MainActivity.class);
        			NormalMessaging.this.startActivity(i);
        			tts.speak("returning to home", TextToSpeech.QUEUE_FLUSH, null);
                	this.finish();	
                }
                else if(state.equalsIgnoreCase("inbox")){
                	
                	if(action[0].equalsIgnoreCase("none") && result.get(0).split(" ",2)[0].equalsIgnoreCase("read")){
                		try {
                			action = result.get(0).split(" ",2);
                    		tts.speak("Sender "+action[1]+", is it right?", TextToSpeech.QUEUE_FLUSH, null);
							Thread.sleep(2000);
							promptSpeechInput();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
                	 }
                	else if(action[0].equalsIgnoreCase("read")){
                		if(result.get(0).equalsIgnoreCase("reply") && !number.equals("")){
                			createSms(number,"blind");
                			tts.speak("preparing S M S", TextToSpeech.QUEUE_FLUSH, null);
                			this.finish();
                		}
                		else if(action[1].equalsIgnoreCase("")){
                			try {
                    			action[1] = result.get(0);
                        		tts.speak("Sender "+action[1]+", is it right?", TextToSpeech.QUEUE_FLUSH, null);
    							Thread.sleep(2000);
    							promptSpeechInput();
    						} catch (InterruptedException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                		}
                		else if(result.get(0).equalsIgnoreCase("yes")){
                			if(this.checkName(action[1])){
	                		try {
			                		tts.speak("now reading "+action[1]+"'s message.", TextToSpeech.QUEUE_FLUSH, null);
			          					Thread.sleep(2000);
			          				tts.speak(msg+". Voisee done reading message. ", TextToSpeech.QUEUE_FLUSH, null);
			          					Thread.sleep(2000);	
			          					 promptSpeechInput();
			          				} catch (InterruptedException e) {
			          					// TODO Auto-generated catch block
			          					e.printStackTrace();
			          				}
	                		}
	                		else{
	                			try {	
		                			tts.speak(action[1]+" not found in inbox please state again the name.", TextToSpeech.QUEUE_FLUSH, null);
									action[1] = "";
		                			Thread.sleep(3000);
									promptSpeechInput();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                		}
                		}
                		else if(result.get(0).equalsIgnoreCase("no")){
                			try {	
	                			tts.speak("sorry my friend, please state again the name of the sender", TextToSpeech.QUEUE_FLUSH, null);
								action[1] = "";
	                			Thread.sleep(3000);
								promptSpeechInput();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                		}
                		else{
                			try {
   	   	     				 tts.speak("Please answer! YES or NO", TextToSpeech.QUEUE_FLUSH, null);
   	   	     					Thread.sleep(4000);
   	   	     					promptSpeechInput();
   	   	     				} catch (InterruptedException e) {
   	   	     					
   	   	     					e.printStackTrace();
   	   	     				}
                		}
                	}
                	else{
                		unknown();
                	}

                }
                else{
                	unknown();
                }

			}
			
			else{
				try {
     				 tts.speak("i'm waiting for your command, please speak after the beep", TextToSpeech.QUEUE_FLUSH, null);
     					Thread.sleep(4000);
     					 promptSpeechInput();
     				} catch (InterruptedException e) {
     					// TODO Auto-generated catch block
     					e.printStackTrace();
     				}
			}
		}
		

		}
	}
	
	public boolean checkName(String name){
		Uri inboxURI = Uri.parse("content://sms/inbox");

		String[] reqCols = new String[] { "_id", "address", "body","read","date" };
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				"UPPER(DISPLAY_NAME) = '" +name.toUpperCase()+ "'", null, null);
		if(c.moveToFirst()){
			msg = c.getString(c.getColumnIndexOrThrow("body")).toString();
			number = c.getString(c.getColumnIndexOrThrow("address")).toString();
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public void unknown(){
		try {
				 tts.speak("unknown command", TextToSpeech.QUEUE_FLUSH, null);
					Thread.sleep(2000);
					 promptSpeechInput();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public void createSms(String num, String user){
		Intent i=new Intent(NormalMessaging.this,create_message.class);
		i.putExtra("user", user);
		i.putExtra("number" , num);
		NormalMessaging.this.startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

