package net.pythonbear.tead.item.sickle;

import net.minecraft.block.*;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.registry.Registry;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.pythonbear.tead.Tead;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;


public class SickleHarvesting {
    private SickleHarvesting() {
    }

    /**
     * Checks whether the given block is a crop that can be broken and, optionally, drop xp.
     *
     * @param block
     * @return whether the given block is a valid breakable crop.
     */
    public static boolean isCrop(Block block) {
        return block instanceof CropBlock || block instanceof NetherWartBlock || block instanceof CocoaBlock;
    }

    /**
     * Returns the age integer property from the given blockState.
     *
     * @param blockState {@link BlockState state} to take the age property from.
     * @return the age property from the given blockState.
     * @throws NullPointerException if the age property was null.
     * @throws NoSuchElementException if no value for the age property is present.
     * @throws ClassCastException if the age property is not an {@link IntProperty}.
     */
    public static IntProperty getAge(BlockState blockState) throws NullPointerException, NoSuchElementException,
            ClassCastException {
        return (IntProperty) blockState.getProperties().stream().filter(property ->
                property.getName().equals("age")).findFirst().orElseThrow();
    }

    /**
     * Checks whether the given blockstate is a mature crop.
     *
     * @param blockState {@link BlockState state} to take the age property from.
     * @param age {@link IntProperty integer property} for the crop age.
     * @return whether the given blockstate is a mature crop.
     */
    public static boolean isMature(BlockState blockState, IntProperty age) {
        return blockState.getOrEmpty(age).orElse(0) >= Collections.max(age.getValues());
    }

    /**
     * Checks whether the given blockstate is a mature crop.
     *
     * @param blockState {@link BlockState state} to take the age property from.
     * @return whether the given blockstate is a mature crop.
     * @throws NullPointerException if the age property was null.
     * @throws NoSuchElementException if no value for the age property is present.
     * @throws ClassCastException if the age property is not an {@link IntProperty}.
     */
    public static boolean isMature(BlockState blockState) throws NullPointerException, NoSuchElementException,
            ClassCastException {
        return isMature(blockState, getAge(blockState));
    }

    /**
     * Checks whether the given crop is a multi-block crop (a crop made of multiple vertically connected blocks).
     *
     * @param world {@link World} in which the crop is placed.
     * @param blockState {@link BlockState} of the crop.
     * @param blockPos {@link BlockPos} of the crop.
     * @return whether the given crop is a multi-block crop.
     */
    public static boolean isTallCrop(World world, BlockState blockState, BlockPos blockPos) {
        return blockState.isIn(BlockTags.CROPS) && world.getBlockState(blockPos.down()).isOf(blockState.getBlock()) ||
                world.getBlockState(blockPos.up()).isOf(blockState.getBlock());
    }

    /**
     * Checks if the given tier string reference is in the given list of tiers.
     *
     * @param tiers
     * @param tierRef
     * @return whether {@code tierRef} represents a {@link ToolMaterial} in {@code tiers}.
     */
    public static boolean isTierIn(List<ToolMaterial> tiers, String tierRef) {
        return tiers.stream().anyMatch(tier -> matchesTier(tierRef, tier));
    }

    /**
     * Checks if the given {@link ToolMaterial} is in the given list of tiers.
     *
     * @param tiers
     * @param tier
     * @return whether {@code tier} is a {@link ToolMaterial} in {@code tiers}.
     */
    public static boolean isTierIn(List<ToolMaterial> tiers, ToolMaterial tier) {
        return isTierIn(tiers, tier.toString());
    }

    /**
     * Gets the proper tier level for the given {@link ToolMaterial}.
     * <p>
     * If the tier is not registered then the level is {@code -1}.
     *
     * @param tier
     * @return tier level.
     */
    public static int getTierLevel(ToolMaterial tier) {
        return tier.getMiningLevel();
    }

    /**
     * Gets the proper tier level for the given {@link ToolMaterial} reference.
     * <p>
     * If {@code tierRef} is {@code "none"} then the level is {@code -1}.
     * <p>
     * If the tier is not in the {@link ToolMaterials} then the level is {@code 0} (same as Vanilla wood tier).
     *
     * @param tierRef
     * @return tier level.
     */
    public static int getTierLevel(String tierRef) {
        try {
            return tierRef.equalsIgnoreCase("none") ? -1 :
                    getTierLevel(Stream.of(ToolMaterials.values()).filter(tier ->
                            matchesTier(tierRef, tier)).findFirst().orElseThrow());
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    /**
     * Checks whether the given {@link ToolMaterial} matches the given tier reference.
     *
     * @param tier
     * @param tierRef
     * @return whether {@code tierRef} represents {@code tier}.
     */
    public static boolean matchesTier(String tierRef, ToolMaterial tier) {
        return tier.toString().equalsIgnoreCase(tierRef);
    }

    /**
     * Returns the in-game ID of the block passed as parameter.
     *
     * @param block
     * @return in-game ID of the given block.
     */
    private static String getKey(Block block) {
        Optional<RegistryKey<Block>> key = Registry.BLOCK.getKey(block);
        if (key.isPresent()) {
            return key.get().getValue().toString();
        }
        Tead.LOGGER.debug("Couldn't get key for block [" + block + "].");
        return "";
    }
}