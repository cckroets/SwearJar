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

  public static String getNameFromNumber(Context context, String number) {

    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = context.getContentResolver().query(contactUri, PROJECTION, null, null, null);
    String name = null;
    if(cursor != null && cursor.moveToFirst()) {
      name =  cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
      cursor.close();
    }
    return name;
  }

  private static Sms[] getContents(Context context, String box) {
    // Create Draft box URI
    Log.d(TAG, "getInboxContents()");
    final Uri draftURI = Uri.parse(box);

    // List required columns
    final String[] reqCols = new String[]{SMS_ID, SMS_ADDRESS, SMS_BODY, SMS_PERSON};

    final ContentResolver cr = context.getContentResolver();
    final Cursor query = cr.query(draftURI, reqCols, null, null, null);

    if (query != null && query.moveToFirst()) {
      final int messageCount = query.getCount();
      final Sms[] messageData = new Sms[messageCount];
      for (int i = 0; i < messageCount; i++) {
        messageData[i] = Sms.fromCursor(context, query);
        query.moveToNext();
      }
      return messageData;
    } else {
      Log.d(TAG, box + " empty");
    }
    return null;
  }

  private static Sms getSingleSms(Context context, String box) {
    // Create Draft box URI
    Log.d(TAG, "get single from " + box);
    final Uri draftURI = Uri.parse(box);

    // List required columns
    final String[] reqCols = new String[]{SMS_ID, SMS_ADDRESS, SMS_BODY, SMS_PERSON};

    final ContentResolver cr = context.getContentResolver();
    final Cursor query = cr.query(draftURI, reqCols, null, null, null);

    if (query != null && query.moveToFirst()) {
      Sms sms = Sms.fromCursor(context, query);
      return sms;
    } else {
      return null;
    }
  }

  public static Sms getLastSentMessage(Context context) {
    return getSingleSms(context, "content://sms/sent");
  }

  public static Sms getLastReceivedMessage(Context context) {
    return getSingleSms(context, "content://sms/inbox");
  }

  public static Sms[] getSentMessages(Context context) {
    return getContents(context, "content://sms/sent");
  }

  public static Sms[] getInboxContents(Context context) {
    return getContents(context, "content://sms/inbox");
  }
}
