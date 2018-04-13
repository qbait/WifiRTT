package eu.szwiec.wifirtt

import android.app.IntentService
import android.content.Intent


class RttIntentService : IntentService("Cashback IntentService") {

    var isRunning = true

    override fun onHandleIntent(intent: Intent?) {
        sendCashbackInfoToClient()
    }

    private fun sendCashbackInfoToClient() {
        while (isRunning) {
            val intent = Intent()
            intent.action = RESULT
            intent.putExtra("result", "xxx")
            sendBroadcast(intent)
            Thread.sleep(3000)
        }
    }

    companion object {
        internal val RESULT = "result"
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }
}