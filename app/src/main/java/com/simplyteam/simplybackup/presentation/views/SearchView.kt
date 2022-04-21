package com.simplyteam.simplybackup.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBox(
    searchText: String,
    search: (String) -> Unit,
    resetSearch: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier
            .padding(
                16.dp,
                0.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(
                        12.dp,
                        0.dp,
                        0.dp,
                        0.dp
                    ),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.Search
                )
            )

            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        8.dp,
                        0.dp,
                        0.dp,
                        0.dp
                    )
                    .testTag("SearchField"),
                value = searchText,
                onValueChange = search,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus(true)
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                cursorBrush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colors.onBackground,
                        MaterialTheme.colors.onBackground,
                    )
                ),
                textStyle = MaterialTheme.typography.subtitle1.copy(MaterialTheme.colors.onBackground)
            )

            IconButton(
                modifier = Modifier
                    .testTag("ClearSearch"),
                onClick = {
                    resetSearch()

                    focusManager.clearFocus(true)
                    keyboardController?.hide()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(
                        id = R.string.Delete
                    )
                )
            }
        }
    }
}