package com.dineshprabha.openinappassignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dineshprabha.openinappassignment.models.UserClickResponse
import com.dineshprabha.openinappassignment.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {


//    private val _userData = MutableLiveData<Response<UserClickResponse>>()
//    val userData : LiveData<Response<UserClickResponse>> get() = _userData

    val userClickLiveData get() = repository.userClickData

    fun fetchData(){
        viewModelScope.launch {
            repository.getDashboardData()
        }
    }
}