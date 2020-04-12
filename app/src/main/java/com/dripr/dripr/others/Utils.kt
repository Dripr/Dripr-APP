package com.dripr.dripr.others

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.dripr.dripr.activities.LoginActivity
import retrofit2.HttpException

class Utils {

    companion object {
        fun quickToast(ctx: Context, msg: String) {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
        }


        fun onError(ctx: Context, ex: Exception) {
            if (ex is HttpException) {
                when(ex.code()) {
                    401 -> ctx.startActivity(Intent(ctx, LoginActivity::class.java))
                    else -> Log.d("[NET - ERROR]", "Une erreur s'est produite, veuillez réessayer plus tard")
                }
            } else Log.d("[NET - ERROR]", "Une erreur s'est produite, veuillez réessayer plus tard")
        }
    }
}