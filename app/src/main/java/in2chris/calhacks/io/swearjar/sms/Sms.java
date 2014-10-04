package in2chris.calhacks.io.swearjar.sms;

import android.database.Cursor;


/**
* Created by ckroetsc on 10/4/14.
*/
public final class Sms {

  private String mAddress;
  private String mBody;
  private Integer mPerson;
  private long mDate;

  Sms() { }

  public static Sms fromCursor(Cursor query) {
    final Sms sms = new Sms();
    sms.mAddress = query.getString(query.getColumnIndex(SmsReceiver.SMS_ADDRESS));
    sms.mBody = query.getString(query.getColumnIndex(SmsReceiver.SMS_BODY));
    sms.mDate = Long.parseLong(query.getString(query.getColumnIndex(SmsReceiver.SMS_DATE)));
    sms.mPerson = parsePerson(query.getString(query.getColumnIndex(SmsReceiver.SMS_PERSON)));
    return sms;
  }

  private static Integer parsePerson(String person) {
    return (person == null || person.equals("null")) ? null : Integer.parseInt(person);
  }

  // Could be phone number or email address
  public String getAddress() {
    return mAddress;
  }

  // Date received
  public long getDate() {
    return mDate;
  }

  // Cross-listing for an Android contact
  public Integer getPerson() {
    return mPerson;
  }

  // Body of the message
  public String getBody() {
    return mBody;
  }
}
