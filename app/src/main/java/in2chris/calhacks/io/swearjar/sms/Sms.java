package in2chris.calhacks.io.swearjar.sms;

import android.content.Context;
import android.database.Cursor;


/**
* Created by ckroetsc on 10/4/14.
*/
public final class Sms {

  private String mAddress;
  private String mContactId;
  private String mBody;
  private String mName;

  Sms() { }

  public static Sms fromCursor(Context context, Cursor query) {
    final Sms sms = new Sms();
    sms.mAddress = query.getString(query.getColumnIndex(SmsReceiver.SMS_ADDRESS));
    sms.mBody = query.getString(query.getColumnIndex(SmsReceiver.SMS_BODY));
    sms.mContactId = query.getString(query.getColumnIndex(SmsReceiver.SMS_PERSON));
    sms.mName = SmsUtils.getNameFromNumber(context, sms.mAddress);
    return sms;
  }

  private static Integer parsePerson(String person) {
    return (person == null || person.equals("null")) ? null : Integer.parseInt(person);
  }

  // Could be phone number or email address
  public String getAddress() {
    return mAddress;
  }

  // Body of the message
  public String getBody() {
    return mBody;
  }

  public String getName() {
    return mName;
  }
}
