package get.shoplist.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface Webservice {

    @GET("/api/clients/get_clients")
    Call<ResponseBody>   get_clients(
            @Query("app_id") String app_id
    );
    @GET("/api/clients/invite")
    Call<ResponseBody>   invite_client(
            @Query("user_id") int user_id,
            @Query("app_id") String app_id,
            @Query("data") String data
    );
    @GET("/api/clients/del_client")
    Call<ResponseBody>   del_clients(
            @Query("client_id") int client_id

    );
    @GET("/api/enroll/del_meeting")
    Call<ResponseBody>   del_meeting(
            @Query("meeting_id") int meeting_id

    );
    @PUT("/api/enroll/date_picker")
    Call<ResponseBody>  date_picker(
            @Query("invite_id") int invite_id,
            @Query("time") String time
    );
    @GET("/api/enroll/schedule")
    Call<ResponseBody>  schedule_clients(
            @Query("app_id") String app_id
    );
    @GET("/api/enroll/schedule_day")
    Call<ResponseBody>  schedule_day(
            @Query("app_id") String app_id,
            @Query("mounth") int app,
            @Query("day") int day
    );
    @Headers({
            "Content-Type: text/html; charset=utf-8"
    })
    @GET("/api/clients/add")
    Call<ResponseBody>   add_clients(
            @Query("app_id") String app_id,
            @Query("name") String name,
            @Query("phone") String phone,
            @Query("social") String social,
            @Query("location") String location

    );


    @PUT("/Authentication/RecoveryPassword")
    Call<ResponseBody>  RecoveryPassword(
            @Query("Email") String message
    );
    @Headers({
            "Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiMyIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWVpZGVudGlmaWVyIjoiMyIsImp0aSI6IjFmM2QxMDJlLWE4ODktNDc2Mi1hYTBhLTg3ZTQ0MjIwMGVkZCIsImV4cCI6IjE2NTI1MTczMzUiLCJuYmYiOiIxNjQ5OTI1MzM1IiwiaXNzIjoiSldUIiwiYXVkIjoiSldUIn0.Zs_-xeNx2me6XGJT6f--wX8dzATZmBmqV_dOKsWG8ds"
    })
    @PUT("/User/ChangePassword")
    Call<ResponseBody>  ChangePassword(
            @Query("newPassword") String message
    );
}