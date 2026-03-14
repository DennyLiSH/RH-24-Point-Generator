package com.rabbithole.my24points

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rabbithole.my24points.ui.screen.GameScreen
import com.rabbithole.my24points.ui.theme._24PointsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _24PointsTheme {
                GameScreen()
            }
        }
    }
}
