package eu.szwiec.wifirtt

import android.app.IntentService
import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager


class RttIntentService : IntentService("wifiRttService") {

    fun <T> MutableList<T>.takeMax(max: Int) = this.subList(0, minOf(size, max))

    companion object {
        val result = MutableLiveData<List<String>>()
    }

    lateinit var mWifiManager: WifiManager

    private lateinit var receiver: BroadcastReceiver

    var isRunning = true

    override fun onCreate() {
        super.onCreate()

        mWifiManager = getSystemService(WifiManager::class.java)
        val rttManager = getSystemService(WifiRttManager::class.java) as WifiRttManager

        receiver =  object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                with(mWifiManager.scanResults) {
                    if (size > 0) {
                        val rangingRequest = RangingRequest.Builder()
                                .addAccessPoints(takeMax(RangingRequest.getMaxPeers()))
                                .build()

                        rttManager.startRanging(rangingRequest, object : RangingResultCallback() {
                            override fun onRangingResults(results: MutableList<RangingResult>) {
                                if (isRunning) {
                                    mWifiManager.startScan()
                                }

                                var list = mutableListOf<String>()
                                results
                                        .filter { it.status == RangingResult.STATUS_SUCCESS }
                                        .forEach {
                                            list.add("${it.rangingTimestampUs},${it.macAddress},${it.distanceMm},${it.distanceStdDevMm},${it.rssi}")
                                        }
                                result.postValue(list)
                            }

                            override fun onRangingFailure(p0: Int) {
                                val text = "\nerror in ranging request, code: ${p0}"
                                result.postValue(mutableListOf(text))
                            }
                        }, null)
                    }
                }
            }

        }

        registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onHandleIntent(intent: Intent?) {
        mWifiManager.startScan()

        while (isRunning) {
            Thread.sleep(3000)
        }
    }

    override fun onDestroy() {
        isRunning = false
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}