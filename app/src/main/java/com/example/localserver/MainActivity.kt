package com.example.localserver

import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.localserver.databinding.ActivityMainBinding
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    private var server: MyNanoHTTPDServer? = null
    private var isServerRunning = false
    private var currentPort: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.btnStartServer?.setOnClickListener {
            val portText = binding?.edtPort?.text?.toString()?.trim()
            if (portText.isNullOrEmpty()) {
                Toast.makeText(this, "First enter port number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val portNumber = portText.toIntOrNull()
            if (portNumber == null) {
                Toast.makeText(this, "Enter a valid numeric port", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (portNumber <= 1024) {
                Toast.makeText(this, "Enter port number above 1024", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If server already running on same port -> just show message
            if (isServerRunning && currentPort == portNumber) {
                Toast.makeText(this, "Server already running on port $portNumber", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Restart server on new port
            if (isServerRunning) {
                stopServer()
            }
            startServer(portNumber)
        }


    }
    private fun startServer(portNumber: Int) {
        val ip = getLocalIpAddress() ?: "127.0.0.1"
        Log.d("HTTPServer", "Starting server at http://$ip:$portNumber/")

        try {
            server = MyNanoHTTPDServer(this, portNumber)
            server?.start() // NanoHTTPD.start() can throw IOException in some versions
            isServerRunning = true
            currentPort = portNumber

            // Update UI
            runOnUiThread {
                binding?.txtIp?.visibility = View.VISIBLE
                binding?.txtIp?.text = "http://$ip:$portNumber"
                Toast.makeText(this, "Server running at http://$ip:$portNumber", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("HTTPServer", "Failed to start server", e)
            isServerRunning = false
            currentPort = null
            server = null
            runOnUiThread {
                Toast.makeText(this, "Failed to start server: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun stopServer() {
        try {
            server?.stop()
        } catch (e: Exception) {
            Log.w("HTTPServer", "Error stopping server", e)
        } finally {
            server = null
            isServerRunning = false
            currentPort = null
            runOnUiThread {
                binding?.txtIp?.visibility = View.GONE
            }
        }
    }





    override fun onDestroy() {
        super.onDestroy()
     stopServer()
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(':') < 0) {
                        Log.d("HTTPServer", "Server ${inetAddress.hostAddress}")
                        return inetAddress.hostAddress
                    }
                }

            }
        } catch (ex: Exception) {
            Log.d("HTTPServer", "Server Error ${ex.message}")
        }
        return null
    }

}