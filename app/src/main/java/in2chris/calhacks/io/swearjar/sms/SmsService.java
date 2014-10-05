package in2chris.calhacks.io.swearjar.sms;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.app.LaunchFragment;
import in2chris.calhacks.io.swearjar.app.PreferenceManager;
import in2chris.calhacks.io.swearjar.network.ScoreResponse;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.service.RoboService;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class SmsService extends RoboService {

  private static final String TAG = "SmsSendService";

  @Inject
  WindowManager mWindowManager;

  @Inject
  SwearJarAPI mSwearJarAPI;

  @Inject
  PreferenceManager mPreferenceManager;

  String lastAddress;
  String lastMessage;

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

      final String myNumber = mPreferenceManager.getString(LaunchFragment.KEY_NUMBER, null);
      if (myNumber == null) {
        Log.d(TAG, "NUMBER IS NULL");
        return;
      }

      Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
      cur.moveToNext();

      String protocol = cur.getString(cur.getColumnIndex("protocol"));

      if (protocol == null) {
        //the message is sent out just now
        Sms sent = SmsUtils.getLastSentMessage(getApplicationContext());


        Log.d(TAG, "Messsage sent: " + sent.getBody());
        Log.d(TAG, "to: " + sent.getName());
        Log.d(TAG, "address: " + sent.getAddress());

        mSwearJarAPI.messageSent(sent.getBody(), myNumber, sent.getAddress(), new Callback<ScoreResponse>() {
                                   @Override
                                   public void success(ScoreResponse scoreResponse, Response response) {
                                     if (scoreResponse.getScore() != 0) {
                                       showPopup(scoreResponse.getScore());
                                     } else {
                                       Log.d(TAG, "clean message");
                                     }

                                   }

                                   @Override
                                   public void failure(RetrofitError error) {
                                     Log.e(TAG, "sent failure " + error.getMessage());
                                   }
                                 });



      } else {
        //the message is received just now

        Sms received = SmsUtils.getLastReceivedMessage(getApplicationContext());

        if (received.getBody().equals(lastMessage) && received.getAddress().equals(lastAddress)) {
          return;
        }

        lastMessage = received.getBody();
        lastMessage = received.getAddress();

        Log.d(TAG, "Messsage received: " + received.getBody());
        Log.d(TAG, "From: " + received.getName());
        mSwearJarAPI.messageReceived(received.getBody(), myNumber, received.getAddress(), new Callback<in2chris.calhacks.io.swearjar.network.Response>() {
          @Override
          public void success(in2chris.calhacks.io.swearjar.network.Response response, Response response2) {
            Log.d(TAG, "received success");
          }

          @Override
          public void failure(RetrofitError error) {
            Log.d(TAG, "received error : " + error.getMessage());
          }
        });
      }
    }
  }

  private void showPopup(double value) {
    final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.swear_alert, null);

    TextView textView = (TextView) view.findViewById(R.id.value);
    textView.setText("+" + value);

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                                                                             WindowManager.LayoutParams.WRAP_CONTENT,
                                                                             WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                                                                             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                                             PixelFormat.TRANSLUCENT);
    params.gravity = Gravity.TOP | Gravity.RIGHT;
    params.x = 0;
    params.y = 0;

    mWindowManager.addView(view, params);

    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mWindowManager.removeViewImmediate(view);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    view.findViewById(R.id.animating_container).startAnimation(animation);
  }


}
