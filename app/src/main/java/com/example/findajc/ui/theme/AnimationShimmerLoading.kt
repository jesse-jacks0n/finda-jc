//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.animation.core.repeatable
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.core.InfiniteRepeatableSpec
//import androidx.compose.animation.core.AnimationConstants
//import androidx.compose.runtime.remember
//
//@Composable
//fun SkeletonLoader(modifier: Modifier = Modifier) {
//    val transition = remember { Animatable(0f) }
//    val shimmerAnimation = remember {
//        repeatable(
//            iterations = AnimationConstants.Infinite,
//            animation = tween(durationMillis = 1200)
//        )
//    }
//
//    val shimmer by transition.animateFloatAsState(
//        targetValue = 1f,
//        animationSpec = shimmerAnimation
//    )
//
//    Box(
//        modifier = modifier
//            .background(
//                Brush.linearGradient(
//                    colors = listOf(
//                        Color.LightGray.copy(alpha = 0.2f),
//                        Color.LightGray.copy(alpha = 0.4f),
//                        Color.LightGray.copy(alpha = 0.2f)
//                    ),
//                    startX = shimmer * 300.dp.toPx(),
//                    endX = (shimmer * 300.dp.toPx()) + 300.dp.toPx()
//                ),
//                shape = RoundedCornerShape(16.dp)
//            )
//            .height(100.dp) // Adjust size as needed
//            .fillMaxWidth()
//    )
//}
