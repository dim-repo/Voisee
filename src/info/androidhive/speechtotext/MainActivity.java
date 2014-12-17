package info.androidhive.speechtotext;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnInitListener {

    private TextToSpeech tts;
	private TextView txtSpeechInput;
	private ImageButton btnSpeak;
	private Button btn_normal;
	private final int REQ_CODE_SPEECH_INPUT = 100;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);
        tts.setSpeechRate((float) 0.9);
        tts.setPitch(1);


		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		btn_normal = (Button) findViewById(R.id.btnNormal);
		

		// hide the action bar
		getActionBar().hide();

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});
		
		btn_normal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(MainActivity.this,NormalMessaging.class);
				MainActivity.this.startActivity(i);
			}
		});

	}

    @Override
    public void onInit(int status) {

        // check if Text To Speech is initialized or not
        if (status == TextToSpeech.SUCCESS) {
            
            try {
            	tts.speak("Voisee activated!", TextToSpeech.QUEUE_FLUSH, null);
				Thread.sleep(5000);
				 promptSpeechInput();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
        } else {
            Toast.makeText(this, "Text To Speech is not initialized", Toast.LENGTH_LONG).show();
        }
    }

	/**
	 * Showing google speech input dialog
	 * */
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
                if(result.get(0).equals("contacts")){
                	Intent i=new Intent(MainActivity.this,Contact.class);
    				MainActivity.this.startActivity(i);
                    tts.speak("we are now at contacts", TextToSpeech.QUEUE_FLUSH, null);
                }
                else if(result.get(0).equals("message")){
    				Intent i=new Intent(MainActivity.this,NormalMessaging.class);
    				MainActivity.this.startActivity(i);
    				tts.speak("loading message", TextToSpeech.QUEUE_FLUSH, null);

    				
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

				txtSpeechInput.setText(result.get(0));
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
