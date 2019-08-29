package com.example.guftagoochatapp.Fragments;

import com.example.guftagoochatapp.Notifications.MyResponse;
import com.example.guftagoochatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA1LjW7GM:APA91bGYge3uOvWlXASLS7ohQno_inSAGnEWtYwNHNp9wiwpB8onOeUEoYvz3slgEyNjedKYp16k-bZQTOhM4sAqoiDmMC3GwpEzrL6CZrNvND6hR-bDS8cH_EdNjqa726PTtFiJ1ZC8"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
