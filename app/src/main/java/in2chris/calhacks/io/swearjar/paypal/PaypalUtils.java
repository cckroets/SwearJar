package in2chris.calhacks.io.swearjar.paypal;

import com.paypal.android.sdk.payments.PayPalConfiguration;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class PaypalUtils {

  private static final String CLIENT_ID = "AemoGhCDk_hcpyJ2CiVasqu_SA5ipKjaaeGiKhuk1eMDNw22QXiWvfAzTZWb";
  private static final String CLIENT_SECRET = "EAwGZxDD0dCWvl4YMp9ILz5midKWWnARQUyv4N2q4Lv-yf4OqHdb07L4-y16";

  public static PayPalConfiguration CONFIG = new PayPalConfiguration()
      .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
      .clientId(CLIENT_ID);


}
