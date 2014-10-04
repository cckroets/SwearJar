package in2chris.calhacks.io.swearjar.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class PreferenceManager {

  private static final String SHARED_PREFERENCES_NAME = "io.calhacks.in2chris";

  SharedPreferences mSharedPreferences;

  @Inject
  public PreferenceManager(Application application) {
    mSharedPreferences = application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
  }

  public String getString(String key, String defaultValue) {
    return mSharedPreferences.getString(key, defaultValue);
  }

  public void putString(String key, String value) {
    mSharedPreferences.edit().putString(key, value).apply();
  }

}
