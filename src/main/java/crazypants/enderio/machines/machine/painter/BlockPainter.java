package crazypants.enderio.machines.machine.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.EveryPaintableRecipe;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPainter extends AbstractMachineBlock<TileEntityPainter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockPainter create(@Nonnull IModObject modObject) {
    BlockPainter painter = new BlockPainter(modObject);
    painter.init();
    return painter;
  }

  private BlockPainter(@Nonnull IModObject modObject) {
    super(modObject, TileEntityPainter.class);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void init() {
    super.init();
    MachineRecipeRegistry.instance.enableRecipeSorting(MachineObject.block_painter.getUnlocalisedName());
    MachineRecipeRegistry.instance.registerRecipe(MachineObject.block_painter.getUnlocalisedName(), new EveryPaintableRecipe());
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileEntityPainter te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerPainter(player.inventory, te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileEntityPainter te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiPainter(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_PAINTER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileEntityPainter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
