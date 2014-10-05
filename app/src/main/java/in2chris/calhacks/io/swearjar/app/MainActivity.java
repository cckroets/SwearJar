package in2chris.calhacks.io.swearjar.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.paypal.android.sdk.payments.PayPalService;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.paypal.PaypalUtils;
import in2chris.calhacks.io.swearjar.sms.SmsService;
import java.util.Arrays;
import java.util.List;
import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity implements PunishmentDialogFragment.OnPunishmentSelectedListener {

  private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
  private boolean mPendingPublishReauthorization = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startServices();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
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
  protected void onDestroy() {
    stopService(new Intent(this, PayPalService.class));
    super.onDestroy();
  }

  @Override
  public void onShamingOptionSelected() {
    Log.d("MainActivity", "shaming selected");
    Session session = Session.getActiveSession();

    if (session != null) {
      FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
          .setLink("https://challengepost.com")
          .setCaption("Swear Jar caught me swearing to my friends!\n\n Find out more at:")
          .build();
      shareDialog.present();
    }

  }

  @Override
  public void onDonationOptionSelected() {
    Log.d("MainActivity", "donation selected");
  }
}
