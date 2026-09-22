package io.github.kobych.sanitly.ui.components

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Popup
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.util.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(
    initialDateMillis: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= calendar.get(Calendar.YEAR)
            }
        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

    LaunchedEffect(datePickerState.selectedDateMillis) { onDateSelected(datePickerState.selectedDateMillis) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            readOnly = true,
            value = selectedDate,
            onValueChange = { onDateSelected(it.toLongOrNull()) },
            label = {
                Text(
                    stringResource(R.string.goal_builder_new_goal_deadline_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker })
                {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date selection")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat(DateFormat.Full.pattern, Locale.getDefault())
    return formatter.format(Date(millis))
}
