package com.meshchat.android.mesh

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Handles all Bluetooth permission checking and diagnostic auditing logic.
 */
class BluetoothPermissionManager(private val context: Context) {

    companion object {
        private const val TAG = "BluetoothPermissionAudit"
    }

    /**
     * Check if a specific permission is granted using ContextCompat
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if OS-level location services toggle is enabled
     */
    fun isLocationServicesEnabled(): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager == null) {
                false
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error checking OS location services state: ${e.message}")
            false
        }
    }

    /**
     * Check if all required Bluetooth permissions are granted for mesh operations
     */
    fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scanGranted = isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)
            val advGranted = isPermissionGranted(Manifest.permission.BLUETOOTH_ADVERTISE)
            val connGranted = isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)
            val fineGranted = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
            val coarseGranted = isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

            // On Android 12+, BLUETOOTH_SCAN, ADVERTISE, CONNECT are required.
            // If location permissions are also requested (for scans without neverForLocation), check either is fine/coarse.
            val btCore = scanGranted && advGranted && connGranted
            val loc = fineGranted || coarseGranted
            btCore && loc
        } else {
            val bt = isPermissionGranted(Manifest.permission.BLUETOOTH)
            val btAdmin = isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)
            val fine = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
            bt && btAdmin && (fine || coarse)
        }
    }

    /**
     * Check permissions specifically required for BLE scanning
     */
    fun hasScanPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scan = isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)
            val conn = isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)
            val loc = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
                      isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
            scan && conn && loc
        } else {
            val bt = isPermissionGranted(Manifest.permission.BLUETOOTH)
            val loc = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
                      isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
            bt && loc
        }
    }

    /**
     * Check permissions specifically required for BLE advertising
     */
    fun hasAdvertisePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isPermissionGranted(Manifest.permission.BLUETOOTH_ADVERTISE) &&
            isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            isPermissionGranted(Manifest.permission.BLUETOOTH) &&
            isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    /**
     * Comprehensive diagnostic logging of all permissions and OS toggles before BLE calls
     */
    fun logPermissionAudit(callerTag: String, operation: String) {
        val sdk = Build.VERSION.SDK_INT
        val locEnabled = isLocationServicesEnabled()

        Log.i(callerTag, "==================== [STAGE 1: BLE PERMISSION AUDIT] ====================")
        Log.i(callerTag, "Operation: $operation | Android SDK: $sdk (API $sdk)")
        if (sdk >= Build.VERSION_CODES.S) {
            Log.i(callerTag, "  - BLUETOOTH_SCAN:      ${if (isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)) "✅ GRANTED" else "❌ DENIED"}")
            Log.i(callerTag, "  - BLUETOOTH_ADVERTISE: ${if (isPermissionGranted(Manifest.permission.BLUETOOTH_ADVERTISE)) "✅ GRANTED" else "❌ DENIED"}")
            Log.i(callerTag, "  - BLUETOOTH_CONNECT:   ${if (isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) "✅ GRANTED" else "❌ DENIED"}")
        } else {
            Log.i(callerTag, "  - BLUETOOTH:           ${if (isPermissionGranted(Manifest.permission.BLUETOOTH)) "✅ GRANTED" else "❌ DENIED"}")
            Log.i(callerTag, "  - BLUETOOTH_ADMIN:     ${if (isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)) "✅ GRANTED" else "❌ DENIED"}")
        }
        Log.i(callerTag, "  - ACCESS_FINE_LOCATION:   ${if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) "✅ GRANTED" else "❌ DENIED"}")
        Log.i(callerTag, "  - ACCESS_COARSE_LOCATION: ${if (isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) "✅ GRANTED" else "❌ DENIED"}")
        Log.i(callerTag, "  - OS Location Services:   ${if (locEnabled) "✅ ENABLED (ON)" else "❌ DISABLED (OFF - BLE scans may be suppressed on Android < 12!)"}")
        Log.i(callerTag, "=========================================================================")
    }
}

