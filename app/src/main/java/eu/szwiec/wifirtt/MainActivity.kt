package eu.szwiec.wifirtt

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(applicationContext, RttIntentService::class.java)

        RttIntentService.result.observe(this, Observer { result -> results.append(result) })

        scanToggleButton.setOnClickListener({
            if (scanToggleButton.isChecked) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
            }
        })
    }
}
