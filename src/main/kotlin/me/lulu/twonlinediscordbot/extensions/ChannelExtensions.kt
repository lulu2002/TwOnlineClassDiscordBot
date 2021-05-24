package me.lulu.twonlinediscordbot.extensions

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.editRolePermission
import dev.kord.core.entity.PermissionOverwrite
import dev.kord.core.entity.channel.CategorizableChannel
import dev.kord.rest.builder.channel.ChannelPermissionModifyBuilder


suspend fun <T : GuildChannelBehavior> T.makeTeacherViewOnly() = denyEveryone { +Permission.ViewChannel }
suspend fun <T : GuildChannelBehavior> T.makeTeacherSpeakOnly() = denyEveryone {
    +Permission.Speak
    +Permission.SendMessages
}

//todo Might got some update in the future
suspend fun <T : GuildChannelBehavior> T.makeTeacherBroadcast() = makeTeacherSpeakOnly()

suspend fun <T : GuildChannelBehavior> T.denyEveryone(block: Permissions.PermissionsBuilder.() -> Unit = {}): T {
    this.editRolePermission(this.getGuild().everyoneRole.id) { }
    return this
}

suspend fun <T : GuildChannelBehavior> T.allowEveryone(block: Permissions.PermissionsBuilder.() -> Unit = {}): T {
    this.editRolePermission(this.getGuild().everyoneRole.id) {
        allowed += Permissions.invoke(block)
    }
    return this
}

suspend fun <T : GuildChannelBehavior> T.appendRolePermission(
    roleId: Snowflake,
    builder: ChannelPermissionModifyBuilder.() -> Unit
) {

    this.editRolePermission(roleId) {

    }
}
