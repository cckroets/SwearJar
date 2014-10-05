package in2chris.calhacks.io.swearjar.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.inject.Inject;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class LaunchFragment extends RoboFragment {

  public static final String KEY_NAME = "name";
  public static final String KEY_FB_ID = "fb_id";
  public static final String KEY_NUMBER = "number";

  @InjectView(R.id.login_button)
  LoginButton mLoginButton;

  @InjectView(R.id.phone_number)
  EditText mPhoneNumber;

  @InjectView(R.id.title)
  TextView mTitle;

  @Inject
  PreferenceManager mPreferenceManager;

  @Inject
  SwearJarAPI mSwearJarAPI;

  UiLifecycleHelper mUiHelper;

  boolean mRegistered;

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


  private FacebookDialog.Callback mDialogCallback = new FacebookDialog.Callback() {
    @Override
    public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
      Log.d("HelloFacebook", "Success!");
    }

    @Override
    public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
      Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
    }
  };

  public static LaunchFragment newInstance() {
    return new LaunchFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final String number = mPreferenceManager.getString(KEY_NUMBER, null);
    mRegistered = (number != null);
    mUiHelper = new UiLifecycleHelper(getActivity(), new Session.StatusCallback() {
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
      }
    });
    mUiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_launch, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mLoginButton.setFragment(this);
    mLoginButton.setReadPermissions("public_profile");
    mPhoneNumber.addTextChangedListener(mTextWatcher);
    if (mRegistered) {
      if (Session.getActiveSession() == null || !Session.getActiveSession().isOpened()) {
        mTitle.setText("Log in");
        mPhoneNumber.setVisibility(View.GONE);
      } else {
        onSessionStateChange(Session.getActiveSession(), Session.getActiveSession().getState(), null);
      }
    } else {
      mTitle.setText("Sign up");
      mPhoneNumber.setVisibility(View.VISIBLE);
    }

  }

  private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    if (state.isOpened()) {
      Log.i("HelloFacebook", "Logged in...");
      // make request to get facebook user info
      final String fbId = mPreferenceManager.getString(KEY_FB_ID, null);
      final String fbName = mPreferenceManager.getString(KEY_NAME, null);
      String number = mPreferenceManager.getString(KEY_NUMBER, null);
      if (number == null) {
        number = mPhoneNumber.getText().toString();
        mPreferenceManager.putString(KEY_NUMBER, number);
      }
      final String argNumber = number;
      if (fbId == null || fbName == null) {
        Request.newMeRequest(session, new Request.GraphUserCallback() {
          @Override
          public void onCompleted(GraphUser user, Response response) {
            onMeLoaded(user, argNumber);
          }
        }).executeAsync();
      } else {
        goToMainFragment(fbId, fbName, number);
      }
    } else if (state.isClosed()) {
      Log.i("HelloFacebook", "Logged out...");
    }
  }

  private void onMeLoaded(GraphUser me, String number) {
    Log.i("fb", "fb user: " + me.toString());
    final String fbId = me.getId();
    final String fbName = me.getName();

    mSwearJarAPI.register(number, fbName, fbId, new Callback<in2chris.calhacks.io.swearjar.network.Response>() {
      @Override
      public void success(in2chris.calhacks.io.swearjar.network.Response response, retrofit.client.Response response2) {
        Log.d("HelloFacebook", "Success!");
        mPreferenceManager.putString(KEY_NAME, fbName);
        mPreferenceManager.putString(KEY_FB_ID, fbId);
        mPreferenceManager.putString(KEY_NUMBER, mPhoneNumber.getText().toString());
        goToMainFragment(fbId, fbName, mPhoneNumber.getText().toString());
      }

      @Override
      public void failure(RetrofitError error) {
        Log.d("HelloFacebook", "Failure! " + error.getMessage());
        Toast.makeText(getActivity(), "Could not log in", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void goToMainFragment(String fbId, String fbName, String number) {
    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
    transaction.replace(R.id.fragment_container, MainFragment.newInstance(fbId, fbName, number));
    transaction.commit();
  }


  @Override
  public void onResume() {
    super.onResume();
    mUiHelper.onResume();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mUiHelper.onSaveInstanceState(outState);
  }

  @Override
  public void onPause() {
    super.onPause();
    //mUiHelper.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mUiHelper.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUiHelper.onDestroy();
  }

  private boolean validCreds() {
    final String number = mPhoneNumber.getText().toString();

    return number.matches("[0-9]+")
        && number.length() == 10;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mUiHelper.onActivityResult(requestCode, resultCode, data, mDialogCallback);
  }


}
