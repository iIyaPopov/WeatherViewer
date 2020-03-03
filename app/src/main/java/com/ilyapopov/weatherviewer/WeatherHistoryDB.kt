package com.ilyapopov.weatherviewer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class WeatherHistoryDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WeatherHistoryDB"
        private const val TABLE_WEATHER = "History"
        private const val KEY_CITY = "city"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_TEMP = "temp"

        private const val CREATE_TABLE_QUERY =
            ("""
                CREATE TABLE $TABLE_WEATHER (
                    $KEY_CITY TEXT PRIMARY KEY,
                    $KEY_DESCRIPTION TEXT,
                    $KEY_TEMP TEXT
                )
            """)
        private const val DROP_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_WEATHER"
        private const val SELECT_QUERY = "SELECT * FROM $TABLE_WEATHER"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_QUERY)
        onCreate(db)
    }

    /**
     * Добавление новой записи о погоде в городе
     */
    fun addWeatherInfo(weatherInfo: WeatherInfo): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CITY, weatherInfo.city)
        contentValues.put(KEY_DESCRIPTION, weatherInfo.description)
        contentValues.put(KEY_TEMP, weatherInfo.temp)
        val success = db.insert(TABLE_WEATHER, null, contentValues)
        db.close()
        return success
    }

    /**
     * Получение всех записей
     */
    private fun viewHistory(): ArrayList<WeatherInfo>{
        val weatherList:ArrayList<WeatherInfo> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(SELECT_QUERY, null)
        } catch (e: SQLiteException) {
            db.execSQL(SELECT_QUERY)
            return ArrayList()
        }
        var weatherCity: String
        var weatherDescription: String
        var weatherTemp: String
        if (cursor.moveToFirst()) {
            do {
                weatherCity = cursor.getString(cursor.getColumnIndex(KEY_CITY))
                weatherDescription = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
                weatherTemp = cursor.getString(cursor.getColumnIndex(KEY_TEMP))
                val weatherInfo = WeatherInfo(weatherCity, weatherDescription, weatherTemp)
                weatherList.add(weatherInfo)
            } while (cursor.moveToNext())
        }
        db.close()
        return weatherList
    }

    /**
     * Обновление информации по существующему городу
     */
    fun updateWeatherInfo(weatherInfo: WeatherInfo): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_CITY, weatherInfo.city)
        contentValues.put(KEY_DESCRIPTION, weatherInfo.description)
        contentValues.put(KEY_TEMP, weatherInfo.temp)

        val success = db.update(TABLE_WEATHER, contentValues, "$KEY_CITY", null)
        db.close()
        return success
    }

    /**
     * Проверка существование записи в БД о городе
     * @return Объект с информацией о погоде или null, если нет ничего
     */
    fun checkWeatherInfo(cityName: String): WeatherInfo? {
        val weatherArray = viewHistory()
        for (i in weatherArray) {
            if(cityName == i.city) {
                return i
            }
        }
        return null
    }

    /**
     * Проверка существование записи в БД о городе
     * @return true если, запись найдена, false - иначе
     */
    fun isWeatherInfoExists(cityName: String): Boolean {
        val weatherArray = viewHistory()
        for (i in weatherArray) {
            if(cityName == i.city) {
                return true
            }
        }
        return false
    }

    /*fun selectWeatherInfo(cityName: String): WeatherInfo? {
        val selectQuery = "SELECT * FROM $TABLE_WEATHER WHERE $KEY_CITY=$cityName"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            val weatherCity = cursor.getString(cursor.getColumnIndex(KEY_CITY))
            val weatherDescription = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
            val weatherTemp = cursor.getString(cursor.getColumnIndex(KEY_TEMP))
            return WeatherInfo(weatherCity, weatherDescription, weatherTemp)
        }
        return null
    }*/
}