package com.zzw.asmdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by zzw on 2019-08-15
 * Des:
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("zzz","-->onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("zzz","-->onDestroy")
    }
}
