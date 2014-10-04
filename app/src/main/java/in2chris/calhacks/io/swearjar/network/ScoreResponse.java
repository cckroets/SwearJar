package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreResponse {

  @JsonProperty("score")
  int mScore;

  @JsonProperty("sum")
  int mSum;

  private ScoreResponse() {

  }

  public int getScore() {
    return mScore;
  }

  public int getSum() {
    return mSum;
  }
}
