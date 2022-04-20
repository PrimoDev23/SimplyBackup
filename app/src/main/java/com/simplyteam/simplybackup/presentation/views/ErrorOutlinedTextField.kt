package com.simplyteam.simplybackup.presentation.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R

@Composable
fun ErrorOutlinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean,
    errorModifier: Modifier,
    isError: Boolean,
    errorText: String
) {
    OutlinedTextField(
        modifier = modifier,
        label = label,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        isError = isError,
        trailingIcon = {
            if (isError) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_error_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.Error
                    ),
                    tint = MaterialTheme.colors.error
                )
            }
        }
    )

    if (isError) {
        Text(
            modifier = errorModifier
                .padding(
                    12.dp,
                    4.dp,
                    0.dp,
                    0.dp
                ),
            text = errorText,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
    }
}