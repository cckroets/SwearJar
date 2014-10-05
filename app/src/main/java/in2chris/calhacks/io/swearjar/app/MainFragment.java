package in2chris.calhacks.io.swearjar.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.widget.ProfilePictureView;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.event.BusSingleton;
import in2chris.calhacks.io.swearjar.event.PunishmentTakenEvent;
import in2chris.calhacks.io.swearjar.network.ScoreResponse;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import java.text.NumberFormat;
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
  final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

  private static final String TAG = MainFragment.class.getName();

  public static double money;

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
    BusSingleton.get().register(this);
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
    if (mId != null) {
      mProfilePictureView.setProfileId(mId);
    }

    if (mName != null) {
      mWelcomeText.setText("Welcome,\n" + mName);
    } else {
      mWelcomeText.setText("Welcome!");
    }

    mJarMax.setText(CURRENCY_FORMAT.format(MAX_JAR_DOLLARS));
    if (mNumber != null) {
      mSwearJarAPI.getScore(mNumber, new Callback<ScoreResponse>() {
        @Override
        public void success(ScoreResponse scoreResponse, Response response) {
          bindJar(scoreResponse.getScore());
        }

        @Override
        public void failure(RetrofitError error) {
          Log.e(TAG, error.getMessage());
          mJarSum.setText("$0.00");
        }
      });
    }
  }

  @Override
  public void onDestroy() {
    BusSingleton.get().unregister(this);
    super.onDestroy();
  }

  @Subscribe
  public void onPunishmentTaken(PunishmentTakenEvent event) {
    bindJar(0);
    Toast.makeText(getActivity(), "Jar emptied!", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "number = " + mNumber);
    mSwearJarAPI.getScore(mNumber, new Callback<ScoreResponse>() {
      @Override
      public void success(ScoreResponse scoreResponse, Response response) {
        Log.d(TAG, "sum = " + scoreResponse.getScore());
        bindJar(scoreResponse.getScore());
      }

      @Override
      public void failure(RetrofitError error) {
        Log.e(TAG, error.getMessage());
      }
    });
  }

  void bindJar(double score) {
    int resId;
    final String dollars = CURRENCY_FORMAT.format(score);
    money = score;
    if (score >= MAX_JAR_DOLLARS) {
      resId = R.drawable.jar_100_full;
      mJarFullText.setVisibility(View.VISIBLE);
      mJarImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          android.support.v4.app.DialogFragment dialogFragment = new PunishmentDialogFragment();
          dialogFragment.show(getActivity().getSupportFragmentManager(), "punishments");
        }
      });
    } else if (score >= 0.8 * MAX_JAR_DOLLARS) {
      resId = R.drawable.jar_80_full;
    } else if (score >= 0.6 * MAX_JAR_DOLLARS) {
      resId = R.drawable.jar_60_full;
    } else if (score >= 0.4 * MAX_JAR_DOLLARS) {
      resId = R.drawable.jar_40_full;
    } else if (score > 0) {
      resId = R.drawable.jar_20_full;
    } else {
      resId = R.drawable.jar_0_full;
    }
    mJarImage.setImageResource(resId);
    mJarSum.setText(dollars);

    if (score < MAX_JAR_DOLLARS) {
      mJarImage.setOnClickListener(null);
      mJarFullText.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }


}
