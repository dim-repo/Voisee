package info.androidhive.speechtotext;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Contact extends Activity {

	// Cursor Adapter for storing contacts data
	SimpleCursorAdapter adapter;

	// List View Widget
	ListView lvContacts;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);

		// Init ListView
		lvContacts = (ListView) findViewById(R.id.lvContacts);

		// Initialize Content Resolver object to work with content Provider
		ContentResolver cr = getContentResolver();

		// Read Contacts
		Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID,
						ContactsContract.Contacts.DISPLAY_NAME }, null, null,
				null);

		// Attached with cursor with Adapter
		adapter = new SimpleCursorAdapter(this, R.layout.contact_row, c,
				new String[] { ContactsContract.Contacts.DISPLAY_NAME },
				new int[] { R.id.lblName });

		// Display data in listview
		lvContacts.setAdapter(adapter);

		// On Click of each row of contact display next screen with contact
		// number
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long id) {

				Cursor c = (Cursor) adapter.getItemAtPosition(position);

				String cid = c.getString(c
						.getColumnIndex(ContactsContract.Contacts._ID));

				// Explicit Intent Example
				Intent iCInfo = new Intent(getApplicationContext(), CINFO.class);
				iCInfo.putExtra("cid", cid);
				startActivity(iCInfo);

			}
		});
	}

}
