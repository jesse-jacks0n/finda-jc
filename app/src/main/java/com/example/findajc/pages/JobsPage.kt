package com.example.findajc.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun JobsPage(modifier: Modifier = Modifier) {
    Text("Jobs Page", modifier = modifier)
    //ads view
    // Add AdMob AdView
    //val context = LocalContext.current
//    AndroidView(
//        modifier = modifier,
//       // factory = { ctx ->
////            AdView(ctx).apply {
////                AdSize.BANNER
////                adUnitId = "ca-app-pub-1926283539123501/5254521320" // Use your Ad Unit ID
////                loadAd(AdRequest.Builder().build())
////            }
//        }
//    )
//    AndroidView(
//        modifier = Modifier.fillMaxWidth(),
//        factory = { context ->
//            // on below line specifying ad view.
//            AdView(context).apply {
//                // on below line specifying ad size
//                //adSize = AdSize.BANNER
//                // on below line specifying ad unit id
//                // currently added a test ad unit id.
//                setAdSize(AdSize.BANNER)
//                adUnitId = "ca-app-pub-1926283539123501/5254521320"
//                // calling load ad to load our ad.
//                loadAd(AdRequest.Builder().build())
//            }
//        }
//    )
}