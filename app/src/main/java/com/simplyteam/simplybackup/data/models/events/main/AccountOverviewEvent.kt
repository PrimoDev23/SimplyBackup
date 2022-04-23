package com.simplyteam.simplybackup.data.models.events.main

import com.simplyteam.simplybackup.data.models.Account

sealed class AccountOverviewEvent {
    data class OnDeleteAccount(val Account: Account) : AccountOverviewEvent()
}
