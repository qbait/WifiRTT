package eu.szwiec.wifirtt

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.szwiec.wifirtt.databinding.ActivityMainBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.let {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }

        requestLocationAndWritePermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasLocationAndContactsPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *LOCATION_AND_WRITE)
    }

    @AfterPermissionGranted(RC_LOCATION_WRITE_PERM)
    fun requestLocationAndWritePermissions() {
        if (!hasLocationAndContactsPermissions()) {
            EasyPermissions.requestPermissions(this, "This App needs location and storage to write data", RC_LOCATION_WRITE_PERM, *LOCATION_AND_WRITE)
        } else {
            viewModel.isPemissionGranted.value = true
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (perms.size == LOCATION_AND_WRITE.size) {
            viewModel.isPemissionGranted.value = true
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        viewModel.isPemissionGranted.value = false
    }
}

private val LOCATION_AND_WRITE = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
private const val RC_LOCATION_WRITE_PERM = 124