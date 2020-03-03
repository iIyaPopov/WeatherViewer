package com.ilyapopov.weatherviewer

import android.app.Activity
import org.json.JSONArray
import org.json.JSONObject
import khttp.get as httpGet

class MainRepository(mainActivity: Activity) {

    companion object {
        /**
         * Адрес сервера
         */
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/weather"

        /**
         * Город по умолчанию
         */
        private const val DEFAULT_CITY_NAME = "Санкт-Петербург"

        /**
         * Ключ для использования API
         */
        private const val API_KEY = "ff54761d1aea0fa9b112f1a598f42113"

        /**
         * Формат возвращаемых данных
         */
        private const val RETURN_DATA_FORMAT = "json"

        /**
         * Язык текста в ответе
         */
        private const val LANG = "ru"

        /**
         * Единицы измерения
         */
        private const val UNITS = "metric"

        private const val HTTP_OK = "200"
    }

    /**
     * Локальная база данных
     */
    private val weatherDB = WeatherHistoryDB(mainActivity)

    /**
     * Ответ сервера
     */
    private lateinit var response: JSONObject

    /**
     * Выполняет запрос погоды в городе, который указан по умолчанию
     */
    fun defaultHttpGetRequest() {
        httpGetRequest(DEFAULT_CITY_NAME)
    }

    /**
     * Выполняет запрос погоды в городе
     * @param cityName Город, для которого выполняется поиск
     */
    private fun httpGetRequest(cityName: String) {
        val request = httpGet(
            url = BASE_URL,
            params = mapOf(
                "q" to cityName,
                "lang" to LANG,
                "mode" to RETURN_DATA_FORMAT,
                "units" to UNITS,
                "appid" to API_KEY
            )
        )
        response = request.jsonObject
    }


    /**
     * Выполняет поиск иформации о погоде в сети
     * @param cityName Название города
     */
    fun searchWeatherInfo(cityName: String) {
        Thread(Runnable {
            httpGetRequest(cityName)
            val responseCode = response["cod"].toString()
            if(responseCode == HTTP_OK) {
                val newCityName = response["name"].toString()
                val newDescription = ((response["weather"] as JSONArray)[0] as JSONObject)["description"].toString()
                var newTemp = (response["main"] as JSONObject)["temp"].toString()
                if(newTemp.toFloat() > 0) {
                    newTemp = "+$newTemp"
                }
                if(!weatherDB.isWeatherInfoExists(newCityName)) {
                    weatherDB.addWeatherInfo(WeatherInfo(newCityName, newDescription, newTemp))
                } else {
                    weatherDB.updateWeatherInfo(WeatherInfo(newCityName, newDescription, newTemp))
                }

            }
        }).start()
    }

    /**
     * Выполняет поиск иформации о погоде в локальной БД
     * @param cityName Название города
     */
    fun checkWeatherInfo(cityName: String): WeatherInfo? {
        return weatherDB.checkWeatherInfo(cityName)
    }

}