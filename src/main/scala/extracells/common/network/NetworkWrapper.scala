package extracells.common.network

import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.Packet
import net.minecraft.world.World

object NetworkWrapper {
  private val CHANNEL: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("extracells")
  private var currentDiscriminator: Int = 0

  def registerMessages(): Unit = {
    for ( messageType <- MessageEnum.values) {
      CHANNEL.registerMessage(messageType.handlerClass,
        messageType.messageClass, currentDiscriminator, messageType.side)
      currentDiscriminator += 1
    }
  }

  def sendToServer(message: AbstractPacketBase) : Unit =
    CHANNEL.sendToServer(message)

  def sendToAll(message: AbstractPacketBase): Unit =
    CHANNEL.sendToAll(message)

  def sendToDimension(message: AbstractPacketBase, dimId: Int): Unit =
    CHANNEL.sendToDimension(message, dimId)

  def sendToPlayersAround(message: AbstractPacketBase, point: TargetPoint): Unit =
    CHANNEL.sendToAllAround(message, point)

  def sendToPlayer(message: AbstractPacketBase, player: EntityPlayerMP) : Unit =
    CHANNEL.sendTo(message, player)

  def sendPacketToWorld(packet: Packet, world: World): Unit = {
    for (player: Object <- world.playerEntities if (player.isInstanceOf[EntityPlayerMP]))
      player.asInstanceOf[EntityPlayerMP].playerNetServerHandler.sendPacket(packet)
  }
}