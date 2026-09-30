package com.example.cta_5

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.io.File
import com.example.cta_5.ui.theme.CTA_5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CTA_5Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ModernPhotoGalleryApp(lifecycleOwner = this@MainActivity)
                }
            }
        }
    }
}

private enum class AppScreen { GALLERY, CAMERA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ModernPhotoGalleryApp(lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current

    var currentScreen by rememberSaveable {
        mutableStateOf(AppScreen.GALLERY)
    }

    var photos by remember {
        mutableStateOf(PhotoRepository.loadPhotos(context))
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                currentScreen = AppScreen.CAMERA
            } else {
                message = "Camera permission is required to take photos."
            }
        }

    LaunchedEffect(message) {
        message?.let {
            snackBarHostState.showSnackbar(it)
            message = null
        }
    }

    when (currentScreen) {
        AppScreen.GALLERY -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackBarHostState) },
                topBar = { TopAppBar(title = { Text("Modern Photo Gallery") }) },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        if (hasCameraPermission(context)) {
                            currentScreen = AppScreen.CAMERA
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Text("Camera")
                    }
                }
            ) { paddingValues ->
                GalleryScreen(
                    photos = photos,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        AppScreen.CAMERA -> {
            CameraScreen(
                lifecycleOwner = lifecycleOwner, onPhotoSaved = { photo ->
                    photos = PhotoRepository.loadPhotos(context)
                    message = "Photo saved: ${photo.name}"
                    currentScreen = AppScreen.GALLERY
                },
                onError = { error -> message = error },
                onBack = { currentScreen = AppScreen.GALLERY })
        }
    }
}

@Composable
fun GalleryScreen(photos: List<File>, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    if (photos.isEmpty()) {
        Column(modifier = modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "Your gallery is empty.", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Press the Camera button to capture your first photo.", modifier = Modifier.padding(top = 8.dp))
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Text( text ="${photos.size} photos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp))
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 120.dp), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = photos, key = { it.absolutePath }) {
                    photo -> PhotoGridItem(photo = photo, context = context)
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(photo: File, context: Context) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f), shape = RoundedCornerShape(12.dp)) {
        AsyncImage(model = ImageRequest.Builder(context).data(photo).crossfade(true).build(), contentDescription = "Gallery photo ${photo.name}", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(lifecycleOwner: LifecycleOwner, onPhotoSaved: (File) -> Unit, onError: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current

    var isCapturing by remember { mutableStateOf(false) }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }


    /*
     * Bind CameraX to the Activity lifecycle.
     * CameraX automatically stops when the
     * lifecycle is no longer active.
     */
    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose { cameraController.unbind() }
    }

    Scaffold(topBar = {
        TopAppBar( title = { Text("Take Photo") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
        )
    }) { paddingValues -> Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView( factory = { viewContext -> PreviewView(viewContext).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                controller = cameraController }},
                            modifier =  Modifier.fillMaxSize())
            }
            Row( modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                Button(enabled = !isCapturing,
                    onClick = { isCapturing = true
                        takePhoto(context = context, cameraController = cameraController, onSuccess = {
                            photo -> isCapturing = false
                                onPhotoSaved(photo)
                        }, onError = {
                                error -> isCapturing = false
                                onError(error)
                            }
                        )
                    }
                ) {
                    Text(if (isCapturing) "Saving..." else "Capture Photo")
                }
            }
        }
    }
}

private fun takePhoto(context: Context, cameraController: LifecycleCameraController, onSuccess: (File) -> Unit, onError: (String) -> Unit) {
    val photoFile = PhotoRepository.createPhotoFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    cameraController.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object :ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(photoFile)
            }
            override fun onError(exception: ImageCaptureException) {
                /*
                 * Delete an incomplete file
                 * if the capture failed.
                 */
                if (photoFile.exists()) { photoFile.delete() }
                onError(exception.message ?: "Photo capture failed.")
            }
        }
    )
}

private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
