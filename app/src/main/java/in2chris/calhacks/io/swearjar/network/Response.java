package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
public final class Response {

  @JsonProperty("success")
  int mSuccess;

  private Response() {

  }

  int getSuccess() {
    return mSuccess;
  }
}
