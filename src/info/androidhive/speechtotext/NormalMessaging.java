package info.androidhive.speechtotext;


import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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

@SuppressLint({ "NewApi", "DefaultLocale" })
public class NormalMessaging extends Activity implements OnClickListener,OnInitListener{

	// GUI Widget
	private TextToSpeech tts;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	
	Button btnSent, btnInbox, btnDraft, btnContact,btnCreate ;
	TextView lblMsg, lblNo, lblStatus;
	ListView lvMsg;
	String user;
	String state;
	String action;
	String name;
	MatrixCursor matrixCursor = null;
	
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
	
	@SuppressWarnings("deprecation")
	public void inbox(String type){
		Uri inboxURI = Uri.parse("content://sms/" + type);

		// List required columns
		String[] reqCols = new String[] { "_id", "address", "body", "read"};
		
		// Get Content Resolver object, which will deal with Content
		// Provider
		ContentResolver cr = getContentResolver();

		Cursor inboxCursor;
		matrixCursor = new MatrixCursor(new String[]{"_id", "address", "body"});
		
		if(type.equalsIgnoreCase("inbox")){
			inboxCursor = cr.query(inboxURI, reqCols, "read = '" + 0 + "'", null, null);
			
			if(!user.equalsIgnoreCase("normal")){
				if(inboxCursor.moveToFirst()) {
					try {
						for(int count = 0 ; count < inboxCursor.getCount() ; count++){
							String address = findNameByAddress(this,
									inboxCursor.getString(inboxCursor.getColumnIndexOrThrow("address")));
							String body = inboxCursor.getString(inboxCursor.getColumnIndexOrThrow("body"));
							
							matrixCursor.addRow(new String[]{count + "" , address, body});
							
							inboxCursor.moveToNext();
							
						}
						
						tts.speak("You have " + inboxCursor.getCount() + " unread message",
								TextToSpeech.QUEUE_FLUSH, null);
						Thread.sleep(3000);
						
						tts.speak("this are the senders.", TextToSpeech.QUEUE_FLUSH, null);
						Thread.sleep(2000);
						
						matrixCursor.moveToFirst();
						
						for(int i=0; i < matrixCursor.getCount(); i++) {
							try {
								tts.speak(matrixCursor.getString(matrixCursor.getColumnIndexOrThrow("address")),
										TextToSpeech.QUEUE_FLUSH, null);
								
									Thread.sleep(3000);
								
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							matrixCursor.moveToNext();
							
						}
					    tts.speak("you can now read message",TextToSpeech.QUEUE_FLUSH, null);
					    Thread.sleep(3000);
						adapter = new SimpleCursorAdapter(this, R.layout.row, matrixCursor,
								new String[] { "body", "address" }, new int[] {
										R.id.lblMsg, R.id.lblNumber});
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					try {
						tts.speak("You have no unread message", TextToSpeech.QUEUE_FLUSH, null);
						Thread.sleep(3000);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}else{
				inboxCursor.moveToFirst();
				for(int count = 0 ; count < inboxCursor.getCount() ; count++){
					String address = findNameByAddress(this,
							inboxCursor.getString(inboxCursor.getColumnIndexOrThrow("address")));
					String body = inboxCursor.getString(inboxCursor.getColumnIndexOrThrow("body"));
					
					matrixCursor.addRow(new String[]{count + "" , address, body});
					
					inboxCursor.moveToNext();
					
				}
				
				adapter = new SimpleCursorAdapter(this, R.layout.row, matrixCursor,
						new String[] { "body", "address" }, new int[] {
								R.id.lblMsg, R.id.lblNumber});
			}	
			
		}else{
			inboxCursor = cr.query(inboxURI, reqCols, null, null, null);
			adapter = new SimpleCursorAdapter(this, R.layout.row, inboxCursor,
					new String[] { "body", "address" }, new int[] {
							R.id.lblMsg, R.id.lblNumber});
		}	
		
		
		lvMsg.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {

		if (v == btnInbox) {
			Toast.makeText(getApplicationContext(),"now loading inbox",Toast.LENGTH_LONG).show();
			this.inbox("inbox");
		}
		if (v == btnSent) {
			Toast.makeText(getApplicationContext(),"now loading sent box",Toast.LENGTH_LONG).show();
			this.inbox("sent");
		}
		if (v == btnDraft) {
			Toast.makeText(getApplicationContext(),"now loading draft",Toast.LENGTH_LONG).show();
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

	
	public String findNameByAddress(Context ct,String addr){
         Uri myPerson = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(addr));

         String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

            Cursor cursor = ct.getContentResolver().query(myPerson,
                    projection, null, null, null);

            if (cursor.moveToFirst()) {

                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                Log.e("","Found contact name");

                cursor.close();

                return name;
                
            }

            cursor.close();
            Log.e("","Not Found contact name");

            return addr;
            
    }
	
	
	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {
			 try {
				 if(!user.equals("normal")){
					 state = "";
					 tts.speak("We are now at dashboard", TextToSpeech.QUEUE_FLUSH, null);
					Thread.sleep(4000);
					 promptSpeechInput();
				 }
				 
			} catch (InterruptedException e) {
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
	                }else if(state.equalsIgnoreCase("inbox")){
	                	
	                	if(result.get(0).toString().startsWith("read ")){
							action = result.get(0).toString().substring(0,3);
							name = result.get(0).toString().substring(5);
							try {
		   	     				 tts.speak("Do you want to read "+name+"'s message?", TextToSpeech.QUEUE_FLUSH, null);
		   	     					Thread.sleep(2000);
		   	     					 promptSpeechInput();
		   	     				} catch (InterruptedException e) {
		   	     					// TODO Auto-generated catch block
		   	     					e.printStackTrace();
		   	     				}
							
						}
	                	else if(result.get(0).toString().startsWith("reply")){
							
							try {
		   	     				 if(name != ""){
		   	     					tts.speak("preparing S M S", TextToSpeech.QUEUE_FLUSH, null);
			   	     				Thread.sleep(2000);
		   	     					Intent i=new Intent(NormalMessaging.this,create_message.class);
									i.putExtra("user", "blind");
									i.putExtra("number",name);
									NormalMessaging.this.startActivity(i);
		   	     				 }
		   	     				 else{
		   	     					tts.speak("you have n destination for your reply.please read message before reply"
		   	     							, TextToSpeech.QUEUE_FLUSH, null);
			   	     				Thread.sleep(4000);
			   	     				promptSpeechInput();
		   	     				 }
		   	     				} catch (InterruptedException e) {
		   	     					// TODO Auto-generated catch block
		   	     					e.printStackTrace();
		   	     				}
						}
	                	else if(name == "" && action != null){
	                		try {
	                			name = result.get(0).toString();
		   	     				 tts.speak("you mean "+name+". is it right? ", TextToSpeech.QUEUE_FLUSH, null);
		   	     				Thread.sleep(2000);
		   	     				 promptSpeechInput();
		   	     				} catch (InterruptedException e) {
		   	     					// TODO Auto-generated catch block
		   	     					e.printStackTrace();
		   	     				}
	                	}
						else if(result.get(0).equalsIgnoreCase("yes")){
							matrixCursor.moveToFirst();
	
							int count = 1, flag = 0;
							try {	
							while(count <= matrixCursor.getCount()){
							 
								if(name.equalsIgnoreCase(matrixCursor.getString(matrixCursor.getColumnIndexOrThrow("address")))){
									
									     flag = 1;
				   	     				 tts.speak("now reading "+name+"'s message.", TextToSpeech.QUEUE_FLUSH, null);
				   	     				 Thread.sleep(3000);
				   	     				 String trimmed = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow("body")).trim();
				   	     				 int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
				   	     				 tts.speak(trimmed, TextToSpeech.QUEUE_FLUSH, null);
				   	     				 Thread.sleep(words * 1000);
				   	     				 tts.speak("Done reading "+name+"'s message. you can now reply.", TextToSpeech.QUEUE_FLUSH, null);
				   	     				 Thread.sleep(3000);
				   	     				 promptSpeechInput();
				   	     				
									
									break;
									
								}
									count++;
									matrixCursor.moveToNext();
									
								}
								if(flag == 0){
									 tts.speak("sender not found. please state the name again", TextToSpeech.QUEUE_FLUSH, null);
									 name = "";
			   	     				 Thread.sleep(3000);
			   	     				 promptSpeechInput();
								}
							} catch (InterruptedException e) {
	   	     					// TODO Auto-generated catch block
	   	     					e.printStackTrace();
	   	     				
	   	     				}
						}
						else if(result.get(0).equalsIgnoreCase("no")){
							try {
		   	     				 tts.speak("i'm sorry please state the name again", TextToSpeech.QUEUE_FLUSH, null);
		   	     				 name = "";
		   	     				 Thread.sleep(3000);
		   	     				 promptSpeechInput();
		   	     				} catch (InterruptedException e) {
		   	     					// TODO Auto-generated catch block
		   	     					e.printStackTrace();
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
	
	public void createSms(String num, String type){
		Intent i=new Intent(NormalMessaging.this,create_message.class);
		i.putExtra("user", type);
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

