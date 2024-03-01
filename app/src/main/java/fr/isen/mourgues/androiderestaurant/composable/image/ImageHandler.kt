package fr.isen.mourgues.androiderestaurant.composable.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import fr.isen.mourgues.androiderestaurant.R

@Composable
fun ImageHandler(images: List<String>, imageSize: Int = 200) {
    val placeholderImage = images.getOrNull(1)

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(images[0])
            .apply(fun ImageRequest.Builder.() {
                crossfade(true)
                size(imageSize)
                memoryCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
            }).build(),
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = if (placeholderImage.isNullOrBlank()) {
            painterResource(R.drawable.ic_launcher_background)
        } else {
            rememberAsyncImagePainter(images[1])
        }
    )

    Image(
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .size(imageSize.dp),
    )
}
