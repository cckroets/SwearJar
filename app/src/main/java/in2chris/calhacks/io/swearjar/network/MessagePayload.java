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

  @JsonProperty("contact")
  Contact mContact;

  public MessagePayload(String message, String number, Contact contact) {
    mMessage = message;
    mNumber = number;
    mContact = contact;
  }
}
