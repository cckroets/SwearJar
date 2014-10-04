package in2chris.calhacks.io.swearjar.sms;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import roboguice.service.RoboService;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class SmsService extends RoboService {

  private static final String TAG = "SmsSendService";

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)  {
    Log.d(TAG, "onStartCommand()");
    ContentObserver observer = new SmsObserver(new Handler());
    getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, observer);

    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public class SmsObserver extends ContentObserver {

    public SmsObserver(Handler handler) {
      super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
      super.onChange(selfChange);
      Log.d(TAG, "CHANGED");

      Uri uriSMSURI = Uri.parse("content://sms/");

      Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
      cur.moveToNext();

      String protocol = cur.getString(cur.getColumnIndex("protocol"));

      if (protocol == null) {
        //the message is sent out just now
        Sms[] sent = SmsUtils.getSentMessages(getApplicationContext());

        Log.d(TAG, "Messsage sent: " + sent[0].getBody());
        Log.d(TAG, "From: " + sent[0].getName());

      } else {
        //the message is received just now

        Sms[] sent = SmsUtils.getInboxContents(getApplicationContext());

        Log.d(TAG, "Messsage received: " + sent[0].getBody());
        Log.d(TAG, "From: " + sent[0].getName());
      }
    }
  }
}
