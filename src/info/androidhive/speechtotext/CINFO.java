package info.androidhive.speechtotext;




import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CINFO extends Activity implements OnClickListener {

	// GUI Widget
	TextView lblNumber;
	Button btnCall;
	Button btnMsg;
	String number;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);

		lblNumber = (TextView) findViewById(R.id.lblNumber);
		btnCall = (Button) findViewById(R.id.btnCall);
		btnMsg = (Button) findViewById(R.id.btnMsg);

		String cid = getIntent().getStringExtra("cid");
		

		// Read Contact number of specific contact with help of Content Resolver
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
				new String[] { cid }, null);
		c.moveToFirst();
		number = c.getString(c
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		// Display Contact Number into Label
		lblNumber.setText(number);
		btnCall.setOnClickListener(this);
		btnMsg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == btnCall) {
			// Implicit Intent to make call
			Intent iCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ number));
			startActivity(iCall);
		}else if(v == btnMsg){
			Intent i=new Intent(CINFO.this,create_message.class);
			i.putExtra("user", "normal");
			i.putExtra("number",number);
			CINFO.this.startActivity(i);
		}

	}
}
