package me.lulu.twonlinediscordbot.classes

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.commands.converters.*
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.channel.createVoiceChannel
import dev.kord.core.behavior.channel.editRolePermission
import dev.kord.core.behavior.createCategory
import dev.kord.core.behavior.createRole
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import kotlinx.coroutines.flow.collect
import me.lulu.twonlinediscordbot.extensions.getCategoryByName
import me.lulu.twonlinediscordbot.extensions.getRoleByName
import me.lulu.twonlinediscordbot.extensions.makeTeacherSpeakOnly
import me.lulu.twonlinediscordbot.extensions.makeTeacherViewOnly
import java.awt.Color

class ClassModule(bot: ExtensibleBot) : Extension(bot) {
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

            command(ClassModule::CreateClassArgs) {
                name = "create"
                description = "創建班級"


                action {
                    val className = arguments.grade
                    val levelName = arguments.levelName
                    guild!!.createClassSet(className, levelName)
                    message.respond("$className 創立完成！")
                }
            }

            command(ClassModule::DeleteClassArgs) {
                name = "delete"
                description = "刪除班級"

                action {
                    val name = arguments.grade
                    val g = guild!!

                    val role = g.getRoleByName(name)

                    if (role === null) {
                        message.respond("系統無法找到指定班級。")
                        return@action
                    }

                    role.delete()
                    message.respond("成功刪除班級。")

                    val categoryDelete = g.deleteClassCategoryIfNeed(name)

                    if (categoryDelete)
                        message.respond("偵測到班級擁有頻道，已將其全部清除！")
                }
            }
        }
    }


    class CreateClassArgs : Arguments() {
        val grade by string("班級名稱", "即將做更動的班級");
        val levelName by long("班級年級", "班級屬於的年級")
    }

    class DeleteClassArgs : Arguments() {
        val grade by string("班級名稱", "即將做更動的班級");
    }
}

private suspend fun Guild.deleteClassCategoryIfNeed(name: String): Boolean {
    val category = this.getCategoryByName(name)

    if (category != null) {
        category.channels.collect { it.delete() }
        category.delete()

        return true
    }

    return false
}

private suspend fun Guild.initClassroom() {
    val role = this.createRole {
        name = "老師"
        color = Color(31, 147, 31).kColor

        permissions = Permissions.invoke {
            +Permission.Administrator
        }
    }

    this.getOwner().addRole(role.id)
    this.createTextChannel("新人加入") { position = 0 }.makeTeacherSpeakOnly()

    val manageCategory = this.createCategory("管理").makeTeacherViewOnly()
    manageCategory.createTextChannel("指令區")
}

private suspend fun Guild.createClassSet(className: String, levelName: Long) {
    val guild = this

    val classRole = guild.createRole {
        name = className
    }

    val gradeRole = guild.getRoleByName("$levelName 年級") ?: guild.createRole {
        name = "$levelName 年級"
    }

    val category = guild.createCategory(className) {
        addRoleOverwrite(guild.id) {
            denied += Permission.ViewChannel
        }
        addRoleOverwrite(gradeRole.id) {
            allowed += Permission.ViewChannel
        }
    }

    val channel = category.createTextChannel("公告區")
    val role = channel.getPermissionOverwritesForRole(guild.everyoneRole.id)

    channel.editRolePermission(guild.everyoneRole.id) {
        channel.getPermissionOverwritesForRole(guild.everyoneRole.id)?.let {
            allowed
        }
    }
    channel.editRolePermission(guild.everyoneRole.id) {
        allowed = role?.allowed?.copy { } ?: Permissions()
        denied = role?.denied?.copy { } ?: Permissions()

        denied += Permission.SendMessages
    }

    category.createTextChannel("學生聊天")
    category.createVoiceChannel("上課區").makeTeacherSpeakOnly()
}
