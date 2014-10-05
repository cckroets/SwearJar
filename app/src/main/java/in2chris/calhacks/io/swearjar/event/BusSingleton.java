package in2chris.calhacks.io.swearjar.event;

import com.squareup.otto.Bus;


/**
 * Created by ckroetsc on 10/5/14.
 */
public class BusSingleton {

  private static Bus sInstance;

  public static Bus get() {
    if (sInstance == null) {
      sInstance = new Bus();
    }
    return sInstance;
  }


}
