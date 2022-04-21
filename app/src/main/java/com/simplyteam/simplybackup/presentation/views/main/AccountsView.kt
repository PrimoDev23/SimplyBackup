package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.presentation.viewmodels.main.AccountsViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon

@Composable
fun AccountsView(
    paddingValues: PaddingValues,
    viewModel: AccountsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            state = viewModel.ListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.GetAccounts()) { account ->
                AccountItem(
                    account = account,
                    deleteAccount = {
                        viewModel.DeleteAccount(account)
                    }
                )
            }
        }
    }
}

@Composable
fun AccountItem(
    account: Account,
    deleteAccount: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionIcon(
                connectionType = account.Type
            )

            Text(
                modifier = Modifier
                    .padding(16.dp, 0.dp),
                text = account.Username
            )
        }

        Column {
            IconButton(
                onClick = {
                    menuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        id = R.string.More
                    )
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(224.dp),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("DeleteMenuItem"),
                    onClick = {
                        deleteAccount()

                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        24.dp,
                        8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(
                            id = R.string.Delete
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Delete
                        )
                    )
                }
            }
        }
    }
}