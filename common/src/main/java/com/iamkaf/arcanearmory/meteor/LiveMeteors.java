//? if >=1.21.11 {
package com.iamkaf.arcanearmory.meteor;

import com.iamkaf.amber.api.billboard.v1.Billboard;
import com.iamkaf.amber.api.billboard.v1.BillboardAnimation.Easing;
import com.iamkaf.amber.api.billboard.v1.Billboards;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.WorldEvents;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Meteors that streak across the night sky and land as Meteorite boulders. In flight a meteor is only a
 * billboard shown to each nearby player; nothing exists in the world until it lands.
 */
final class LiveMeteors {
    private static final Identifier METEOR_TEXTURE = ArcaneArmoryConstants.resource("textures/meteor/meteor.png");
    private static final Identifier FLASH_TEXTURE = ArcaneArmoryConstants.resource("textures/meteor/impact_flash.png");
    private static final String COMMAND_KEY = "commands." + ArcaneArmoryConstants.MOD_ID + ".meteor.";
    private static final int FLIGHT_TICKS = 60;
    private static final int FLASH_TICKS = 16;
    /** Meteors land at least this far from every player, so they are seen falling but never hit anyone. */
    private static final int MIN_PLAYER_DISTANCE = 48;
    private static final int MAX_LANDING_DISTANCE = 96;
    private static final double VIEW_DISTANCE = 512.0D;
    private static final double START_DISTANCE = 90.0D;
    private static final double START_HEIGHT = 120.0D;
    // A chance per dark Overworld tick. Nights last about 10,000 ticks, so roughly one night in four has a meteor.
    private static final int DARK_TICK_CHANCE = 40_000;

    private static final List<FallingMeteor> FALLING = new ArrayList<>();
    private static @Nullable ServerLevel overworld;

    private LiveMeteors() {
    }

