package com.simplyteam.simplybackup.presentation.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
private fun <T> rememberLifecycleAwareFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) = remember {
    flow.flowWithLifecycle(lifecycleOwner.lifecycle)
}

@Composable
fun <T> Flow<T>.collectFlowLifecycleAware(
    initial: T
): State<T> {
    val lifecycleAwareFlow = rememberLifecycleAwareFlow(flow = this)

    return lifecycleAwareFlow.collectAsState(initial)
}