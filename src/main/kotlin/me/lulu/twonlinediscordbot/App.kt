package me.lulu.twonlinediscordbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import me.lulu.twonlinediscordbot.classes.ClassModule

suspend fun main() {
    val bot = ExtensibleBot(System.getenv("TOKEN")) {
        commands {
            defaultPrefix = "*"
            slashCommands = true

        }

        extensions {
            add(::TestExtension)
            add(::ClassModule)
        }
    }

    bot.start()
}
