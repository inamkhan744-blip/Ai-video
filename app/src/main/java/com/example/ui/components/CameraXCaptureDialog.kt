package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldSubtle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * CameraXCaptureDialog:
 * Fullscreen in-app camera viewfinder integrating CameraX for capturing hero portraits
 * directly within the story-to-film creation flow.
 */
@Composable
fun CameraXCaptureDialog(
  onDismiss: () -> Unit,
  onPhotoCaptured: (Uri) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  // Permission State
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
    if (!granted) {
      Toast.makeText(context, "Camera permission zaroori hai photo lene ke liye", Toast.LENGTH_SHORT).show()
      onDismiss()
    }
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  // Camera State
  var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) } // Front camera default for selfie hero
  var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
  var showGrid by remember { mutableStateOf(true) }
  var showFaceGuide by remember { mutableStateOf(true) }
  var isCapturing by remember { mutableStateOf(false) }
  var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var cameraError by remember { mutableStateOf<String?>(null) }

  val cameraExecutor: Executor = remember { Executors.newSingleThreadExecutor() }
  val imageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
      .setFlashMode(flashMode)
      .build()
  }

  Dialog(
    onDismissRequest = {
      if (!isCapturing) onDismiss()
    },
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = true,
      dismissOnClickOutside = false
    )
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .testTag("camerax_dialog_surface"),
      color = Color.Black
    ) {
      if (!hasCameraPermission) {
        // Permission Request Prompt
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = QismatGold,
              modifier = Modifier.size(48.dp)
            )
            Text(
              text = "Camera Permission Required",
              color = CinematicWhite,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Apne chehre ki photo khenchne aur film ka Hero banne ke liye camera permission allow karein.",
              color = CinematicTextMuted,
              textAlign = TextAlign.Center,
              fontSize = 13.sp
            )
            Button(
              onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
              colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black)
            ) {
              Text("Allow Camera Permission", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else if (capturedPhotoUri != null) {
        // Captured Photo Preview & Confirmation
        CapturedPhotoPreviewScreen(
          photoUri = capturedPhotoUri!!,
          onRetake = { capturedPhotoUri = null },
          onConfirm = {
            onPhotoCaptured(capturedPhotoUri!!)
            onDismiss()
          }
        )
      } else {
        // Live CameraX Viewfinder
        Box(modifier = Modifier.fillMaxSize()) {
          // Camera Preview View
          AndroidView(
            modifier = Modifier
              .fillMaxSize()
              .testTag("camerax_preview_view"),
            factory = { ctx ->
              val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
              }

              val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
              cameraProviderFuture.addListener({
                try {
                  val cameraProvider = cameraProviderFuture.get()
                  val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                  }

                  val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                  cameraProvider.unbindAll()
                  cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                  )
                } catch (exc: Exception) {
                  Log.e("CameraX", "Use case binding failed", exc)
                  cameraError = "Camera start hone mein masla aaya: ${exc.localizedMessage}"
                }
              }, ContextCompat.getMainExecutor(ctx))

              previewView
            },
            update = { previewView ->
              val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
              cameraProviderFuture.addListener({
                try {
                  val cameraProvider = cameraProviderFuture.get()
                  val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                  }

                  val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                  imageCapture.flashMode = flashMode

                  cameraProvider.unbindAll()
                  cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                  )
                } catch (e: Exception) {
                  Log.e("CameraX", "Update failed", e)
                }
              }, ContextCompat.getMainExecutor(context))
            }
          )

          // Cinematic Rule of Thirds Grid Overlay
          if (showGrid) {
            CameraGridOverlay(modifier = Modifier.fillMaxSize())
          }

          // Face Alignment Oval Overlay for Face-Lock
          if (showFaceGuide) {
            CameraFaceGuideOverlay(modifier = Modifier.fillMaxSize())
          }

          // Cinema Top Letterbox Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(30.dp)
              .background(Color.Black.copy(alpha = 0.75f))
              .align(Alignment.TopCenter)
          )

          // Cinema Bottom Letterbox Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(30.dp)
              .background(Color.Black.copy(alpha = 0.75f))
              .align(Alignment.BottomCenter)
          )

          // Top Action Header Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 36.dp)
              .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Close Button
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .testTag("camerax_close_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Camera",
                tint = CinematicWhite,
                modifier = Modifier.size(20.dp)
              )
            }

            // Mode Indicator
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(QismatEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "CINEMATIC HERO CAPTURE",
                  color = QismatGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }

            // Flash Mode Toggle
            IconButton(
              onClick = {
                flashMode = when (flashMode) {
                  ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                  ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                  else -> ImageCapture.FLASH_MODE_OFF
                }
                imageCapture.flashMode = flashMode
              },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .testTag("camerax_flash_toggle")
            ) {
              Icon(
                imageVector = when (flashMode) {
                  ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                  ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                  else -> Icons.Default.FlashOff
                },
                contentDescription = "Toggle Flash",
                tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) QismatGold else CinematicWhite,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // Error banner if any
          cameraError?.let { err ->
            Box(
              modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.Red, RoundedCornerShape(12.dp))
                .padding(16.dp)
            ) {
              Text(
                text = err,
                color = CinematicWhite,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
              )
            }
          }

          // Bottom Capture Controls
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter)
              .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Helper Instruction Label
            Text(
              text = if (showFaceGuide) "Chehra oval circle ke darmiyan rakhein ✨" else "Frame the shot and capture",
              color = CinematicWhite.copy(alpha = 0.85f),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Control Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Toggle Grid / Face Guide
              IconButton(
                onClick = {
                  showFaceGuide = !showFaceGuide
                  showGrid = showFaceGuide
                },
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(if (showFaceGuide) QismatGold.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.6f))
                  .border(1.dp, if (showFaceGuide) QismatGold else CinematicBorder, CircleShape)
                  .testTag("camerax_guide_toggle")
              ) {
                Icon(
                  imageVector = Icons.Default.Face,
                  contentDescription = "Toggle Face Guide",
                  tint = if (showFaceGuide) QismatGold else CinematicWhite,
                  modifier = Modifier.size(24.dp)
                )
              }

              // Shutter Capture Button
              Box(
                modifier = Modifier
                  .size(76.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.2f))
                  .border(3.dp, QismatGold, CircleShape)
                  .clickable(enabled = !isCapturing) {
                    isCapturing = true
                    val photoFile = File(
                      context.cacheDir,
                      "hero_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                      outputOptions,
                      cameraExecutor,
                      object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                          scope.launch(Dispatchers.Main) {
                            isCapturing = false
                            capturedPhotoUri = Uri.fromFile(photoFile)
                          }
                        }

                        override fun onError(exception: ImageCaptureException) {
                          scope.launch(Dispatchers.Main) {
                            isCapturing = false
                            Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                            Toast.makeText(context, "Photo capture fail ho gaya: ${exception.message}", Toast.LENGTH_SHORT).show()
                          }
                        }
                      }
                    )
                  }
                  .padding(6.dp)
                  .testTag("camerax_shutter_button"),
                contentAlignment = Alignment.Center
              ) {
                if (isCapturing) {
                  CircularProgressIndicator(
                    color = QismatGold,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                  )
                } else {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .clip(CircleShape)
                      .background(QismatGold)
                  )
                }
              }

              // Switch Front/Back Camera Lens
              IconButton(
                onClick = {
                  lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.LENS_FACING_BACK
                  } else {
                    CameraSelector.LENS_FACING_FRONT
                  }
                },
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.6f))
                  .border(1.dp, CinematicBorder, CircleShape)
                  .testTag("camerax_switch_lens_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Cameraswitch,
                  contentDescription = "Switch Camera",
                  tint = CinematicWhite,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Screen displayed right after photo is taken, allowing the user to review before using it.
 */
@Composable
private fun CapturedPhotoPreviewScreen(
  photoUri: Uri,
  onRetake: () -> Unit,
  onConfirm: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("captured_photo_preview_screen")
  ) {
    // Captured Image View
    AsyncImage(
      model = photoUri,
      contentDescription = "Captured Hero Portrait",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Top Cinema Letterbox Strip
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(30.dp)
        .background(Color.Black.copy(alpha = 0.8f))
        .align(Alignment.TopCenter)
    )

    // Bottom Cinema Letterbox Strip
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(30.dp)
        .background(Color.Black.copy(alpha = 0.8f))
        .align(Alignment.BottomCenter)
    )

    // Top Status Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 36.dp)
        .align(Alignment.TopCenter),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color.Black.copy(alpha = 0.7f))
          .border(1.dp, QismatEmerald, RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = QismatEmerald,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PHOTO CAPTURED",
            color = QismatEmerald,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color.Black.copy(alpha = 0.7f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Face-Lock AI Ready",
          color = QismatGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Bottom Action Buttons: Retake vs Confirm
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .background(Color.Black.copy(alpha = 0.85f))
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = "Kya yeh photo aapki film ke Hero ke liye theek hai?",
        color = CinematicWhite,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Retake Button
        Button(
          onClick = onRetake,
          colors = ButtonDefaults.buttonColors(
            containerColor = CinematicSurfaceElevated,
            contentColor = CinematicWhite
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .border(1.dp, CinematicBorder, RoundedCornerShape(12.dp))
            .testTag("camerax_retake_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = CinematicWhite,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Retake ↺", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // Use Photo Button
        Button(
          onClick = onConfirm,
          colors = ButtonDefaults.buttonColors(
            containerColor = QismatGold,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("camerax_use_photo_button")
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Use as Hero ✅", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
      }
    }
  }
}

/**
 * Cinematic rule-of-thirds grid line overlay
 */
@Composable
private fun CameraGridOverlay(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val stroke = 1.dp.toPx()
    val gridColor = Color.White.copy(alpha = 0.25f)

    // Vertical lines (1/3 and 2/3)
    val oneThirdW = size.width / 3f
    val twoThirdW = size.width * 2f / 3f
    drawLine(
      color = gridColor,
      start = Offset(oneThirdW, 0f),
      end = Offset(oneThirdW, size.height),
      strokeWidth = stroke
    )
    drawLine(
      color = gridColor,
      start = Offset(twoThirdW, 0f),
      end = Offset(twoThirdW, size.height),
      strokeWidth = stroke
    )

    // Horizontal lines (1/3 and 2/3)
    val oneThirdH = size.height / 3f
    val twoThirdH = size.height * 2f / 3f
    drawLine(
      color = gridColor,
      start = Offset(0f, oneThirdH),
      end = Offset(size.width, oneThirdH),
      strokeWidth = stroke
    )
    drawLine(
      color = gridColor,
      start = Offset(0f, twoThirdH),
      end = Offset(size.width, twoThirdH),
      strokeWidth = stroke
    )
  }
}

/**
 * Oval guide overlay to help users align their face for consistent Face-Lock AI processing.
 */
@Composable
private fun CameraFaceGuideOverlay(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val ovalWidth = size.width * 0.62f
    val ovalHeight = size.height * 0.42f
    val left = (size.width - ovalWidth) / 2f
    val top = (size.height - ovalHeight) / 2.3f

    val goldColor = Color(0xFFFFD700).copy(alpha = 0.75f)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)

    drawOval(
      color = goldColor,
      topLeft = Offset(left, top),
      size = Size(ovalWidth, ovalHeight),
      style = Stroke(
        width = 2.dp.toPx(),
        pathEffect = dashEffect
      )
    )
  }
}
