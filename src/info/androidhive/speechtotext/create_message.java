package info.androidhive.speechtotext;


import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class create_message extends Activity implements OnClickListener,OnInitListener{
	// Wiget GUI
		private TextToSpeech tts;
		private final int REQ_CODE_SPEECH_INPUT = 100;
		int counter = 0;
		String number = "";
		EditText txtNumber, txtMessage;
		Button btnSend;
		String name = "";
		String user;

		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sms);
			
			user = getIntent().getStringExtra("user");
			 tts = new TextToSpeech(this, this);
	        tts.setSpeechRate((float) 0.9);
	        tts.setPitch(1);

			// Init GUI
			txtNumber = (EditText) findViewById(R.id.txtNumber);
			txtMessage = (EditText) findViewById(R.id.txtMesssage);
			btnSend = (Button) findViewById(R.id.btnSMS);

			// Attached Click Listener
			btnSend.setOnClickListener(this);
		}
		
		
		public boolean checkContact(String checkName){
			
			
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					"UPPER(DISPLAY_NAME) = '" +checkName.toUpperCase()+ "'", null, null);
			if (cursor.moveToFirst()) {
			    String contactId =
			        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			    //
			    //  Get all phone numbers.
			    //
			    Cursor phones = cr.query(Phone.CONTENT_URI, null,
			        Phone.CONTACT_ID + " = " + contactId, null, null);
			    while (phones.moveToNext()) {
			        number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
			        int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
			        switch (type) {
			            case Phone.TYPE_HOME:
			            	Toast.makeText(getApplicationContext(), "TYPE_HOME", Toast.LENGTH_LONG);
			                break;
			            case Phone.TYPE_MOBILE:
			            	Toast.makeText(getApplicationContext(), "TYPE_MOBILE", Toast.LENGTH_LONG);
			                break;
			            case Phone.TYPE_WORK:
			            	Toast.makeText(getApplicationContext(), "TYPE_WORK", Toast.LENGTH_LONG);
			                break;
			        }
			    }
			}
			if(number.equalsIgnoreCase("")){
				try {
					tts.speak("contact not found", TextToSpeech.QUEUE_FLUSH, null);	
					Thread.sleep(3000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
			else{
				try {
					Thread.sleep(2000);
							
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;	
				}
			
		}
		
		private void sendSMS(String phoneNumber, String message) {
		    // Intent Filter Tags for SMS SEND and DELIVER
		    String SENT = "SMS_SENT";
		    String DELIVERED = "SMS_DELIVERED";
		// STEP-1___
		    // SEND PendingIntent
		    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
		            SENT), 0);

		    // DELIVER PendingIntent
		    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(DELIVERED), 0);
		// STEP-2___
		    // SEND BroadcastReceiver
		    BroadcastReceiver sendSMS = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context arg0, Intent arg1) {
		            switch (getResultCode()) {
		            case Activity.RESULT_OK:				
						try {
							if(user.equals("normal")){
								Toast.makeText(getApplicationContext(), "Message sent successfully, returning to dashboard", Toast.LENGTH_LONG).show();
							}
							else{
								tts.speak("Message sent successfully, returning to dashboard", TextToSpeech.QUEUE_FLUSH, null);
							}
							Thread.sleep(4000);
							create_message.this.finish();
							Intent i=new Intent(create_message.this,NormalMessaging.class);
							i.putExtra("user", user);
			    			create_message.this.startActivity(i);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                break;
		            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:			
						try {
							if(user.equals("normal")){
								Toast.makeText(getApplicationContext(), "Sending failed, not enough load  balance, returning to dashboard", Toast.LENGTH_LONG).show();
							}
							else{
								tts.speak("Sending failed, not enough load  balance, returning to dashboard", TextToSpeech.QUEUE_FLUSH, null);
							}
							
							Thread.sleep(5000);
							create_message.this.finish();
							Intent i=new Intent(create_message.this,NormalMessaging.class);
							i.putExtra("user", user);
			    			create_message.this.startActivity(i);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
		                break;
		            case SmsManager.RESULT_ERROR_NO_SERVICE:
		                Toast.makeText(getBaseContext(), "No service",
		                        Toast.LENGTH_SHORT).show();
		                break;
		            case SmsManager.RESULT_ERROR_NULL_PDU:
		                Toast.makeText(getBaseContext(), "Null PDU",
		                        Toast.LENGTH_SHORT).show();
		                break;
		            case SmsManager.RESULT_ERROR_RADIO_OFF:
		                Toast.makeText(getBaseContext(), "Radio off",
		                        Toast.LENGTH_SHORT).show();
		                break;
		            }
		        }
		    };

		    // DELIVERY BroadcastReceiver
		    BroadcastReceiver deliverSMS = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					  switch (getResultCode()) {
			            case Activity.RESULT_OK:
			            	
			                break;
			            case Activity.RESULT_CANCELED:
			                Toast.makeText(getBaseContext(), "SMS not delivered",
			                        Toast.LENGTH_SHORT).show();
			                break;
			            }
					
				}
		    };
		// STEP-3___
		    // ---Notify when the SMS has been sent---
		    registerReceiver(sendSMS, new IntentFilter(SENT));

		    // ---Notify when the SMS has been delivered---
		    registerReceiver(deliverSMS, new IntentFilter(DELIVERED));

		    SmsManager sms = SmsManager.getDefault();
		    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		 }
	

		@Override
		public void onClick(View v) {

			if (v == btnSend) {
				if(this.checkContact(txtNumber.getText().toString())){
				 this.sendSMS(number, txtMessage.getText().toString());
			 }
								
			}

		}
		@Override
	    public void onInit(int status) {

	        // check if Text To Speech is initialized or not
	        if (status == TextToSpeech.SUCCESS) {
	            
	            try {
	            	
	            	 if(!user.equals("normal")){
	            		 tts.speak("please add contact after the beep.", TextToSpeech.QUEUE_FLUSH, null);
	 					Thread.sleep(6000);
	 					 promptSpeechInput();
	 				 }
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
                	
                	
	                if(counter == 0 && !result.get(0).equalsIgnoreCase("boise dashboard") && !result.get(0).equalsIgnoreCase("go home")){
	                	if(!result.get(0).equalsIgnoreCase("yes") && !result.get(0).equalsIgnoreCase("no") && name == "" ){
		                	try {
		                		txtNumber.setText(result.get(0));
		                		name = result.get(0).toString();
		                    	tts.speak("you entered "+result.get(0)+", is it  right?", TextToSpeech.QUEUE_FLUSH, null);
								Thread.sleep(3000);
								promptSpeechInput();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                	}
	                	else if(result.get(0).equalsIgnoreCase("no")){
	                		try {
	                			name = "";
	                			tts.speak("i'm sorry boss, please add contact again.", TextToSpeech.QUEUE_FLUSH, null);
								Thread.sleep(4000);
								promptSpeechInput();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
	                	}
	                	else if(result.get(0).equalsIgnoreCase("yes")){
	                		
	                		if(this.checkContact(name)){
	                			try {
			                		tts.speak("done adding contact, please add message", TextToSpeech.QUEUE_FLUSH, null);
				                	counter ++;
			          					Thread.sleep(3000);
			          					 promptSpeechInput();
			          				} catch (InterruptedException e) {
			          					// TODO Auto-generated catch block
			          					e.printStackTrace();
			          				}
	                		}
	                		else{
	                			try {
		                			name = "";
		                			tts.speak("please add contact again.", TextToSpeech.QUEUE_FLUSH, null);
									Thread.sleep(3000);
									promptSpeechInput();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                		}
	                	}
	                
	                	else{
	                		try {
	   	     				 tts.speak("Please answer! YES? or NO?", TextToSpeech.QUEUE_FLUSH, null);
	   	     					Thread.sleep(4000);
	   	     					promptSpeechInput();
	   	     				} catch (InterruptedException e) {
	   	     					
	   	     					e.printStackTrace();
	   	     				}
	                	}
	                	
	                	
	                }
	                else if(counter == 1 && !result.get(0).equalsIgnoreCase("boise dashboard") && !result.get(0).equalsIgnoreCase("go home")){
	                	txtMessage.setText(result.get(0));
	                	counter ++;
	                	try {
	                		tts.speak("done adding message, ready to send", TextToSpeech.QUEUE_FLUSH, null);
		                	counter ++;
	          					Thread.sleep(4000);
	          					 promptSpeechInput();
	          				} catch (InterruptedException e) {
	          					// TODO Auto-generated catch block
	          					e.printStackTrace();
	          				}

	    				
	                }
	                else if(counter == 3 && result.get(0).equalsIgnoreCase("yes")){
	                	this.sendSMS(number, txtMessage.getText().toString());
	                }
	                else if(result.get(0).equalsIgnoreCase("boise dashboard")){
	                	Intent i=new Intent(create_message.this,NormalMessaging.class);
	                	create_message.this.startActivity(i);
	                	this.finish();	
	                }
	                
	                else if(result.get(0).equalsIgnoreCase("go home")){
	                	Intent i=new Intent(create_message.this,MainActivity.class);
	                	create_message.this.startActivity(i);
	                	tts.speak("returning to home", TextToSpeech.QUEUE_FLUSH, null);
	                	this.finish();	
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
				else{
					try {
	     				 tts.speak("i'm waiting for your command, please speak after the beep", TextToSpeech.QUEUE_FLUSH, null);
	     					Thread.sleep(4000);
	     					promptSpeechInput();
	     				} catch (InterruptedException e) {
	     					
	     					e.printStackTrace();
	     				}
				}
			}

			}
		}


}
