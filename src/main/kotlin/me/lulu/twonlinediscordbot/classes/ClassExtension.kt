package me.lulu.twonlinediscordbot.classes

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.commands.converters.*
import com.kotlindiscord.kord.extensions.commands.converters.impl.RoleConverter
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.channel.createVoiceChannel
import dev.kord.core.behavior.createCategory
import dev.kord.core.behavior.createRole
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import dev.kord.rest.builder.channel.PermissionOverwriteBuilder
import kotlinx.coroutines.flow.first
import me.lulu.twonlinediscordbot.extensions.makeTeacherSpeakOnly
import java.awt.Color

class ClassExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name = "class"

    override suspend fun setup() {

        group {
            name = "class"
            description = "用來管理學生的相關指令"

            check { it.getGuild() != null && it.member?.isOwner() == true }

            command {
                name = "init"
                description = "初始所有設定"

                action {
                    guild!!.initClassroom()
                    message.respond("設定完畢！已建立老師身份組。")
                }
            }

            command(ClassExtension::ClassArgs) {
                name = "create"
                description = "創建班級"


                action {
                    val className = arguments.className
                    guild!!.createClassSet(className)
                    message.respond("$className 創立完成！")
                }
            }

            command(ClassExtension::ClassArgs) {
                name = "delete"
                description = "刪除班級"

                action {
                    message.respond("delete!")
                }
            }
        }
    }


    class ClassArgs : Arguments() {
        val className by string("班級名稱", "即將做更動的班級");
    }
}

private suspend fun Guild.initClassroom() {
    val role = this.createRole() {
        name = "老師"
        color = Color(31, 147, 31).kColor

        permissions = Permissions.invoke {
            +Permission.Administrator
        }
    }

    this.getOwner().addRole(role.id)
    this.createTextChannel("新人加入") { position = 0 }.makeTeacherSpeakOnly()
}

private suspend fun Guild.createClassSet(className: String) {
    val guild = this

    val role = guild.createRole {
        name = className
    }

    val category = guild.createCategory(className) {
        addRoleOverwrite(guild.id) {
            denied += Permission.ViewChannel
        }
        addRoleOverwrite(role.id) {
            allowed += Permission.ViewChannel
        }
    }

    category.createTextChannel("公告區").makeTeacherSpeakOnly()
    category.createTextChannel("學生聊天")
    category.createVoiceChannel("上課區").makeTeacherSpeakOnly()
}
