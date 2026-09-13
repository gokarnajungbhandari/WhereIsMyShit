package com.example.whereismyshit.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whereismyshit.data.Shit

@Composable
fun QrButton(
    shit: Shit,
    addQr: (shit:Shit) -> Unit,
    removeQr: (shit:Shit) -> Unit,
    qrAvailableToPrint: () -> Boolean,
    qrAlreadyAdded: (id:Int) -> Boolean
){
    var inQrPrintList = qrAlreadyAdded(shit.id)
    Button(
        enabled = (inQrPrintList || qrAvailableToPrint()),
        onClick = {
            if(inQrPrintList){
                removeQr(shit)
            }
            else{
                addQr(shit)
            }
        },
        modifier = Modifier.widthIn(max= 200.dp),
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp)
    ){
        Text(text =
            if (inQrPrintList){
                "Remove from QR print List"
            }
            else{
                "Add to QR print List"
            },
            maxLines = 2,
            fontSize = 11.sp,
            fontStyle = FontStyle.Normal,
            overflow = TextOverflow.Ellipsis
        )
    }

}