package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class MessagePayload {

  @JsonProperty("message")
  String mMessage;

  @JsonProperty("number")
  String mNumber;

  @JsonProperty("contact_name")
  String mContactName;

  @JsonProperty("contact_number")
  String mContactNumber;

  public MessagePayload(String message, String number, String contactName, String contactNumber) {
    mMessage = message;
    mNumber = number;
    mContactName = contactName;
    mContactNumber = contactNumber;
  }
}
