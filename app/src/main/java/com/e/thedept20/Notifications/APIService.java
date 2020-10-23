package com.e.thedept20.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAl2fyP6k:APA91bHZPUq8k_RTuL2u-ddphYuQwPcasDQvFZ7aiQbMYP67mDwEooR0UH1MexNKCWFyu4VQFQiQv_Rlxsk3fBYuF0_5NciBiZg6PM_0UmXdKf5SrJB0NJ1v-3AVbi65csmZdn_EIg4p"
    })

    @POST("fcm/send")
    Call<Response> sendNotifications(@Body Sender body);
}
