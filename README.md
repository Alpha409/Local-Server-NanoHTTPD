🌐 Android Local Server (NanoHTTPD)

A simple Android app that starts a local web server using NanoHTTPD and hosts a dummy website directly on your device — no internet required.

🚀 Features

Start and stop a lightweight HTTP server

Serve static HTML, CSS, JS, and images from assets/

Display your local IP and port for easy browser access

Simple, minimal UI

Works completely offline

📱 How It Works

Enter a port number (e.g., 8080)

Tap Start Server

The app hosts a local site at
👉 http://<your-device-ip>:8080

Open this address in your browser to view the dummy website

⚙️ Setup

Add NanoHTTPD to your build.gradle:

implementation 'org.nanohttpd:nanohttpd:2.3.1'


Place your web files inside the assets/ folder:

app/src/main/assets/
 ├── index.html
 ├── style.css
 └── script.js

🧠 Tech Used

Kotlin

NanoHTTPD

Android SDK

💡 Future Enhancements

Add HTTPS support

Serve files from internal/external storage

Add request logging UI

Implement file upload handling

🧑‍💻 Author

Muhammad Awais Umer
📍 Android Developer
🔗 GitHub: Alpha409
