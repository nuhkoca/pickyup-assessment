package com.upday.shutterdemo.pickyup.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.upday.shutterdemo.pickyup.BuildConfig;
import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.api.IShutterstockAPI;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.remote.EndpointRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {ViewModelModule.class, ContextModule.class, FragmentBuilder.class, ActivityBuilder.class, RoomModule.class})
public class AppModule {

    @Provides
    @Singleton
    AdRequest provideAdRequest(){
        return new AdRequest.Builder().build();
    }

    @Provides
    @Singleton
    InterstitialAd provideInterstitialAd(Context context, AdRequest adRequest){
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.interstitial_id));
        interstitialAd.loadAd(adRequest);

        return interstitialAd;
    }

    @Provides
    SharedPreferences provideSharedPreferences(Application application){
        return application.getApplicationContext().getSharedPreferences(Constants.PICKYUP_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Gson provideGson(){
        return new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .create();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);

        httpClient.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                    .build();
            return chain.proceed(newRequest);
        }).build();

        httpClient.addInterceptor(new StethoInterceptor());
        httpClient.interceptors().add(httpLoggingInterceptor);

        return httpClient.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson){
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    IShutterstockAPI provideService(Retrofit retrofit){
        return retrofit.create(IShutterstockAPI.class);
    }

    @Provides
    @Singleton
    EndpointRepository provideEndpointRepository(IShutterstockAPI iShutterstockAPI){
        return new EndpointRepository(iShutterstockAPI);
    }
}
