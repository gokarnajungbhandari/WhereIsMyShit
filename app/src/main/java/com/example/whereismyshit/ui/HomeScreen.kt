package com.example.whereismyshit.ui
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.whereismyshit.helper.getContainerIdFromQr
import com.example.whereismyshit.helper.printBitmap
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ShitViewModel,
    changeStack: (parentStack: List<Shit>) -> Unit,
    navController: NavController,
    addQr: (shit:Shit) -> Unit,
    removeQr: (shit:Shit) -> Unit,
    qrAvailableToPrint: () -> Boolean,
    qrAlreadyAdded: (id:Int) -> Boolean
) {
   Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(16.dp)
   ){
       var expanded by rememberSaveable { mutableStateOf(false) }
       var searchResults : List<Shit> = emptyList()
       var textFieldState: TextFieldState = remember{
           TextFieldState("")
           }
       val context = LocalContext.current

       val options = GmsBarcodeScannerOptions.Builder()
           .setBarcodeFormats(
               Barcode.FORMAT_QR_CODE
           )
           .enableAutoZoom()
           .build()

       /*val scanner = remember {
           GmsBarcodeScanning.getClient(
               context,
               options
           )
       }
       val scanLauncher =
           rememberLauncherForActivityResult(
               contract = ScanContract()
           ) { result ->

               if (result.contents != null) {
                   val qrValue = result.contents

                   println("QR = $qrValue")
               }
           }
       val scope = rememberCoroutineScope()*/
       val scope = rememberCoroutineScope()

       val scanLauncher =
           rememberLauncherForActivityResult(
               contract = ScanContract()
           ) { result ->

               val qrValue =
                   result.contents ?: return@rememberLauncherForActivityResult

               val containerId =
                   getContainerIdFromQr(qrValue)
                       ?: return@rememberLauncherForActivityResult

               scope.launch {

                   val shit =
                       viewModel.getShit(containerId)
                           ?: return@launch

                   if (!shit.isContainer) {
                       return@launch
                   }

                   val stack =
                       viewModel.getParentStack(shit) + shit

                   changeStack(stack)

                   navController.navigate("containers")
               }
           }
       Spacer(
           modifier = Modifier.height(50.dp)
       )
       Box(
           modifier = Modifier
               //.fillMaxSize()
               .semantics { isTraversalGroup = true }
       ) {
           SearchBar(
               modifier = Modifier
                   .align(Alignment.TopCenter)
                   .semantics { traversalIndex = 0f },
               inputField = {
                   SearchBarDefaults.InputField(
                       query = textFieldState.text.toString(),
                       onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                       onSearch = {
                           viewModel.performSearchText(textFieldState.text.toString())
                           //expanded = false
                       },
                       expanded = expanded,
                       onExpandedChange = { expanded = it },
                       placeholder = { Text("Search") }
                   )
               },
               expanded = expanded,
               onExpandedChange = { expanded = it },
           ) {
               // Display search results in a scrollable column
               Column(Modifier.verticalScroll(rememberScrollState())
                   .background(color = MaterialTheme.colorScheme.background)
               ) {
                   viewModel.searchResults.forEach { result ->
                       ShitSearchResultRow(
                           shit = result,

                           goToParentContainer = { parentStack ->
                               if (result.isContainer) {
                                   changeStack(parentStack)
                                   //containerStack.add(shit)
                               }
                               navController.navigate("containers")
                           },
                           getParentStack = {
                               viewModel.getParentStack(it)
                           },
                           getNumOfChildContainers = {
                               viewModel.getNumOfChildContainers(result.id)
                           },

                           getNumOfChildShits = {
                               viewModel.getNumOfChildShits(result.id)
                           },
                           addQr = addQr,
                           removeQr = removeQr,
                           qrAvailableToPrint = qrAvailableToPrint,
                           qrAlreadyAdded = qrAlreadyAdded
                       )
                       /*ListItem(
                           headlineContent = { Text(result) },
                           modifier = Modifier
                               .clickable {
                                   textFieldState.edit { replace(0, length, result) }
                                   expanded = false
                               }
                               .fillMaxWidth()
                       )*/
                   }
               }
           }

       }
       Spacer(
           modifier = Modifier.height(20.dp)
       )
       IconButton(
           onClick = {
               /*scanner.startScan()
                   .addOnSuccessListener { barcode ->

                       val qrValue = barcode.rawValue

                       if (qrValue != null) {

                           val containerId =
                               getContainerIdFromQr(qrValue)
                           scope.launch {
                               if (containerId != null) {
                                   viewModel.getShit(containerId)?.let{ shit ->
                                       val stack = viewModel.getParentStack(shit)
                                       changeStack(stack)
                                       navController.navigate("containers")
                                   }
                               }
                           }

                       }
                   }*/


               /*scanner.startScan()

                   .addOnSuccessListener { barcode ->

                       val qrValue =
                           barcode.rawValue
                               ?: return@addOnSuccessListener

                       Toast.makeText(
                           context,
                           "QR = $qrValue",
                           Toast.LENGTH_LONG
                       ).show()

                       val containerId =
                           getContainerIdFromQr(qrValue)
                               ?: return@addOnSuccessListener

                       scope.launch {

                           val shit =
                               viewModel.getShit(containerId)
                                   ?: return@launch

                           if (!shit.isContainer) {
                               return@launch
                           }

                           val stack =
                               viewModel.getParentStack(shit) + shit

                           changeStack(stack)

                           navController.navigate("containers")
                       }
                   }

                   .addOnCanceledListener {

                       Toast.makeText(
                           context,
                           "Scan cancelled",
                           Toast.LENGTH_SHORT
                       ).show()
                   }

                   .addOnFailureListener { exception ->

                       Toast.makeText(
                           context,
                           "Scanner error: ${exception.message}",
                           Toast.LENGTH_LONG
                       ).show()
                   }*/
               val options = ScanOptions().apply {
                   setDesiredBarcodeFormats(
                       ScanOptions.QR_CODE
                   )

                   setPrompt("Scan container QR code")
                   setBeepEnabled(false)
                   setOrientationLocked(false)
               }

               scanLauncher.launch(options)
           },
           modifier = Modifier.align(Alignment.CenterHorizontally)
       ) {
           Icon(
               imageVector = Icons.Filled.QrCodeScanner,
               contentDescription = "Scan Qr Code", // For accessibility
               modifier = Modifier.size(200.dp,200.dp),
               tint = MaterialTheme.colorScheme.primary
           )
       }

       Spacer(
           modifier = Modifier.height(20.dp)
       )
       Box(
           modifier = Modifier
               .fillMaxWidth()
               .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
               .padding(10.dp)
               .clickable(onClick = {navController.navigate("containers") }),

           contentAlignment = Alignment.Center
       ) {
           Text(
               text = "Containers",
               style = MaterialTheme.typography.headlineMedium,
               color = MaterialTheme.colorScheme.primary,
               fontSize = 24.sp,
               fontFamily = FontFamily.Monospace
           )
       }
       Spacer(
           modifier = Modifier.height(16.dp)
       )

       Box(
           modifier = Modifier.fillMaxHeight(),
           contentAlignment = Alignment.BottomCenter
       ){
           Button(onClick = {
               navController.navigate("printQR")
           }) {
               Text("Print QR Codes")
           }
       }
   }
}