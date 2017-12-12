package crazypants.enderio.machines.machine.wireless;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PainterUtil2;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWirelessCharger extends BlockEio<TileWirelessCharger>
    implements IResourceTooltipProvider, ISmartRenderAwareBlock, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveRenderers {

  public static BlockWirelessCharger create(@Nonnull IModObject modObject) {
    BlockWirelessCharger res = new BlockWirelessCharger(modObject);
    res.init();
    return res;
  }

  private BlockWirelessCharger(@Nonnull IModObject modObject) {
    super(modObject, TileWirelessCharger.class);
    setLightOpacity(1);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  @Override
  protected void init() {
    SmartModelAttacher.register(this);
  }

  @Override
  @Nonnull
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  @Nonnull
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  @Nonnull
  public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public final IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
    TileWirelessCharger tileEntity = getTileEntitySafe(world, pos);
    if (tileEntity != null) {
      setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
    }
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileWirelessCharger tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  private @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return WirelessRenderMapper.instance;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return WirelessRenderMapper.instance;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    TileWirelessCharger te = getTileEntity(world, pos);
    if (te != null) {
      te.readFromItemStack(stack);
      te.setPaintSource(PainterUtil2.getSourceBlock(stack));
    }
  }

  @Override
  protected void processDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable TileWirelessCharger te, @Nonnull ItemStack drop) {
    if (te != null) {
      te.writeToItemStack(drop);
    }
    PainterUtil2.setSourceBlock(drop, getPaintSource(world.getBlockState(pos), world, pos));
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileWirelessCharger te = getTileEntity(world, pos);
    if (te != null) {
      te.setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileWirelessCharger te = getTileEntitySafe(world, pos);
    if (te != null) {
      return te.getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(MachineObject.block_wireless_charger);
  } // TODO: check if this is needed---smartmodelattacher should already take care of it...

}