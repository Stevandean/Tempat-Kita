package com.example.tempatkita.api;

import com.example.tempatkita.model.Province;
import com.example.tempatkita.model.Regency;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/provinces.json")
    Call<List<Province>> getProvinces();

    @GET("api/regencies/{province_id}.json")
    Call<List<Regency>> getRegencies(@Path("province_id") String provinceId);
}
