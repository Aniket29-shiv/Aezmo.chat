package com.aniket.aezmodate.api;
import com.aniket.aezmodate.model.ConversationBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
//matchList
public interface ProfileApi {
    @GET("S4UW")
    Call<ArrayList<ConversationBean>> getProfileData();
}
