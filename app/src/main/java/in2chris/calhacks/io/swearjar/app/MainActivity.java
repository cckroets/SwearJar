package in2chris.calhacks.io.swearjar.app;

import android.content.Intent;
import android.os.Bundle;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import in2chris.calhacks.io.swearjar.R;
import in2chris.calhacks.io.swearjar.sms.SmsUtils;
import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Parse.initialize(this, "kaXaeXxfwlzNOIdAUwPcFATkGnboNZHT82EkmGe2", "dIpBtvspJ8d6zSMtlLNjgzyI0UdCxegpec4ZNlr2");
    ParseFacebookUtils.initialize("661005694006952");

    setContentView(R.layout.activity_main);
    //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    //transaction.add(R.id.fragment_container, new MainFragment());
    SmsUtils.getInboxContents(getApplicationContext());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
  }


}
