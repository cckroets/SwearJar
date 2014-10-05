package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class Registration {

  @JsonProperty("number")
  String number;

  @JsonProperty("name")
  String name;

  @JsonProperty("facebook_id")
  String id;

  public Registration(String a, String b, String c) {
    number = a;
    name = b;
    id = c;
  }
}
