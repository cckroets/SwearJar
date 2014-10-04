package in2chris.calhacks.io.swearjar.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.paypal.android.sdk.payments.PayPalService;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.paypal.PaypalUtils;
import in2chris.calhacks.io.swearjar.sms.SmsService;
import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {

  UiLifecycleHelper mUiHelper;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
      @Override
      public void call(Session session, SessionState state, Exception exception) {

      }
    });
    mUiHelper.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startServices();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, LaunchFragment.newInstance());
    transaction.commit();
  }


  private void startServices() {
    Intent paypalIntent = new Intent(this, PayPalService.class);
    paypalIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalUtils.CONFIG);
    startService(paypalIntent);
    startService(new Intent(this, SmsService.class));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mUiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onDestroy() {
    mUiHelper.onDestroy();
    stopService(new Intent(this, PayPalService.class));
    super.onDestroy();
  }
}
