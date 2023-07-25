package com.ecosystem.mobile.reparation.ui.odata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sap.cloud.mobile.flows.compose.ui.FlowComposeTheme

class ODataActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowComposeTheme {
                ODataApp()

            }
        }
    }
}

@Composable
fun ODataApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    ODataNavHost(modifier = modifier, navController = navController)
}

