package eu.szwiec.wifirtt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

class RttIntentService : Service() {

    fun <T> MutableList<T>.takeMax(max: Int) = this.subList(0, minOf(size, max))

    companion object {
        val result = NonNullLiveData<MutableList<String>>(mutableListOf())
        var isRunning = NonNullLiveData(false)
    }

    private val wifiManager by lazy { getSystemService(WifiManager::class.java) }
    private val rttManager by lazy { getSystemService(WifiRttManager::class.java) as WifiRttManager }

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                with(wifiManager.scanResults) {
                    if (size > 0) {
                        val rangingRequest = RangingRequest.Builder()
                                .addAccessPoints(takeMax(RangingRequest.getMaxPeers()))
                                .build()

                        rttManager.startRanging(rangingRequest, object : RangingResultCallback() {
                            override fun onRangingResults(results: MutableList<RangingResult>) {
                                if (isRunning.value) {
                                    wifiManager.startScan()
                                }

                                var list = result.value
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
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        result.postValue(mutableListOf())
        isRunning.postValue(true)
        startForeground()
        wifiManager.startScan()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopForeground(true)
        isRunning.postValue(false)
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun startForeground() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "my_service"
        val channelName = "My Background Service"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}