package org.technoserve.cherie

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context){

    private val KEY = "default"
    private val NO_OF_GENERATED_PREDICTIONS = "NO_OF_GENERATED_PREDICTIONS"
    private val NO_OF_UPLOADED_PREDICTIONS = "NO_OF_UPLOADED_PREDICTIONS"

    private val persistor: SharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)

    var generatedPredictions: Int
        get() = persistor.getInt(NO_OF_GENERATED_PREDICTIONS, 0)
        set(value) = persistor.edit().putInt(NO_OF_GENERATED_PREDICTIONS, value).apply()


    var uploadedPredictions: Int
        get() = persistor.getInt(NO_OF_UPLOADED_PREDICTIONS, 0)
        set(value) = persistor.edit().putInt(NO_OF_UPLOADED_PREDICTIONS, value).apply()
}