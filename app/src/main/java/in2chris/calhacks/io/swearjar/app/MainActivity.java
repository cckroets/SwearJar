package in2chris.calhacks.io.swearjar.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.google.inject.Inject;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.otto.Bus;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.event.BusSingleton;
import in2chris.calhacks.io.swearjar.event.PunishmentTakenEvent;
import in2chris.calhacks.io.swearjar.network.Response;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import in2chris.calhacks.io.swearjar.paypal.PaypalUtils;
import in2chris.calhacks.io.swearjar.sms.SmsService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import retrofit.Callback;
import retrofit.RetrofitError;
import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity implements PunishmentDialogFragment.OnPunishmentSelectedListener {

  private static final int PAYPAL_REQUEST = 5;
  private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
  private boolean mPendingPublishReauthorization = false;

  @Inject
  SwearJarAPI mSwearJarAPI;

  @Inject
  PreferenceManager mPreferenceManager;

  Bus mBus;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mBus = BusSingleton.get();

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
      FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this).setLink("https://challengepost.com")
                                                                              .setCaption(
                                                                                  "Swear Jar caught me swearing to my friends!\n\n Find out more at:")
                                                                              .build();
      shareDialog.present();
    }
  }

  public void onBuyPressed(double money) {

    // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
    // Change PAYMENT_INTENT_SALE to
    //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
    //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
    //     later via calls from your server.

    PayPalPayment payment =
        new PayPalPayment(new BigDecimal(money), "USD", "Swear Jar Charity", PayPalPayment.PAYMENT_INTENT_SALE);
    Intent intent = new Intent(this, PaymentActivity.class);
    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
    startActivityForResult(intent, PAYPAL_REQUEST);
  }

  @Override
  public void onDonationOptionSelected() {
    Log.d("MainActivity", "donation selected");
    onBuyPressed(MainFragment.money);
  }

  @Override
  protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
    Log.d("MainActivity", "onActivityResult() : " + requestCode);
    if (requestCode == PAYPAL_REQUEST && resultCode == Activity.RESULT_OK) {
      PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
      if (confirm != null) {
        try {
          Log.i("paymentExample", confirm.toJSONObject().toString(4));

          // TODO: send 'confirm' to your server for verification.
          // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
          // for more details.

        } catch (JSONException e) {
          Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
        }
      }
    } else if (resultCode == Activity.RESULT_CANCELED) {
      Log.i("paymentExample", "The user canceled.");
    } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
      Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
    } else {
      Log.d("MainActivity", "resultCode = " + requestCode);
    }
    final String number = mPreferenceManager.getString(LaunchFragment.KEY_NUMBER, null);
    if (number != null) {
      mSwearJarAPI.empty(number, new Callback<Response>() {
        @Override
        public void success(Response response, retrofit.client.Response response2) {
          Log.d("MainActivity", "empty success");
          mBus.post(new PunishmentTakenEvent(requestCode == PAYPAL_REQUEST ?
                                                 PunishmentTakenEvent.Type.DONATION
                                                 : PunishmentTakenEvent.Type.FB_SHAMING));
        }

        @Override
        public void failure(RetrofitError error) {
          Log.e("MainActivity", error.getMessage());
        }
      });
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}
