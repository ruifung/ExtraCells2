package extracells.common.network.packet

import appeng.api.AEApi
import appeng.api.storage.data.{IAEFluidStack, IItemList}
import extracells.common.network.AbstractPacketBase
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fluids.{Fluid, FluidStack}

class PacketFluidStorage extends AbstractPacketBase {
  private var fluidStackList: IItemList[IAEFluidStack] = _
  private var currentFluid: Fluid = _
  this.mode = 0

  def this(currentFluid: Fluid) {
    this()
    this.mode = 1
    this.currentFluid = currentFluid
  }

  def this(player: EntityPlayer, list: IItemList[IAEFluidStack]) {
    this()
    this.mode = 2
    this.fluidStackList = list
  }

  def getFluidList: IItemList[IAEFluidStack] = fluidStackList
  def getCurrentFluid: Fluid = currentFluid

  override def readData(in: ByteBuf): Unit = {
    this.mode match {
      case 1 => this.currentFluid = PacketHelper.readFluid(in)
      case 2 => this.fluidStackList = AEApi.instance.storage.createFluidList
        while (in.readableBytes() > 0) {
          val fluid: Fluid = PacketHelper.readFluid(in)
          val fluidAmt: Long = in.readLong()
          if (fluid == null)
            return
          val stack: IAEFluidStack = AEApi.instance.storage.createFluidStack(new FluidStack(fluid, 1))
          stack.setStackSize(fluidAmt)
          this.fluidStackList.add(stack)
        }
    }
  }

  override def writeData(out: ByteBuf): Unit = {
    this.mode match {
      case 1 => PacketHelper.writeFluid(this.currentFluid, out)
      case 2 => for (stack: IAEFluidStack <- this.fluidStackList) {
          PacketHelper.writeFluid(stack.getFluid, out)
          out.writeLong(stack.getStackSize)
        }
    }
  }
}
