package com.yoon.allergyinfo.composeUI

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.yoon.allergyinfo.R

@Composable
fun DropdownMenuButton(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expended by rememberSaveable { mutableStateOf(false) }
    var buttonText by rememberSaveable { mutableStateOf(options[selectedIndex]) }

    Box(modifier = modifier) {
        Button(
            onClick = { expended = !expended },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.transparent),
                contentColor = colorResource(id = R.color.black)
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(buttonText)
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expended,
            onDismissRequest = { expended = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(index)
                    buttonText = options[index]
                    expended = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}