package in2chris.calhacks.io.swearjar.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class SmsUtils {

  private static final String TAG = SmsUtils.class.getName();

  public static final String SMS_ADDRESS = "address";
  public static final String SMS_PERSON = "person";
  public static final String SMS_BODY = "body";
  public static final String SMS_DATE = "date";
  public static final String SMS_ID = "_id";

  private static final String[] PROJECTION = {
      // The detail data row ID. To make a ListView work this column is required.
      ContactsContract.PhoneLookup.DISPLAY_NAME,
      ContactsContract.PhoneLookup._ID
  };

  private static String getNameFromNumber(Context context, String number) {

    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = context.getContentResolver().query(contactUri, PROJECTION, null, null, null);
    String name = null;
    if(cursor != null && cursor.moveToFirst()) {
      name =  cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
      cursor.close();
    }
    return name;
  }

  /**
   * Read the entire contents of the user's inbox.
   * @return Array of SMS messages, with a body, address, date, etc...
   */
  public static Sms[] getInboxContents(Context context) {
    // Create Draft box URI
    Log.d(TAG, "getInboxContents()");
    final Uri draftURI = Uri.parse("content://sms/inbox");

    // List required columns
    final String[] reqCols = new String[]{SMS_ID, SMS_ADDRESS, SMS_PERSON, SMS_DATE, SMS_BODY};

    final ContentResolver cr = context.getContentResolver();
    final Cursor query = cr.query(draftURI, reqCols, null, null, null);

    if (query != null && query.moveToFirst()) {
      final int messageCount = query.getCount();
      final Sms[] messageData = new Sms[messageCount];
      for (int i = 0; i < messageCount; i++) {
        messageData[i] = Sms.fromCursor(query);
        Log.d(TAG, "mAddress: " + messageData[i].getAddress());
        Log.d(TAG, "body: " + messageData[i].getBody());
        Log.d(TAG, "mDate: " + messageData[i].getDate());
        Log.d(TAG, "mName: " + getNameFromNumber(context, messageData[i].getAddress()));
        query.moveToNext();
      }
      return messageData;
    } else {
      Log.d(TAG, "inbox empty");
    }
    return null;
  }
}
