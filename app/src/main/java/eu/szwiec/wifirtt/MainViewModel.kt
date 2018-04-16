package eu.szwiec.wifirtt

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Transformations
import android.content.Intent
import android.os.Environment
import org.apache.commons.io.FileUtils
import java.io.File
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val serviceIntent = Intent(getApplication(), RttIntentService::class.java)
    var isPemissionGranted = NonNullLiveData(false)
    var isServiceRunning = RttIntentService.isRunning
    val allResults = Transformations.map(RttIntentService.result, { result -> result.joinToString(separator = "\n") })

    fun toggleScan() {
        if (isServiceRunning.value) {
            getApplication<Application>().stopService(serviceIntent)
        } else {
            getApplication<Application>().startService(serviceIntent)
        }
    }

    fun saveOnSdCard() {
        val file = File("${Environment.getExternalStorageDirectory().getPath()}/wifi_rtt/${LocalDateTime.now()}")
        FileUtils.writeStringToFile(file, allResults.value, "UTF-8")
    }
}
