package com.ilyapopov.weatherviewer

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager


class MainPresenter(private val activity: MainActivity) {

    /**
     * Место "хранения" данных
     */
    private val repository = MainRepository(activity)

    /**
     * Для взаимодействия с активити из другого потока
     */
    private val activityHandler = ActivityHandler(activity)

    /**
     * Была нажата кнопка поиска
     */
    fun onFindBtnClicked(cityName: String) {
        Thread(Runnable {
            activityHandler.sendEmptyMessage(ActivityHandler.PROGRESS_BAR_ON)
            if(isOnline()) {
                repository.searchWeatherInfo(cityName)
                Thread.sleep(2000)
            }
            var weatherInfo = repository.checkWeatherInfo(cityName)
            if(weatherInfo != null) {
                var msg = activityHandler.obtainMessage(ActivityHandler.EDIT_CITY_NAME_TEXT_VIEW, weatherInfo)
                activityHandler.sendMessage(msg)
                msg = activityHandler.obtainMessage(ActivityHandler.EDIT_WEATHER_INFO_TEXT_VIEW, weatherInfo)
                activityHandler.sendMessage(msg)
            } else {
                activityHandler.sendEmptyMessage(ActivityHandler.NOT_FOUND)
            }
            activityHandler.sendEmptyMessage(ActivityHandler.PROGRESS_BAR_OFF)
        }).start()
    }

    /**
     * Проверка подключения к сети
     */
    private fun isOnline(): Boolean {
        val cm = activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo == null) {
            return false
        }
        return true
    }

}