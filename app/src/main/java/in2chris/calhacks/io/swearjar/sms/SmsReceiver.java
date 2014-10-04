package in2chris.calhacks.io.swearjar.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.google.inject.Inject;


/**
 * Created by ckroetsc on 9/15/14.
 * Listens for SMS messages and events.
 */
public class SmsReceiver extends BroadcastReceiver {

  public static final String SMS_ADDRESS = "address";
  public static final String SMS_PERSON = "person";
  public static final String SMS_BODY = "body";
  public static final String SMS_DATE = "date";
  public static final String SMS_ID = "_id";

  private static final String TAG = SmsReceiver.class.getName();

  @Inject
  Context mContext;

  @Inject
  public SmsReceiver() {

  }

  /**
   * Receive any SMS actions, e.g. text and data messages.
   * @param context
   * @param intent
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive");
    if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
      Log.d(TAG, "text message");
    } else if (Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION.equals(intent.getAction())) {
      Log.d(TAG, "data message");
    } else if (Telephony.Sms.Intents.SMS_REJECTED_ACTION.equals(intent.getAction())) {
      Log.d(TAG, "rejected");
      final int result = intent.getExtras().getInt("result");
    }
  }

  /**
   * Get the (Data) SMS messages from the intent.
   * @param intent
   * @return
   */
  private static SmsMessage[] getMessagesFromIntent(Intent intent) {
    final Bundle bundle = intent.getExtras();

    if (bundle != null) {
      final Object[] pdus = (Object[]) bundle.get("pdus");
      final SmsMessage[] msgs = new SmsMessage[pdus.length];
      for (int i = 0; i < msgs.length; i++) {
        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        Log.d(TAG, SMS_ADDRESS + ": " + msgs[i].getOriginatingAddress());
        Log.d(TAG, SMS_BODY + ": " + msgs[i].getMessageBody());
      }
      return msgs;
    }
    return null;
  }


}
