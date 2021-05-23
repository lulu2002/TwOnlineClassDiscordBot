package me.lulu.twonlinediscordbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import me.lulu.twonlinediscordbot.classes.ClassExtension

suspend fun main() {
    val bot = ExtensibleBot(System.getenv("TOKEN")) {
        commands {
            defaultPrefix = "*"
            slashCommands = true

        }

        extensions {
            add(::TestExtension)
            add(::ClassExtension)
        }
    }

    bot.start()
}
