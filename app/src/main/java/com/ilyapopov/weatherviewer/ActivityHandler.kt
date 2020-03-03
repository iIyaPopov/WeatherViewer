package com.ilyapopov.weatherviewer

import android.os.Handler
import android.os.Message
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference

class ActivityHandler(activity: MainActivity): Handler() {

    private val activityRef = WeakReference<MainActivity>(activity)

    companion object {
        /**
         * Включение прогресс-бара
         */
        const val PROGRESS_BAR_ON = 1

        /**
         * Выключение прогресс-бара
         */
        const val PROGRESS_BAR_OFF = 2

        /**
         * Редактирование название города
         */
        const val EDIT_CITY_NAME_TEXT_VIEW = 3

        /**
         * Редактирование информации о погоде
         */
        const val EDIT_WEATHER_INFO_TEXT_VIEW = 4

        /**
         * Показ сообщения о невозможности найти информацию
         */
        const val NOT_FOUND = 5
    }

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            PROGRESS_BAR_ON -> activityRef.get()?.enableProgressBar()
            PROGRESS_BAR_OFF -> activityRef.get()?.disableProgressBar()
            EDIT_CITY_NAME_TEXT_VIEW -> {
                val weatherInfo = msg.obj as WeatherInfo
                activityRef.get()?.setCityName(weatherInfo.city)
            }
            EDIT_WEATHER_INFO_TEXT_VIEW -> {
                val weatherInfo = msg.obj as WeatherInfo
                activityRef.get()?.setWeatherInfo("${weatherInfo.temp}, ${weatherInfo.description}")
            }
            NOT_FOUND -> activityRef.get()?.showNotFoundInfo()
        }
    }
}