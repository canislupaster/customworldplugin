package com.thomas.customworld;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import scala.com.thomas.customworld.discord.DiscordBot;

public class DiscordBotJava {
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        DiscordBot.evDiscord(event);
    }
}
