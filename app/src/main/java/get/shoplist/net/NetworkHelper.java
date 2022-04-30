package get.shoplist.net;

import android.content.SharedPreferences;



import java.io.IOException;

import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;





public class NetworkHelper {

    private static NetworkHelper instance;
    private Webservice webService;

    private NetworkHelper(String urlbase) {
        webService = createClient(urlbase).create(Webservice.class);
    }

    private Retrofit createClient(String urlbase) {
        OkHttpClient client = new OkHttpClient.Builder()
               // .addInterceptor(logInterceptor)
                .addInterceptor(new HeaderInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(urlbase)
         //       .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


  

    public class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
         
            Request.Builder builder = chain.request().newBuilder();
  
            builder.addHeader("user-agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Mobile Safari/537.36");
       
            return chain.proceed(builder.build());
        }
    }

    public static NetworkHelper getInstance(String urlbase) {
            instance = new NetworkHelper(urlbase);
        return instance;
    }

    /**
     * Пересоздает объект класса с новыми параметрами Retrofit
     */
    public static void resetInstance(String urlbase) {
        instance = new NetworkHelper(urlbase);
    }

    public Webservice getWebService() {
        return webService;
    }
}
