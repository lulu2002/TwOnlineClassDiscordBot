package me.lulu.twonlinediscordbot.extensions

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.entity.PermissionOverwrite


suspend fun GuildChannelBehavior.makeTeacherViewOnly() = denyEveryone { +Permission.ViewChannel }
suspend fun GuildChannelBehavior.makeTeacherSpeakOnly() = denyEveryone {
    +Permission.Speak
    +Permission.SendMessages
}

//todo Might got some update in the future
suspend fun GuildChannelBehavior.makeTeacherBroadcast() = makeTeacherSpeakOnly()

suspend fun GuildChannelBehavior.denyEveryone(block: Permissions.PermissionsBuilder.() -> Unit = {}): GuildChannelBehavior {
    this.addOverwrite(PermissionOverwrite.forEveryone(this.guildId, denied = Permissions.invoke(block)))
    return this
}

suspend fun GuildChannelBehavior.allowEveryone(block: Permissions.PermissionsBuilder.() -> Unit = {}): GuildChannelBehavior {
    this.addOverwrite(PermissionOverwrite.forEveryone(this.guildId, allowed = Permissions.invoke(block)))
    return this
}
