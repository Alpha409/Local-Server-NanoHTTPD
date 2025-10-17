package com.example.localserver

import android.content.Context
import android.util.Log
import com.example.localserver.MainActivity.Companion.isServerRunning
import fi.iki.elonen.NanoHTTPD

class MyNanoHTTPDServer(
    private val context: Context, portNum: Int
) : NanoHTTPD(portNum) {

    override fun serve(session: IHTTPSession): Response {
        val response: Response = if (session.method == Method.OPTIONS) {
            // 🔑 Preflight response
            newFixedLengthResponse(Response.Status.OK, "text/plain", "")
        } else {
            // Your existing GET/POST handling
            try {
                val uri = session.uri.removePrefix("/")
                val fileName = if (uri.isEmpty()) "web/index.html" else "web/$uri"
                val stream = context.assets.open(fileName)
                newChunkedResponse(Response.Status.OK, getMimeTypeForFile(fileName), stream)
            } catch (e: Exception) {
                newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found")
            }
        }

        // ✅ CORS headers must be on *every* response
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.addHeader(
            "Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization"
        )
        response.addHeader("Access-Control-Allow-Credentials", "true")

        Log.d(
            "HTTPServer",
            "Response for ${session.uri}: ${response.status} headers=${response.getHeader("Access-Control-Allow-Origin")}"
        )

        return response
    }

}