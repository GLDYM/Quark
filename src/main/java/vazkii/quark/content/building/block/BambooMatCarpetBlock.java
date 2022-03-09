package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

public class BambooMatCarpetBlock extends BambooMatBlock {

	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public BambooMatCarpetBlock(String name, QuarkModule module) {
		super(name, module, CreativeModeTab.TAB_DECORATIONS);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState p_152917_, @Nonnull BlockGetter p_152918_, @Nonnull BlockPos p_152919_, @Nonnull CollisionContext p_152920_) {
		return SHAPE;
	}

	@Nonnull
	@Override
	public BlockState updateShape(BlockState p_152926_, @Nonnull Direction p_152927_, @Nonnull BlockState p_152928_, @Nonnull LevelAccessor p_152929_, @Nonnull BlockPos p_152930_, @Nonnull BlockPos p_152931_) {
		return !p_152926_.canSurvive(p_152929_, p_152930_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_152926_, p_152927_, p_152928_, p_152929_, p_152930_, p_152931_);
	}

	@Override
	public boolean canSurvive(@Nonnull BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
		return !p_152923_.isEmptyBlock(p_152924_.below());
	}

}
