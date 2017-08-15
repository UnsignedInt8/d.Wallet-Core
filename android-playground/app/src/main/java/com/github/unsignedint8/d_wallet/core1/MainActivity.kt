package com.github.unsignedint8.d_wallet.core1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.dunsignedint8.core1.SocketEx
import com.github.dunsignedint8.core1.generateKeyPair

import kotlinx.coroutines.experimental.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch(Unconfined) {
            repeat(100) { i ->
                println("      'Unconfined': I'm working in thread ${Thread.currentThread().name}")
                Log.d("repeat", "$i ")
                delay(1000)
            }
        }

        runBlocking {
            val e2 = SocketEx()
            Log.d("blocking", "${e2.connectAsync("baidu.com", 80).await()} connected")
        }

        val ex = SocketEx()
        async(CommonPool) {
            val result = ex.connect("baidu.com", 80)
            Log.d("async",  "$result connected")
        }


        println(generateKeyPair())
    }
}
