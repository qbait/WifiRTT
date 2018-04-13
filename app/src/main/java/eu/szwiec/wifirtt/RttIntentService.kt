package eu.szwiec.wifirtt

import android.app.IntentService
import android.arch.lifecycle.MutableLiveData
import android.content.Intent


class RttIntentService : IntentService("Cashback IntentService") {

    companion object {
        val result = MutableLiveData<String>()
    }

    var isRunning = true

    override fun onHandleIntent(intent: Intent?) {
        sendCashbackInfoToClient()
    }

    private fun sendCashbackInfoToClient() {
        while (isRunning) {
            result.postValue("xxx")
            Thread.sleep(3000)
        }
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }
}