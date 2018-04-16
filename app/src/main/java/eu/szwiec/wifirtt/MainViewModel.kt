package eu.szwiec.wifirtt

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val allResults = NonNullLiveData("")
    val serviceIntent = Intent(getApplication(), RttIntentService::class.java)
    var isPemissionGranted = NonNullLiveData(false)
    var isServiceRunning = RttIntentService.isRunning

    init {
        RttIntentService.result.observeForever({ result ->
            if(result != null && result.size!=0)  {
                allResults.value = allResults.value + "\n" + result.joinToString(separator = "\n")
            }
        })
    }

    fun toggleScan() {
        if (isServiceRunning.value) {
            getApplication<Application>().stopService(serviceIntent)
        } else {
            allResults.value = ""
            getApplication<Application>().startService(serviceIntent)
        }
    }
}
