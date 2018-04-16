package eu.szwiec.wifirtt

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Transformations
import android.content.Intent

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
}
