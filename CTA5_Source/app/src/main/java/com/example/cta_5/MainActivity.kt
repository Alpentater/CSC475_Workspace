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

//MainActivity is always the entry point for applications.
class MainActivity : ComponentActivity() {
    //This function is always called when the 'MainActivity' is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //'SetContent' is basically how we tell android we are using Jetpack Compose.
        setContent {
            //Applying the custom Material theme for this application.
            CTA_5Theme {
                //The 'Surface' function defines the main background/container for the UI.
                Surface(modifier = Modifier.fillMaxSize()) {
                    /* Starts the main photo gallery UI.
                    The Activity is also passed as the LifecycleOwner so
                    CameraX can automatically follow the Activity lifecycle. */
                    ModernPhotoGalleryApp(lifecycleOwner = this@MainActivity)
                }
            }
        }
    }
}

//Enum for the two screens that the user would be on when using the app.
private enum class AppScreen { GALLERY, CAMERA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ModernPhotoGalleryApp(lifecycleOwner: LifecycleOwner) {
    //This 'context' is needed for permissions, file access, etc.
    val context = LocalContext.current

    //Stores which screen is currently being displayed.
    //'rememberSaveable' allows the value to survive configuration changes, such as rotating the device.
    var currentScreen by rememberSaveable {
        mutableStateOf(AppScreen.GALLERY)
    }

    //Loads the photos that have already been saved by the application.
    //When this variable changes, Compose automatically updates the gallery.
    var photos by remember {
        mutableStateOf(PhotoRepository.loadPhotos(context))
    }

    //Stores a temporary message that can be displayed to the user.
    var message by remember {
        //null here means there is currently no message to display.
        mutableStateOf<String?>(null)
    }

    //Controls Material Design snackbar messages.
    val snackBarHostState = remember { SnackbarHostState() }

    //request camera permission at runtime.
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            //If permission was approved...
            if (granted) {
                //... open the camera screen.
                currentScreen = AppScreen.CAMERA
            } else { //If permission was not approved...
                //...display the following message:
                message = "Camera permission is required to take photos."
            }
        }
    //runs whenever the message value changes, like the message directly above.
    //If a message exists, it is shown using a snackbar.
    LaunchedEffect(message) {
        message?.let {
            snackBarHostState.showSnackbar(it)
            //Resetting the message after displaying it
            message = null
        }
    }

    //What screen is being displayed.
    when (currentScreen) {
        AppScreen.GALLERY -> {
            //'Scaffold' provides the standard Material Design screen structure.
            Scaffold(
                //Snackbar support brought in here
                snackbarHost = { SnackbarHost(snackBarHostState) },
                //App's title bar
                topBar = { TopAppBar(title = { Text("Modern Photo Gallery") }) },
                //Button that opens the camera.
                floatingActionButton = {
                    //When you click the button...
                    FloatingActionButton(onClick = {
                        //Check whether camera permission has been granted.
                        if (hasCameraPermission(context)) {
                            //Open the camera immediately if you have the permission
                            currentScreen = AppScreen.CAMERA
                        } else {
                            //If the permission does not exist... ask here.
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Text("Camera")
                    }
                }
            ) { paddingValues ->
                //The grid of saved photos.
                GalleryScreen(
                    photos = photos,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        AppScreen.CAMERA -> {
            //CameraX camera interface.
            CameraScreen(
                //This is called when a photo is saved...
                lifecycleOwner = lifecycleOwner, onPhotoSaved = { photo ->
                    //...then it adds the image to the gallery! (And shows a confirmation message)
                    photos = PhotoRepository.loadPhotos(context)
                    message = "Photo saved: ${photo.name}"
                    currentScreen = AppScreen.GALLERY
                },
                //Display any camera errors using the snackbar.
                onError = { error -> message = error },
                //Return to the gallery when the user presses Back.
                onBack = { currentScreen = AppScreen.GALLERY })
        }
    }
}

@Composable
fun GalleryScreen(photos: List<File>, modifier: Modifier = Modifier) {
    //Current Android context used by Coil when loading image files.
    val context = LocalContext.current
    //If there are no photos, we show an 'empty-gallery' message instead.
    if (photos.isEmpty()) {
        Column(modifier = modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "Your gallery is empty.", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Press the Camera button to capture your first photo.", modifier = Modifier.padding(top = 8.dp))
        }
    } else { //If there are photos... display the gallery when photos are available.
        Column(modifier = modifier.fillMaxSize()) {
            //Shows the number of photos currently stored.
            Text( text ="${photos.size} photos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp))
            //LazyVerticalGrid displays photos in the grid... adaptive colims and scrolling included!
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 120.dp), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                //A loop that creates a grid item for each photo.
                //each photo path as a unique identifier... thje 'key'.
                items(items = photos, key = { it.absolutePath }) {
                    photo -> PhotoGridItem(photo = photo, context = context)
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(photo: File, context: Context) {
    //Card gives each image a Material Design container with rounded corners.
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f), shape = RoundedCornerShape(12.dp)) {
        //Coil's AsyncImage loads the image file.
        AsyncImage(model = ImageRequest.Builder(context).data(photo).crossfade(true).build(), contentDescription = "Gallery photo ${photo.name}", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(lifecycleOwner: LifecycleOwner, onPhotoSaved: (File) -> Unit, onError: (String) -> Unit, onBack: () -> Unit) {
    //Android Context used to create and configure CameraX.
    val context = LocalContext.current

    //Tracks whether the application is currently saving a photo.
    //This prevents the capture button from being pressed multiple times.
    var isCapturing by remember { mutableStateOf(false) }

    //The CameraX controller.
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            //Only enable image capture because video recording.
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }


    //Bind CameraX to the Activity lifecycle.This way, CameraX automatically stops when the lifecycle is no longer active.
    DisposableEffect(lifecycleOwner) {
        //Connects CameraX to the Activity lifecycle.
        cameraController.bindToLifecycle(lifecycleOwner)
        //Unconnects CameraX when this Composable leaves the screen.
        onDispose { cameraController.unbind() }
    }

    //Material Design layout for the camera screen.
    Scaffold(topBar = {
        TopAppBar( title = { Text("Take Photo") },
            //Making the back button that takes the user back to the gallery.
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
        )
    }) { paddingValues -> Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            //Camera preview fills most of the screen.
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

//Captures a picture using CameraX and then saves the image to a File.
private fun takePhoto(context: Context, cameraController: LifecycleCameraController, onSuccess: (File) -> Unit, onError: (String) -> Unit) {
    //where the image will be stored.
    val photoFile = PhotoRepository.createPhotoFile(context)
    //Telling CameraX which file should receive the captured image.
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    //Capturing the photo asynchronously.
    cameraController.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object :ImageCapture.OnImageSavedCallback {
            //Called when CameraX successfully saves the photograph.
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(photoFile)
            }
            //If anthing goes wrong... fallback here
            override fun onError(exception: ImageCaptureException) {
                //Delete an incomplete file if the capture failed.
                if (photoFile.exists()) { photoFile.delete() }
                onError(exception.message ?: "Photo capture failed.")
            }
        }
    )
}

//Here, we vhecks whether the user has already granted permission to use the camera.
private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
