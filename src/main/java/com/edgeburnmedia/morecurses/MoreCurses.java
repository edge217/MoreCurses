package com.edgeburnmedia.morecurses;

import com.edgeburnmedia.morecurses.enchantments.ClumsinessCurse;
import com.edgeburnmedia.morecurses.enchantments.VoidingCurse;
import com.edgeburnmedia.morecurses.enchantments.WeightlessnessCurse;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreCurses implements ModInitializer {

	public static final String MOD_ID = "more_curses";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Enchantment VOIDING_CURSE = new VoidingCurse();
	public static final Identifier VOIDING_CURSE_ID = new Identifier(MOD_ID, "voiding_curse");
	public static final Enchantment WEIGHTLESSNESS_CURSE = new WeightlessnessCurse();
	public static final Identifier WEIGHTLESSNESS_CURSE_ID = new Identifier(MOD_ID, "weightlessness_curse");
	public static final Enchantment CLUMSINESS_CURSE = new ClumsinessCurse();
	public static final Identifier CLUMSINESS_CURSE_ID = new Identifier(MOD_ID, "clumsiness_curse");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registries.ENCHANTMENT, VOIDING_CURSE_ID, VOIDING_CURSE);
		Registry.register(Registries.ENCHANTMENT, WEIGHTLESSNESS_CURSE_ID, WEIGHTLESSNESS_CURSE);
		Registry.register(Registries.ENCHANTMENT, CLUMSINESS_CURSE_ID, CLUMSINESS_CURSE);

		LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
			LootTable.Builder lootTableBuilder = LootTable.builder();

			for (LootPool pool : original.pools) {
				ArrayList<LootCondition> conditions = new ArrayList<LootCondition>(Arrays.asList(pool.conditions));

				EnchantmentPredicate voidingCursePredicate = new EnchantmentPredicate(VOIDING_CURSE, IntRange.atLeast(1));
				ItemPredicate.Builder itemPredicateBuilder = ItemPredicate.Builder.create().enchantment(voidingCursePredicate);

				LootCondition voidingCurseEnchantedItemCondition = MatchToolLootCondition.builder(itemPredicateBuilder).invert().build();


				conditions.add(0, voidingCurseEnchantedItemCondition);
				lootTableBuilder.pool(FabricLootPoolBuilder.copyOf(pool).conditionally(conditions)).build();
			}
			return lootTableBuilder.build();
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			for (ServerPlayerEntity player : world.getPlayers()) {
				for (ItemStack armourItem : player.getInventory().armor) {
					boolean hasWeightlessnessCurse = armourItem.getEnchantments().asString().contains(WEIGHTLESSNESS_CURSE_ID.toString());
					if (hasWeightlessnessCurse) {
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 30, 0, false, false));
					}
				}
			}
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!entity.getWorld().isClient()) {
				LOGGER.info("entity {}, source {}, amount {}", entity, source.getType(), amount);
			}
			return true;
		});
	}
}
