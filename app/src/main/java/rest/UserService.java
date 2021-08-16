package rest;

import model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{id}")
    public Call<User> getUser(@Path("id") long userId);

}
