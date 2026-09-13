package com.example.whereismyshit.ui
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.whereismyshit.helper.saveResizedImage
import com.example.whereismyshit.helper.createCameraImageUri
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Print
import androidx.compose.ui.draw.scale
import java.io.File

@Composable
fun QrPrintScreen(
    viewModel: ShitViewModel,
    navController: NavController,
    getParentpath: () -> String,
    addContainerToStack: (shit: Shit) -> Unit,
    getLastContainerOrNullFromStack: () -> Shit?,
    addQr: (shit:Shit) -> Unit,
    removeQr: (shit:Shit) -> Unit,
    qrAvailableToPrint: () -> Boolean,
    qrAlreadyAdded: (id:Int) -> Boolean,
    clearAllQrCodes: () -> Unit,
    allSelectedToPrintQrCodes: Set<Shit>
) {
    var name by remember { mutableStateOf("") } //mutableStateOf() makes compose track state of name
    // remember makes compose remember the previous state of
    // name and doesnot redo the assignment when the function
    // is recomposed. name is not a String but mutableState<String>
    // and the value is accessed by name.value but using "by" lets
    // you use name directly insead of dealing with .value
    var isContainer by remember { mutableStateOf(false) }
    var shitBeingEdited by remember {
        mutableStateOf<Shit?>(null)
    }

    // Keeps track of which containers we have entered
    /*val containerStack = remember {
        mutableStateListOf<Shit>()
    }*/

    // The container we are currently inside
    val currentContainer = getLastContainerOrNullFromStack()//containerStack.lastOrNull()


    // Decide which Flow we should display
    val shitFlow = remember(currentContainer?.id) {
        if (currentContainer == null) {
            viewModel.allPrimaryShits
        } else {
            viewModel.getAllShitInsideAContainer(
                currentContainer.id
            )
        }
    }

    // Convert the Flow into Compose state
    val shitList by shitFlow.collectAsState(
        initial = emptyList()
    )

    val context = LocalContext.current

    var selectedImagePath by remember {
        mutableStateOf<String?>(null)
    }

    var cameraFile by remember {
        mutableStateOf<File?>(null)
    }

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri != null) {
                // User selected a picture
                selectedImagePath =
                    saveResizedImage(
                        context = context,
                        sourceUri = uri
                    )
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {
                cameraUri?.let { uri ->

                    selectedImagePath =
                        saveResizedImage(
                            context = context,
                            sourceUri = uri
                        )

                    cameraFile?.delete()
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        // -------------------------
        // HEADER / BACK BUTTON
        // -------------------------

        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = getParentpath()
            )
            Text(
                text = "QR PRINT AREA",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            )

            Spacer(
                modifier = Modifier.width(20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Button(
                        onClick = {
                            navController.navigate("home")
                        }
                    ) {
                        Text("Back")
                    }
                }
                Spacer(
                    modifier = Modifier.width(100.dp)
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = {
                            clearAllQrCodes()
                        }
                    ) {
                        Text("Clear List")
                    }
                }
                Spacer(
                    modifier = Modifier.width(10.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(
                        enabled = allSelectedToPrintQrCodes.isNotEmpty(),
                        onClick = {
                            //navController.navigate("home")
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Print,
                            contentDescription = "Print Qr Codes", // For accessibility
                            modifier = Modifier.size(200.dp,200.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -------------------------
        // LIST
        // -------------------------

        LazyColumn {

            items(
                items = allSelectedToPrintQrCodes.toList(), // Convert to list for LazyColumn
                key = { shit -> shit.id }
            ) { shit ->

                ShitQrPrintRow(
                    shit = shit,

                    goToContainer = {
                        if (shit.isContainer) {
                            addContainerToStack(shit)
                            //containerStack.add(shit)
                        }
                    },

                    getNumOfChildContainers = {
                        viewModel.getNumOfChildContainers(shit.id)
                    },

                    getNumOfChildShits = {
                        viewModel.getNumOfChildShits(shit.id)
                    },
                    getParentStack = {
                        viewModel.getParentStack(it)
                    },
                    addQr = addQr,
                    removeQr = removeQr,
                    qrAvailableToPrint = qrAvailableToPrint,
                    qrAlreadyAdded = qrAlreadyAdded

                )
            }
        }
    }
}