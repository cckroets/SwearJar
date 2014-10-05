package in2chris.calhacks.io.swearjar.network;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;


/**
 * Created by ckroetsc on 10/4/14.
 */
public interface SwearJarAPI {

  @FormUrlEncoded
  @POST("/message/send")
  void messageSent(
      @Field("message") String message,
      @Field("number") String number,
      @Field("contact_number") String contactNumber,
      Callback<ScoreResponse> callback
  );

  @FormUrlEncoded
  @POST("/message/receive")
  void messageReceived(
      @Field("message") String message,
      @Field("number") String number,
      @Field("contact_number") String contactNumber,
      Callback<Response> callback
  );

  @GET("/score/{number}")
  void getScore(
      @Path("number") String number,
      Callback<ScoreResponse> callback
  );

  @FormUrlEncoded
  @POST("/register/{number}")
  void register(
      @Path("number") String number,
      @Field("name") String name,
      @Field("facebook_id") String fbId,
      Callback<Response> callback
  );

}
