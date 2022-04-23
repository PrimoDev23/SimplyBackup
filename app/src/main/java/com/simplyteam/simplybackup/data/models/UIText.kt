package com.simplyteam.simplybackup.data.models

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText {
    data class DynamicString(val value: String) : UIText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val formatArgs: Any
    ) : UIText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(
                resId,
                *formatArgs
            )
        }
    }
}
