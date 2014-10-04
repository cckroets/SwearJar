package in2chris.calhacks.io.swearjar.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.facebook.widget.LoginButton;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import in2chris.calhacks.io.swearjar.R;
import java.math.BigDecimal;
import org.json.JSONException;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class LaunchFragment extends RoboFragment {

  @InjectView(R.id.login_button)
  LoginButton mLoginButton;

  @InjectView(R.id.phone_number)
  EditText mPhoneNumber;

  @InjectView(R.id.name)
  EditText mName;

  @InjectView(R.id.login_view)
  LinearLayout mLoginView;

  private static final int PAYPAL_REQUEST = 5;
  private static final String TAG = LaunchFragment.class.getName();

  TextWatcher mTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (validCreds()) {
        mLoginButton.setEnabled(true);
        mLoginButton.setAlpha(1.0f);
      } else {
        mLoginButton.setEnabled(false);
        mLoginButton.setAlpha(0.7f);
      }
    }
  };

  public static LaunchFragment newInstance() {
    return new LaunchFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_launch, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mPhoneNumber.addTextChangedListener(mTextWatcher);
    mName.addTextChangedListener(mTextWatcher);
  }

  private boolean validCreds() {
    final String name = mName.getText().toString();
    final String number = mPhoneNumber.getText().toString();

    return name.length() > 0
        && number.matches("[0-9]+")
        && number.length() == 10;
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
}
