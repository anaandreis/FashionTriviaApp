package com.anaandreis.fashiontriviatest.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlinx.coroutines.flow.Flow


class DataScore(context: Context) {

    val Context.myDataStore: DataStore<Preferences> by preferencesDataStore(name = "my_datastore")

    private val mContext = context

    private object PreferencesKeys {
        val score = intPreferencesKey("score")
    }

    suspend fun saveToDataStore(score: Int) {
        Log.d("DataScore", "Saving score: $score")
        this@DataScore.mContext.myDataStore.edit  { preferences ->
            preferences[PreferencesKeys.score] = score
        }
    }

    val readFromDataStore: Flow<Int> = context.myDataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("dataSource", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preferences ->
            val myScore: Int = preferences[PreferencesKeys.score] ?: 0
            myScore
        }
}