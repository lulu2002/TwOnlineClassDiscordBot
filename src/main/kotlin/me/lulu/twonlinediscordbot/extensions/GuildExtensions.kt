package me.lulu.twonlinediscordbot.extensions

import dev.kord.common.entity.ChannelType
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.Category
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull

fun Guild.findRolesByName(name: String, ignoredCase: Boolean = true) =
    this.roles.filter { it.name.equals(name, ignoredCase) }

suspend fun Guild.getRoleByName(name: String, ignoredCase: Boolean = true) =
    findRolesByName(name, ignoredCase).firstOrNull()

suspend fun Guild.getCategoryByName(name: String, ignoredCase: Boolean = true) =
    this.channels.filter {
        it.type === ChannelType.GuildCategory && it.name.equals(name, ignoredCase)
    }.firstOrNull() as Category
