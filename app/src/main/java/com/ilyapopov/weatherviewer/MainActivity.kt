package com.ilyapopov.weatherviewer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private val presenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_find.setOnClickListener {
            presenter.onFindBtnClicked(city_text_input.text.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("city_name_tv", city_name_tv.text.toString())
        outState.putString("weather_info_tv", weather_info_tv.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        city_name_tv.text = savedInstanceState.getString("city_name_tv")
        weather_info_tv.text = savedInstanceState.getString("weather_info_tv")
    }

    /**
     * Активация прогресс-бара
     */
    fun enableProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    /**
     * Деактиваия прогресс-бара
     */
    fun disableProgressBar() {
        progress_bar.visibility = View.INVISIBLE
    }

    fun setCityName(cityName: String?) {
        city_name_tv.text = cityName
    }

    fun setWeatherInfo(weatherInfo: String?) {
        weather_info_tv.text = weatherInfo
    }

    /**
     * Вывод информации о том, что данные о погоде не найдены нигде
     */
    fun showNotFoundInfo() {
        city_name_tv.text = ""
        weather_info_tv.text = ""
        Toast.makeText(
            this.applicationContext,"Ничего не найдено",Toast.LENGTH_LONG
        ).show()
    }
}
