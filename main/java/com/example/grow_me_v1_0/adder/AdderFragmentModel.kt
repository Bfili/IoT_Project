package com.example.grow_me_v1_0.adder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

class AdderFragmentModel : ViewModel() {
    private var _temperature_value_threshold = MutableLiveData<Double>()
    val temperature_value_threshold : LiveData<Double>
        get() = _temperature_value_threshold

    private var _actual_temp = MutableLiveData<Pair<Double, Double>>()
    val actual_temp : LiveData<Pair<Double, Double>>
        get() = _actual_temp

    init {
        _temperature_value_threshold.value = 0.5
        _actual_temp.value = Pair(0.0, 0.0)
    }
    fun getTemperatureThreshold(temperature_val : Double) : Pair<Double, Double>{
        var temp_actual_threshhold_low = temperature_val - temperature_value_threshold.value!!
        var temp_actual_threshhold_high = temperature_val + _temperature_value_threshold.value!!
        return Pair(temp_actual_threshhold_high, temp_actual_threshhold_low)
    }



}