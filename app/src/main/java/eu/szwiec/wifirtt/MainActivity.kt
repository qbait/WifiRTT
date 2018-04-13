package eu.szwiec.wifirtt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var serviceReciver: ServiceReciver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(applicationContext, RttIntentService::class.java)

        scanToggleButton.setOnClickListener({
            if (scanToggleButton.isChecked) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
            }
        })

        registerCashbackReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(serviceReciver)
    }

    private fun registerCashbackReceiver() {
        serviceReciver = ServiceReciver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(RttIntentService.RESULT)

        registerReceiver(serviceReciver, intentFilter)
    }

    private inner class ServiceReciver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getStringExtra("result")
            results.text = results.text.toString() + "," + result
        }
    }

}
