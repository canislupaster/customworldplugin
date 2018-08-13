package scala.com.thomas.customworld.discord

import java.util

import github.scarsz.discordsrv.dependencies.jda.core.entities.MessageChannel
import org.bukkit.Server
import org.bukkit.command.{CommandSender, ConsoleCommandSender}
import org.bukkit.conversations.Conversation
import org.bukkit.permissions.{Permission, PermissionAttachment, PermissionAttachmentInfo}
import org.bukkit.plugin.Plugin

import scala.com.thomas.customworld.CustomCore

class DiscordCommandSender(channel:MessageChannel) extends CommandSender {
  def getChannel: MessageChannel = channel
  override def getServer: Server = CustomCore.server

  def perms = Set("build")

  override def hasPermission(name: String): Boolean = perms contains name
  override def getName: String = "DISCORD"
  override def isOp: Boolean = false

  override def sendMessage(message: String): Unit = channel.sendMessage(message)
  override def sendMessage(messages: Array[String]): Unit = messages foreach (x => channel.sendMessage(x))

  override def hasPermission(perm: Permission): Boolean = perms contains perm.getName
  override def spigot(): CommandSender.Spigot = null
  override def removeAttachment(attachment: PermissionAttachment): Unit = ()
  override def isPermissionSet(perm: Permission): Boolean = true
  override def isPermissionSet(name: String): Boolean = true
  override def setOp(value: Boolean): Unit = ()
  override def getEffectivePermissions: util.Set[PermissionAttachmentInfo] = null
  override def addAttachment(plugin: Plugin): PermissionAttachment = null
  override def addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment = null
  override def addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment = null
  override def addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment = null
  override def recalculatePermissions(): Unit = ()
}
