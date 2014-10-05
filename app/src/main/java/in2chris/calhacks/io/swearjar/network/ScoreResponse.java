package in2chris.calhacks.io.swearjar.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by ckroetsc on 10/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ScoreResponse {

  @JsonProperty("score")
  double mScore;

  @JsonProperty("sum")
  double mSum;

  private ScoreResponse() {

  }

  public double getScore() {
    return mScore;
  }

  public double getSum() {
    return mSum;
  }
}
