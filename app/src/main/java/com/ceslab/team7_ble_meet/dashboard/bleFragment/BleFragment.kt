package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.addZeroNum
import com.ceslab.team7_ble_meet.ble.BleDataScanned
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.databinding.FragmentBleBinding
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase


class BleFragment : Fragment() {
    private val TAG = "Ble_Lifecycle"

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }

    private lateinit var bleFragmentBinding: FragmentBleBinding
    private lateinit var bleFragmentViewModel: BleFragmentViewModel

    //adapter for recycler view
    private var listDataDiscoveredAdapter = ListBleDataScannedAdapter()

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        bleFragmentViewModel.stopFindFriend()
                        bleFragmentBinding.swTurnOnOffBLE.isChecked = false
                        bleFragmentBinding.vRipple.stopRippleAnimation()
                        Toast.makeText(requireContext(), "Bluetooth off", Toast.LENGTH_LONG).show()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        bleFragmentBinding.swTurnOnOffBLE.isChecked = true
                        Toast.makeText(requireContext(), "Bluetooth on", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        checkPermissions()
        setUpBleFragmentViewModelBinding(inflater, container)
        setUpBle()
        return bleFragmentBinding.root
    }

    private fun checkPermissions() {
        val reqPermissions = ArrayList<String>()
        if (activity?.let
            {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= 23 && activity?.let
            {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (reqPermissions.isNotEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, reqPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty())) {
                    grantResults.forEach { result ->
                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            finish()
                            return
                        }
                    }
                }
            }
        }
    }

    private fun setUpBleFragmentViewModelBinding(inflater: LayoutInflater, container: ViewGroup?) {
        //set up view model
        bleFragmentViewModel = ViewModelProvider(requireActivity()).get(BleFragmentViewModel::class.java)
        bleFragmentViewModel.context = requireContext()
        bleFragmentViewModel.isMyServiceRunning()

        //set up data binding
        bleFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ble,
            container,
            false
        )
        bleFragmentBinding.apply {
            //init state of bluetooth switch
            if (bleFragmentViewModel.bluetoothAdapter.isEnabled) swTurnOnOffBLE.isChecked = true
            //set up switch change
            swTurnOnOffBLE.setOnCheckedChangeListener { _, isChecked ->
                bleFragmentViewModel.changeBluetoothStatus(isChecked)
            }

            //set up click find friend
            btnFindFriend.setOnClickListener {
                bleFragmentViewModel.findFriend()
            }

            //set up ripple
            bleFragmentViewModel.isRunning.observe(requireActivity(), {
                if (it) {
                    vRipple.startRippleAnimation()
                } else {
                    vRipple.stopRippleAnimation()
                }
            })

            btnStartFindFriend.setOnClickListener {
                bleFragmentViewModel.startFindFriend()
            }

            btnStopFindFriend.setOnClickListener {
                bleFragmentViewModel.stopFindFriend()
            }

            btnDelete.setOnClickListener {
                bleFragmentViewModel.deleteBleDataScanned(requireContext())
            }

            bleFragmentViewModel.setUpListDataScanned(requireContext(), requireActivity())
            rcListBleDataScanned.adapter = listDataDiscoveredAdapter
            bleFragmentViewModel.isBleDataScannedDisplay.observe(requireActivity(), {
                if (it) {
                    btnFindFriend.isGone = true
                    vRipple.isGone = true
                    rcListBleDataScanned.visibility = View.VISIBLE
                    listDataDiscoveredAdapter.data = BleDataScannedDataBase
                        .getDatabase(requireActivity())
                        .bleDataScannedDao()
                        .getUserDiscover() as ArrayList<BleDataScanned>
                } else {
                    btnFindFriend.isGone = false
                    vRipple.isGone = false
                    rcListBleDataScanned.visibility = View.GONE
                }
            })

            listDataDiscoveredAdapter.listener = object : ListBleDataScannedAdapter.IdClickedListener {
                    override fun onClickListen(id: String) {
                        Log.d(TAG, id)
                    }
                }
            listDataDiscoveredAdapter.nextlistener =
                object : ListBleDataScannedAdapter.onClickNextListender {
                    override fun onClick(id: String) {
                        Log.d(TAG, "id: $id")
                        val num = addZeroNum(id.toInt())
                        val intent = Intent(activity, ChatActivity::class.java)
                        intent.putExtra(AppConstants.USER_ID, num)
                        startActivity(intent)
                    }
                }
        }
    }

    private fun setUpBle() {
        //Register broadcast receiver to receive state of bluetooth
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
    }
}