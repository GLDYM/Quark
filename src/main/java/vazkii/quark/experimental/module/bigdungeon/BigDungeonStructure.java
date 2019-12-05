package vazkii.quark.experimental.module.bigdungeon;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;
import vazkii.quark.base.Quark;

public class BigDungeonStructure extends ScatteredStructure<NoFeatureConfig> {

	private static final String NAMESPACE = "big_dungeon";

	private static final String STARTS_DIR = "starts";
	private static final Set<String> STARTS = ImmutableSet.of(
			"3x3_pillars", "3x3_tnt", "3x3_water",
			"plus_barricade", "plus_ores", "plus_plain",
			"triplex_3sect", "triplex_lava", "triplex_plain");

	private static final String ROOMS_DIR = "rooms";
	private static final Set<String> ROOMS = ImmutableSet.of(
			"4room_plain", "4room_trapped",
			"ascend_intersection", "ascend_plain",
			"climb_parkour", "climb_plain",
			"double_hall_plain", "double_hall_silverfish",
			"connector_base", "connector_bush", "connector_fountain", "connector_melon");
	
	private static final String CORRIDORS_DIR = "corridors";
	private static final Set<String> CORRIDORS = ImmutableSet.of(
			"forward_cobweb", "forward_plain",
			"left_cobweb", "left_plain",
			"right_cobweb", "right_plain",
			"t_cobweb", "t_plain");
	
	private static final String ENDPOINT = "misc/endpoint";
			
	private static final ResourceLocation START_POOL = new ResourceLocation(Quark.MOD_ID, NAMESPACE + "/" + STARTS_DIR);
	
	private static final int MAX_ROOMS = 10;
	private static final int MAX_CHUNK_WIDTH = 5;
 
	static {
		JigsawRegistryHelper.pool(NAMESPACE, STARTS_DIR)
		.addMult(STARTS_DIR, STARTS, 1)
		.register(PlacementBehaviour.RIGID);

		JigsawRegistryHelper.pool(NAMESPACE, ROOMS_DIR)
		.addMult(ROOMS_DIR, ROOMS, 1)
		.register(PlacementBehaviour.RIGID);
		
		JigsawRegistryHelper.pool(NAMESPACE, CORRIDORS_DIR)
		.addMult(CORRIDORS_DIR, CORRIDORS, 1)
		.register(PlacementBehaviour.RIGID);
		
		final int roomWeight = 100;
		final int corridorWeight = 70;
		final double endpointWeightMult = 1.5;
		
		JigsawRegistryHelper.pool(NAMESPACE, "rooms_or_endpoint")
		.addMult(ROOMS_DIR, ROOMS, roomWeight)
		.addMult(CORRIDORS_DIR, CORRIDORS, corridorWeight)
		.add(ENDPOINT, (int) ((ROOMS.size() * roomWeight + CORRIDORS.size() * corridorWeight) * endpointWeightMult))
		.register(PlacementBehaviour.RIGID);
	}

	public BigDungeonStructure() {
		super(fc -> NoFeatureConfig.NO_FEATURE_CONFIG);
		setRegistryName(Quark.MOD_ID, NAMESPACE);
	}

	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
		ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
		if(chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
			int i = chunkPosX >> 4;
			int j = chunkPosZ >> 4;
			rand.setSeed((long)(i ^ j << 4) ^ chunkGen.getSeed());
			return true;
		}
		return false;
	}

	@Override
	protected int getSeedModifier() {
		return 79234823;
	}

	@Override
	public IStartFactory getStartFactory() {
		return Start::new;
	}

	@Override
	public String getStructureName() {
		return getRegistryName().toString();
	}

	@Override
	public int getSize() {
		return MAX_CHUNK_WIDTH;
	}

	public static class Start extends MarginedStructureStart {

		public Start(Structure<?> structureIn, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
			super(structureIn, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
			BlockPos blockpos = new BlockPos(chunkX * 16, 40, chunkZ * 16); // TODO proper height check here
			JigsawManager.func_214889_a(START_POOL, MAX_ROOMS, Piece::new, generator, templateManagerIn, blockpos, components, this.rand);
			recalculateStructureSize();
		}

	}

	public static class Piece extends AbstractVillagePiece {

		public static IStructurePieceType PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "bigdungeon", BigDungeonStructure.Piece::new);

		public Piece(TemplateManager templateManagerIn, JigsawPiece jigsawPieceIn, BlockPos posIn, int p_i50560_4_, Rotation rotationIn, MutableBoundingBox boundsIn) {
			super(PIECE_TYPE, templateManagerIn, jigsawPieceIn, posIn, p_i50560_4_, rotationIn, boundsIn);
		}

		public Piece(TemplateManager templateManagerIn, CompoundNBT nbt) {
			super(templateManagerIn, nbt, PIECE_TYPE);
		}

	}

}
