package top.yukonga.fontWeightTest.utils

import android.content.Context
import android.content.SharedPreferences

class Preferences {
    private var context = AppContext.context
    private val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("font_weight", Context.MODE_PRIVATE)

    fun perfSet(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    fun perfGet(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }

    fun perfRemove(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }
}