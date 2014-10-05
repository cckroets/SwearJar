package in2chris.calhacks.io.swearjar.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.google.inject.Inject;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.network.ScoreResponse;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import java.math.BigDecimal;
import java.text.NumberFormat;
import org.json.JSONException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class MainFragment extends RoboFragment {

  public static final double MAX_JAR_DOLLARS = 10; // $10.00

  private static final int PAYPAL_REQUEST = 5;
  private static final String TAG = LaunchFragment.class.getName();

  @InjectView(R.id.welcome)
  TextView mWelcomeText;

  @InjectView(R.id.picture)
  ProfilePictureView mProfilePictureView;

  @InjectView(R.id.jar)
  ImageView mJarImage;

  @InjectView(R.id.jar_total)
  TextView mJarSum;

  @InjectView(R.id.jar_max)
  TextView mJarMax;

  @InjectView(R.id.jar_full_text)
  TextView mJarFullText;

  @Inject
  PreferenceManager mPreferenceManager;

  @Inject
  SwearJarAPI mSwearJarAPI;

  String mNumber;
  String mId;
  String mName;

  public static MainFragment newInstance(String fbId, String fbName, String number) {
    final Bundle args = new Bundle();
    final MainFragment fragment = new MainFragment();
    args.putString(LaunchFragment.KEY_NUMBER, number);
    args.putString(LaunchFragment.KEY_FB_ID, fbId);
    args.putString(LaunchFragment.KEY_NAME, fbName);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Bundle args = getArguments();
    mNumber = args.getString(LaunchFragment.KEY_NUMBER);
    mId = args.getString(LaunchFragment.KEY_FB_ID);
    mName = args.getString(LaunchFragment.KEY_NAME);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final NumberFormat format = NumberFormat.getCurrencyInstance();


    if (mId != null) {
      mProfilePictureView.setProfileId(mId);
    }

    if (mName != null) {
      mWelcomeText.setText("Welcome,\n" + mName);
    } else {
      mWelcomeText.setText("Welcome!");
    }

    mJarMax.setText(format.format(MAX_JAR_DOLLARS));
    if (mNumber != null) {
      mSwearJarAPI.getScore(mNumber, new Callback<ScoreResponse>() {
        @Override
        public void success(ScoreResponse scoreResponse, Response response) {
          final String dollars = format.format(scoreResponse.getSum());
          mJarSum.setText(dollars);
          if (scoreResponse.getSum() >= MAX_JAR_DOLLARS) {
            mJarFullText.setVisibility(View.VISIBLE);
          }
        }

        @Override
        public void failure(RetrofitError error) {
          Log.e(TAG, error.getMessage());
          mJarSum.setText("$0.00");
        }
      });
    }
    mJarImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        android.support.v4.app.DialogFragment dialogFragment = new PunishmentDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "punishments");
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult() : " + requestCode);
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
      Log.d(TAG, "resultCode = " + requestCode);
    }
  }

  public void onBuyPressed(View pressed) {

    // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
    // Change PAYMENT_INTENT_SALE to
    //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
    //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
    //     later via calls from your server.

    PayPalPayment payment = new PayPalPayment(new BigDecimal("1.00"), "USD", "F*ck", PayPalPayment.PAYMENT_INTENT_SALE);
    Intent intent = new Intent(getActivity(), PaymentActivity.class);
    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
    startActivityForResult(intent, PAYPAL_REQUEST);
  }
}
