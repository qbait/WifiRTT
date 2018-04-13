package eu.szwiec.wifirtt

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val allResults = NonNullLiveData("")
    val serviceIntent = Intent(getApplication(), RttIntentService::class.java)
    var isScanning = false

    init {
        RttIntentService.result.observeForever({ result ->
            allResults.value = allResults.value + "\n" + result
        })
    }

    fun toggleScan() {
        if (isScanning) {
            getApplication<Application>().stopService(serviceIntent)
            isScanning = false
        } else {
            allResults.value = ""
            getApplication<Application>().startService(serviceIntent)
            isScanning = true
        }
    }
}