    static void init() {
        WorldEvents.WORLD_LOAD.register((server, level) -> {
            if (level instanceof ServerLevel serverLevel && serverLevel.dimension() == Level.OVERWORLD) {
                overworld = serverLevel;
            }
        });
        WorldEvents.WORLD_UNLOAD.register((server, level) -> {
            if (level == overworld) {
                overworld = null;
                FALLING.clear();
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(LiveMeteors::tick);
        CommandEvents.EVENT.register((dispatcher, context, selection) -> dispatcher.register(command()));
    }

    private static void tick() {
        FALLING.removeIf(FallingMeteor::tick);
        ServerLevel level = overworld;
        if (level == null || !level.isDarkOutside() || level.getRandom().nextInt(DARK_TICK_CHANCE) != 0) {
            return;
        }
        List<ServerPlayer> players = level.players().stream().filter(player -> !player.isSpectator()).toList();
        if (players.isEmpty()) {
            return;
        }
        RandomSource random = level.getRandom();
        ServerPlayer near = players.get(random.nextInt(players.size()));
        findLanding(level, near.blockPosition(), random).ifPresent(landing -> launch(level, landing, randomHorizontal(random)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal(ArcaneArmoryConstants.MOD_ID)
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("meteor")
                        .executes(context -> callDown(context.getSource(), Optional.empty()))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> callDown(context.getSource(),
                                        Optional.of(BlockPosArgument.getLoadedBlockPos(context, "pos"))))));
    }

    /** Calls a meteor down near the caller, or onto the surface of a chosen column. */
    private static int callDown(CommandSourceStack source, Optional<BlockPos> column) {
        ServerLevel level = source.getLevel();
        if (level.dimension() != Level.OVERWORLD) {
            source.sendFailure(Component.translatable(COMMAND_KEY + "overworld_only"));
            return 0;
        }
        RandomSource random = level.getRandom();
        BlockPos caller = BlockPos.containing(source.getPosition());
        Optional<BlockPos> landing = column.isPresent() ? surface(level, column.get()) : findLanding(level, caller, random);
        if (landing.isEmpty()) {
            source.sendFailure(Component.translatable(COMMAND_KEY + "no_ground"));
            return 0;
        }
        BlockPos at = landing.get();
        // Fly in from one side of the caller's view, so the whole fall can be watched and filmed.
        Vec3 toLanding = new Vec3(at.getX() - caller.getX(), 0.0D, at.getZ() - caller.getZ());
        Vec3 approach = toLanding.lengthSqr() < 1.0D
                ? randomHorizontal(random)
                : new Vec3(-toLanding.z, 0.0D, toLanding.x).normalize().scale(random.nextBoolean() ? 1.0D : -1.0D);
        launch(level, at, approach);
        source.sendSuccess(() -> Component.translatable(COMMAND_KEY + "falling", at.getX(), at.getY(), at.getZ()), true);
        return 1;
    }

    private static Optional<BlockPos> findLanding(ServerLevel level, BlockPos around, RandomSource random) {
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            int distance = MIN_PLAYER_DISTANCE + random.nextInt(MAX_LANDING_DISTANCE - MIN_PLAYER_DISTANCE + 1);
            BlockPos column = around.offset((int) Math.round(Math.cos(angle) * distance), 0, (int) Math.round(Math.sin(angle) * distance));
            Optional<BlockPos> landing = surface(level, column).filter(at -> farFromPlayers(level, at));
            if (landing.isPresent()) {
                return landing;
            }
        }
        return Optional.empty();
    }

    /** The first open block above dry ground in a loaded column; meteors never land in water or lava. */
    private static Optional<BlockPos> surface(ServerLevel level, BlockPos column) {
        if (!level.isLoaded(column)) {
            return Optional.empty();
        }
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ());
        BlockPos at = new BlockPos(column.getX(), y, column.getZ());
        if (y <= level.getMinY() || !level.getFluidState(at.below()).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(at);
    }

    private static boolean farFromPlayers(ServerLevel level, BlockPos at) {
        for (ServerPlayer player : level.players()) {
            double dx = player.getX() - at.getX() - 0.5D;
            double dz = player.getZ() - at.getZ() - 0.5D;
            if (dx * dx + dz * dz < MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    private static void launch(ServerLevel level, BlockPos landing, Vec3 approach) {
        Vec3 end = Vec3.atCenterOf(landing.above());
        Vec3 start = end.add(approach.scale(START_DISTANCE)).add(0.0D, START_HEIGHT, 0.0D);
        for (ServerPlayer viewer : viewers(level, end)) {
            Billboards.show(viewer, flight(viewer, start, end));
        }
        FALLING.add(new FallingMeteor(level, landing));
    }

    private static Billboard flight(ServerPlayer viewer, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize();
        Vec3 eye = viewer.getEyePosition();
        double startRoll = screenAngle(eye, start, direction);
        double endRoll = startRoll + Mth.wrapDegrees(screenAngle(eye, end, direction) - startRoll);
        return Billboard.texture(start, METEOR_TEXTURE, 24.0F, 12.0F)
                .forTicks(FLIGHT_TICKS)
                .translateTo(end.subtract(start), Easing.EASE_IN_QUAD)
                .rotateFromTo(new Vec3(0.0D, 0.0D, startRoll), new Vec3(0.0D, 0.0D, endRoll))
                .scaleFromTo(0.6D, 1.2D)
                .opacityFromTo(0.3F, 1.0F, Easing.EASE_OUT_QUAD);
    }

    /**
     * The meteor texture flies toward its right edge, and billboards face the camera. This rolls it to the
     * direction of travel as a viewer looking at the meteor sees it, in degrees counterclockwise from right.
     */
    private static double screenAngle(Vec3 eye, Vec3 at, Vec3 direction) {
        Vec3 forward = at.subtract(eye).normalize();
        Vec3 right = forward.cross(new Vec3(0.0D, 1.0D, 0.0D));
        right = right.lengthSqr() < 1.0E-6D ? new Vec3(1.0D, 0.0D, 0.0D) : right.normalize();
        Vec3 up = right.cross(forward);
        return Math.toDegrees(Math.atan2(direction.dot(up), direction.dot(right)));
    }

    private static void land(ServerLevel level, BlockPos landing) {
        Optional<BlockPos> surface = surface(level, landing);
        if (surface.isEmpty()) {
            return;
        }
        BlockPos center = surface.get().above();
        MeteorBoulder.place(level, center, level.getRandom(), LiveMeteors::isOpen);

        Vec3 impact = Vec3.atCenterOf(center);
        level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 8.0F, 0.6F);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, true, true, impact.x, impact.y, impact.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.FLAME, true, true, impact.x, impact.y, impact.z, 40, 1.5D, 1.0D, 1.5D, 0.08D);
        level.sendParticles(ParticleTypes.LAVA, true, true, impact.x, impact.y, impact.z, 24, 1.5D, 1.0D, 1.5D, 0.0D);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, true, true, impact.x, impact.y + 1.0D, impact.z, 40, 2.0D, 1.0D, 2.0D, 0.04D);
        Billboard flash = Billboard.texture(impact, FLASH_TEXTURE, 4.0F, 4.0F)
                .forTicks(FLASH_TICKS)
                .scaleFromTo(0.5D, 3.0D, Easing.EASE_OUT_CUBIC)
                .opacityFromTo(1.0F, 0.0F, Easing.EASE_IN_QUAD);
        for (ServerPlayer viewer : viewers(level, impact)) {
            Billboards.show(viewer, flash);
        }
    }

    // A landing meteor fills only air and replaceable plants, never digging into the ground.
    private static boolean isOpen(BlockState state) {
        return state.isAir() || state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    private static List<ServerPlayer> viewers(ServerLevel level, Vec3 at) {
        return level.players().stream()
                .filter(player -> player.distanceToSqr(at.x, player.getY(), at.z) <= VIEW_DISTANCE * VIEW_DISTANCE)
                .toList();
    }

    private static Vec3 randomHorizontal(RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        return new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
    }

    private static final class FallingMeteor {
        private final ServerLevel level;
        private final BlockPos landing;
        private int ticksLeft = FLIGHT_TICKS;

        private FallingMeteor(ServerLevel level, BlockPos landing) {
            this.level = level;
            this.landing = landing;
        }

        /** Counts down the flight and lands the meteor, returning whether it has landed. */
        private boolean tick() {
            if (--ticksLeft > 0) {
                return false;
            }
            land(level, landing);
            return true;
        }
    }
}
//?}
