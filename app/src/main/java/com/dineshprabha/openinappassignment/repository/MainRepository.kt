package com.dineshprabha.openinappassignment.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dineshprabha.openinappassignment.api.APIService
import com.dineshprabha.openinappassignment.models.UserClickResponse
import com.dineshprabha.openinappassignment.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: APIService) {

    private val _userClicksLiveData = MutableLiveData<NetworkResult<UserClickResponse>>()
    val userClickData : LiveData<NetworkResult<UserClickResponse>>
        get() = _userClicksLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    suspend fun getDashboardData(){
        _userClicksLiveData.postValue(NetworkResult.Loading())
        val response = apiService.getDashboardData()

        if (response.isSuccessful && response.body() != null){
            _userClicksLiveData.postValue(NetworkResult.Success(response.body()!!))
        }else if (response.errorBody() != null){
//            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
//            _userClicksLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }else{
            _userClicksLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}