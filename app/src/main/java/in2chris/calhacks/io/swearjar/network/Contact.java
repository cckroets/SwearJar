package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class Contact {

  @JsonProperty("name")
  String mName;

  @JsonProperty("number")
  String mNumber;

  public Contact(String name, String number) {
    mName = name;
    mNumber = number;
  }

  public String getName() {
    return mName;
  }

  public String getNumber() {
    return mNumber;
  }
}
