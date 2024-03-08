package com.example.datadrobe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.decorView.windowInsetsController!!.hide(
//            android.view.WindowInsets.Type.statusBars()
//        )
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        setContentView(R.layout.landing_page)
//        val db = Firebase.firestore
//        val user = hashMapOf(
//            "email" to "test@example.com",
//            "password" to "123456"
//        )
//        db.collection("users")
//            .document("test@example.com")
//            .set(user)
//            .addOnSuccessListener { docRef ->
//                Log.e("lalala", "Doc id ${docRef}")
//            }
//            .addOnFailureListener {e ->
//                Log.e("wuwuwu", "Doc id ${e}")
//            }
    }

    fun loginClicked(view: View?) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun signupClicked(view: View?) {
        val intent = Intent(this, Signup::class.java)
        startActivity(intent)
    }
}


//@Composable
//fun Greeting(name: String, font: FontFamily, modifier: Modifier = Modifier) {
////    Text(
////            text = "Hello $name!",
////            modifier = modifier,
////            fontFamily = font
////    )
//    Image(
//        painter = painterResource(id = R.drawable.clothing_doodle_2),
//        contentDescription = "image description",
//        contentScale = ContentScale.None,
//        modifier = Modifier
//            .padding(0.dp)
//            .width(153.71637.dp)
//            .height(163.68642.dp)
//    )
//
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    DatadrobeTheme {
//        Greeting("Android")
//    }
//}