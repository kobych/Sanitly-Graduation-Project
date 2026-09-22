package io.github.kobych.sanitly.ui.components.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertDialog(
    alertLabelId: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                Text(text = stringResource(alertLabelId), style = Typography.bodyLarge)
                Spacer(modifier = Modifier.height(Dimens.XL.padding))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                    ) {
                        Text(
                            text = stringResource(R.string.common_cancel),
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            onConfirm()
                        },
                        modifier = Modifier
                    ) {
                        Text(
                            text = stringResource(R.string.common_confirm),
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
