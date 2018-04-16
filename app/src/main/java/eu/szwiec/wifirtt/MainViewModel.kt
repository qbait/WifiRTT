package eu.szwiec.wifirtt

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val allResults = NonNullLiveData("")
    val serviceIntent = Intent(getApplication(), RttIntentService::class.java)
    var isScanning = false
    var isPemissionGranted = NonNullLiveData(false)

    init {
        RttIntentService.result.observeForever({ result ->
            if(result != null && result.size!=0)  {
                allResults.value = allResults.value + "\n" + result.joinToString(separator = "\n")
            }
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
