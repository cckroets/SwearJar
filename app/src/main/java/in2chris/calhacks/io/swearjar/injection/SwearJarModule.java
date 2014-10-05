package in2chris.calhacks.io.swearjar.injection;

import com.google.inject.AbstractModule;
import in2chris.calhacks.io.swearjar.network.SwearJarAPI;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class SwearJarModule extends AbstractModule {

  private static final String END_POINT = "http://whispering-river-1524.herokuapp.com";

  @Override
  protected void configure() {
    bind(SwearJarAPI.class).toInstance(createAPI());
  }

  private SwearJarAPI createAPI() {
    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(END_POINT)
        .setConverter(new JacksonConverter())
        .build();

    return restAdapter.create(SwearJarAPI.class);
  }
}
