package com.example.datadrobe

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Layout.Alignment
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.BoolRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.cjcrafter.openai.OpenAI
import com.cjcrafter.openai.chat.ChatMessage
import com.cjcrafter.openai.chat.ChatMessage.Companion.toSystemMessage
import com.cjcrafter.openai.chat.ChatMessage.Companion.toUserMessage
import com.cjcrafter.openai.chat.chatRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import java.io.IOException
import java.lang.RuntimeException


class HomeActivity : ComponentActivity() {
    val isMetric = true
    val OPENAI_KEY = "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"
    var commuteOption : String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val API_KEY = "309135f39bd78ae1fb761b1051bdc5b4"
    private lateinit var weatherData: JsonObject
    class gptExe : GPTExecutor {
        override fun executeGPT(weatherData: JsonObject, activity: HomeActivity) {
            val openAI = OpenAI.builder().apiKey(activity.OPENAI_KEY).build()
            val request = chatRequest { model("gpt-3.5-turbo").addMessage("Help the user with their problem.".toSystemMessage()) }
            val messages: MutableList<ChatMessage> = request.messages
            val unitFull = if (activity.isMetric) "celsius" else "fahrenheit"
            val requestInfo = ("Given feel-like temperature " + weatherData.getAsJsonObject("main").get("feels_like").toString() + " degree " + unitFull + "and that I'm commuting through" + activity.commuteOption + ", recommend a set of outfit for me. For each choice just give me the recommendation for each part to wear, no need to have acknowledgement words, justify the reason and any signaling words like option before recommendation for each part. Respond in less than 30 words").toUserMessage()
            messages.add(requestInfo)
            try {
                activity.runOnUiThread(Runnable {
                    val optionView = activity.findViewById<HorizontalScrollView>(R.id.optionView)
//                    if (optionView.childCount == 0) {
                    if (optionView.childCount > 0) {
                        optionView.removeAllViews()
                    }
                    val optionContainer = LinearLayout(activity)
                    for (i in 1..3) {
                        val completion = openAI.createChatCompletion(request)[0]
                        println("Ans: " + completion.message.content)
                        optionContainer.orientation = LinearLayout.HORIZONTAL
                        optionContainer.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        val optioni = LinearLayout(activity)
                        optioni.orientation = LinearLayout.VERTICAL
                        val layoutParams = LinearLayout.LayoutParams(600, 700)
                        optioni.layoutParams = layoutParams
                        (optioni.layoutParams as LinearLayout.LayoutParams).marginStart = 100
                        (optioni.layoutParams as LinearLayout.LayoutParams).marginEnd = 100
                        optioni.background =
                            ContextCompat.getDrawable(activity, R.drawable.option_background)
                        val optionTitle = TextView(activity)
                        optionTitle.text = "Option " + i.toString()
                        optionTitle.typeface =
                            Typeface.createFromAsset(activity.assets, "bagnard.otf")
                        optionTitle.setTextColor(Color.parseColor("#121212"))
                        optionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                        optionTitle.gravity = Gravity.CENTER_HORIZONTAL
                        optionTitle.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        (optionTitle.layoutParams as LinearLayout.LayoutParams).bottomMargin =
                            50
                        (optionTitle.layoutParams as LinearLayout.LayoutParams).topMargin = 20
                        optioni.addView(optionTitle)
                        val optionBody = TextView(activity)
                        optionBody.gravity = Gravity.START
                        optionBody.text = completion.message.content
                        optionBody.typeface =
                            Typeface.createFromAsset(activity.assets, "bagnard.otf")
                        optionBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                        optionBody.gravity = Gravity.CENTER_HORIZONTAL
                        optioni.addView(optionBody)
                        optionContainer.addView(optioni)
                    }
                    optionView.addView(optionContainer)
                    val progressBar = activity.findViewById<ProgressBar>(R.id.progressBar)
                    progressBar.visibility = View.INVISIBLE
                })
            } catch (e: Exception) {
                println(e.message)
            }


        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), 0)
        }


        class httpListenerClass : HttpListener {
            override fun onGetWeatherSuccess(res: String, activity: HomeActivity) {
                    activity.runOnUiThread {
                        val gson = Gson()
                        weatherData = gson.fromJson(res, JsonObject::class.java)
                        val weatherDes = activity.findViewById<TextView>(R.id.weatherDescription)
                        println(weatherData.getAsJsonObject("main").get("feels_like").toString())
                        val unit = if (isMetric) "C" else "F";
                        weatherDes.text =
                            "Today it feels " + weatherData.getAsJsonObject("main").get("feels_like").toString() + "°" + unit +" outside."
                    }
            }
        }
        val httpListener = httpListenerClass()
        getWeather(httpListener, this)
        val spinner = findViewById<Spinner>(R.id.conditionSpinner)
        if (spinner != null) {
            val options = resources.getStringArray(R.array.Languages)
            spinner.adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, options)
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
//                    Toast.makeText(this@HomeActivity,
//                         "Select " + options[position], Toast.LENGTH_SHORT).show()
                    if (position == 0) {
                        commuteOption = null
                    } else {
                        commuteOption = options[position]
                        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.visibility = View.VISIBLE
//                        runBlocking {
                        lifecycleScope.launch {
                            val res = withContext(Dispatchers.IO) {
                                getGPTRes()
                            }
                        }
//                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                    Toast.makeText(this@HomeActivity,
                        "Nothing selected!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private suspend fun getGPTRes() : Boolean {
        val gptExecutor = gptExe()
        gptExecutor.executeGPT(weatherData, this)
        return false
    }

    private fun getWeather(httpListener: HttpListener, activity: HomeActivity) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    val lat = location.latitude
                    val lon = location.longitude
                    currentLocation = location
//                    Toast.makeText(this, lat.toString() + " " + lon.toString(), Toast.LENGTH_SHORT).show()
                    val unit = if (isMetric) "metric" else "imperial"
                    val url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat.toString() + "&lon=" + lon.toString() + "&units=" + unit + "&appid=" + API_KEY
                    println(url)
                    val request = Request.Builder().url(url).build()
                    val client = OkHttpClient()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("failed")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.body != null) {
                                val res = response.body?.string()
                                println(res)
                                httpListener.onGetWeatherSuccess(res!!, activity)
                            }
                        }
                    })
                }
            }
    }

    interface HttpListener {
        fun onGetWeatherSuccess(res: String, activity: HomeActivity)
    }

    interface GPTExecutor {
        fun executeGPT(weatherData: JsonObject, activity: HomeActivity)
    }

    fun backPress(view: View?) {
        onBackPressedDispatcher.onBackPressed()
    }
}