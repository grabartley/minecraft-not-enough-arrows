package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.SocialTestSupport.PAYLOAD_COUNT;
import static com.grahambartley.notenougharrows.gametest.SocialTestSupport.PAYLOAD_ITEM;

import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class CourierArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "courier-arrow";
  private static final String UNDELIVERABLE_BATCH = "courier-arrow-undeliverable";
  private static final String SMALL_CAP_BATCH = "courier-arrow-small-cap";
  private static final int SMALL_CAP = 16;
  private static final BlockPos RECIPIENT_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final Vec3d MID_AIR = new Vec3d(3.5, 4.0, 3.5);
  private static final double AT_THE_FEET = 1.0;
  private static final double AT_THE_WALL = 1.5;

  @BeforeBatch(batchId = UNDELIVERABLE_BATCH)
  public void makeDiamondsUndeliverableBeforeBatch(ServerWorld world) {
    SocialTestSupport.useSocial(
        social ->
            social.withCourier(social.courier().withUndeliverable(List.of("minecraft:diamond"))));
  }

  @AfterBatch(batchId = UNDELIVERABLE_BATCH)
  public void restoreDefaultsAfterUndeliverableBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @BeforeBatch(batchId = SMALL_CAP_BATCH)
  public void lowerThePayloadCapBeforeBatch(ServerWorld world) {
    SocialTestSupport.useSocial(
        social -> social.withCourier(social.courier().withMaxPayload(SMALL_CAP)));
  }

  @AfterBatch(batchId = SMALL_CAP_BATCH)
  public void restoreDefaultsAfterSmallCapBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aStruckPlayerIsHandedTheWholePayload(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              PAYLOAD_COUNT,
              recipient.getInventory().count(PAYLOAD_ITEM),
              "Diamonds in the recipient's inventory");
          context.assertEquals(0, SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM), "Drops");
          assertConserved(context, List.of(recipient, shooter));
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, CourierArrowEntity.class) == null,
              "A courier arrow that delivered is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aStruckPlayerIsNotHurtByTheDelivery(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    final float health = recipient.getHealth();
    SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              PAYLOAD_COUNT, recipient.getInventory().count(PAYLOAD_ITEM), "Delivered diamonds");
          context.assertEquals(health, recipient.getHealth(), "The recipient's health");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aFullRecipientFindsThePayloadAtTheirFeet(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    AgricultureTestSupport.fillInventory(recipient);
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          final List<ItemEntity> drops = diamondsOnTheGround(context);
          context.assertEquals(1, drops.size(), "Dropped stacks");
          context.assertTrue(
              drops.get(0).getPos().distanceTo(recipient.getPos()) <= AT_THE_FEET,
              "The payload should land at the recipient's feet, landed "
                  + drops.get(0).getPos().distanceTo(recipient.getPos())
                  + " away");
          assertConserved(context, List.of(recipient, shooter));
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aRecipientWithSomeRoomKeepsWhatFitsAndFindsTheRestAtTheirFeet(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    AgricultureTestSupport.fillInventory(recipient);
    recipient.getInventory().setStack(0, new ItemStack(PAYLOAD_ITEM, 60));
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              64, recipient.getInventory().count(PAYLOAD_ITEM), "Diamonds that fit");
          context.assertEquals(
              60 + PAYLOAD_COUNT - 64,
              SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM),
              "Diamonds at the recipient's feet");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aStruckBlockGetsThePayloadDroppedAtTheImpact(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          final List<ItemEntity> drops = diamondsOnTheGround(context);
          context.assertEquals(1, drops.size(), "Dropped stacks");
          final Vec3d wall = context.getAbsolute(Vec3d.ofCenter(FiringRangeSupport.BACKSTOP));
          context.assertTrue(
              Math.abs(drops.get(0).getX() - wall.getX()) <= AT_THE_WALL,
              "The payload should drop in front of the struck wall");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, CourierArrowEntity.class) == null,
              "A courier arrow that dropped its payload is spent");
          assertConserved(context, List.of(shooter));
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aStruckCreatureGetsThePayloadDroppedAtItsFeetUnhurt(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              PAYLOAD_COUNT, SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM), "Drops");
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          assertConserved(context, List.of(shooter));
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDiscardedCourierArrowDropsItsPayloadWhereItWas(TestContext context) {
    final CourierArrowEntity arrow = loadedArrowInMidAir(context);
    final Vec3d where = arrow.getPos();

    arrow.discard();

    final List<ItemEntity> drops = diamondsOnTheGround(context);
    context.assertEquals(1, drops.size(), "Dropped stacks");
    context.assertTrue(
        drops.get(0).getPos().distanceTo(where) < 0.01, "The payload drops where the arrow was");
    assertConserved(context, List.of());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCourierArrowThatIsDestroyedDropsItsPayloadWhereItWas(TestContext context) {
    final CourierArrowEntity arrow = loadedArrowInMidAir(context);

    arrow.kill();

    context.assertEquals(
        PAYLOAD_COUNT, SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM), "Drops");
    assertConserved(context, List.of());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCourierArrowLostToTheVoidReturnsItsPayloadToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter =
        ChaosTestSupport.survivalPlayerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.getInventory().clear();
    final CourierArrowEntity arrow = loadedArrowInMidAir(context);
    arrow.setOwner(shooter);
    arrow.setPosition(arrow.getX(), context.getWorld().getBottomY() - 70.0, arrow.getZ());

    arrow.discard();

    context.assertEquals(
        PAYLOAD_COUNT,
        shooter.getInventory().count(PAYLOAD_ITEM),
        "Diamonds back with the shooter");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void anEmptyCourierArrowDoesNotHurtTheCreatureItHits(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    SocialTestSupport.fireEast(context, SocialTestSupport.emptyCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPayloadLostToTheVoidReachesAShooterInAnotherDimension(TestContext context) {
    final ServerPlayerEntity shooter =
        ChaosTestSupport.survivalPlayerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.getInventory().clear();
    final CourierArrowEntity arrow = loadedArrowInMidAir(context);
    arrow.setOwner(shooter);
    shooter.teleport(
        context.getWorld().getServer().getWorld(net.minecraft.world.World.NETHER),
        0.5,
        70.0,
        0.5,
        0f,
        0f);
    arrow.setPosition(arrow.getX(), context.getWorld().getBottomY() - 70.0, arrow.getZ());

    arrow.discard();

    context.assertEquals(
        PAYLOAD_COUNT,
        shooter.getInventory().count(PAYLOAD_ITEM),
        "Diamonds back with the shooter");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCourierArrowUnloadedWithItsChunkDropsNothing(TestContext context) {
    final CourierArrowEntity arrow = loadedArrowInMidAir(context);

    arrow.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);

    context.assertEquals(0, SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM), "Drops");
    context.assertTrue(arrow.isLoaded(), "The unloaded arrow still carries its payload");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSavedAndReloadedCourierArrowStillCarriesTheSameStack(TestContext context) {
    final ItemStack payload = new ItemStack(Items.DIAMOND_SWORD);
    payload.setDamage(17);
    final CourierArrowEntity arrow = arrowCarrying(context, payload);
    final NbtCompound saved = new NbtCompound();
    arrow.saveNbt(saved);
    arrow.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);

    final Optional<Entity> reloaded = EntityType.getEntityFromNbt(saved, context.getWorld());

    context.assertTrue(reloaded.isPresent(), "A saved courier arrow should load again");
    final Optional<ItemStack> carried = ((CourierArrowEntity) reloaded.get()).payload();
    context.assertTrue(
        carried.isPresent() && ItemStack.areEqual(carried.get(), payload),
        "The reloaded arrow should carry exactly the stack it was fired with, carried " + carried);
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void anEmptyCourierArrowEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    SocialTestSupport.fireEast(context, SocialTestSupport.emptyCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          final CourierArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, CourierArrowEntity.class);
          context.assertTrue(arrow != null, "An empty courier arrow embeds like any other");
          context.assertFalse(arrow.isLoaded(), "and carries nothing");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = UNDELIVERABLE_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aPayloadMadeUndeliverableAfterLoadingIsReturnedToTheShooterStillLoaded(
      TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              0, recipient.getInventory().count(PAYLOAD_ITEM), "Diamonds with the recipient");
          context.assertEquals(
              1, shooter.getInventory().count(SocialArrows.COURIER_ARROW.item()), "Arrows back");
          final ItemStack returned =
              shooter
                  .getInventory()
                  .getStack(
                      shooter.getInventory().getSlotWithStack(SocialTestSupport.loadedCourier()));
          context.assertTrue(
              CourierPayloads.isLoaded(returned), "The returned courier arrow is still loaded");
          assertConserved(context, List.of(recipient, shooter));
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = SMALL_CAP_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aPayloadOverTheCapAtImpactIsReturnedRatherThanDelivered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter =
        SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(0, SocialTestSupport.droppedNearby(context, PAYLOAD_ITEM), "Drops");
          context.assertEquals(
              PAYLOAD_COUNT,
              SocialTestSupport.accountedFor(context, PAYLOAD_ITEM, List.of(shooter)),
              "Diamonds still inside the returned arrow");
          context.complete();
        });
  }

  private static ServerPlayerEntity recipientAt(final TestContext context) {
    final ServerPlayerEntity recipient =
        ChaosTestSupport.survivalPlayerAt(context, RECIPIENT_STAND);
    recipient.getInventory().clear();
    return recipient;
  }

  private static CourierArrowEntity loadedArrowInMidAir(final TestContext context) {
    return arrowCarrying(context, new ItemStack(PAYLOAD_ITEM, PAYLOAD_COUNT));
  }

  private static CourierArrowEntity arrowCarrying(
      final TestContext context, final ItemStack payload) {
    final Vec3d at = context.getAbsolute(MID_AIR);
    final CourierArrowEntity arrow =
        new CourierArrowEntity(
            SocialArrows.COURIER_ARROW.entityType(),
            context.getWorld(),
            at.x,
            at.y,
            at.z,
            SocialTestSupport.loadedCourier(payload),
            null);
    arrow.setNoGravity(true);
    context.getWorld().spawnEntity(arrow);
    return arrow;
  }

  private static List<ItemEntity> diamondsOnTheGround(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ItemEntity.class,
            context.getTestBox().expand(4.0),
            drop -> drop.getStack().isOf(PAYLOAD_ITEM));
  }

  private static void assertConserved(
      final TestContext context, final List<ServerPlayerEntity> players) {
    context.assertEquals(
        PAYLOAD_COUNT,
        SocialTestSupport.accountedFor(context, PAYLOAD_ITEM, players),
        "Diamonds anywhere in the world");
  }
}
