package me.noryea.colorfulallays.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Iterator;


@Mixin(AllayEntity.class)
public abstract class AllayEntityMixin extends MobEntity {

    @Shadow private SimpleInventory inventory;
    private static final TrackedData<Byte> COLOR;

    protected AllayEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setColor(defaultColor(world.getRandom()));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, (byte)3);
    }

    @Nullable
    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR) & 15);
    }

    public void setColor(DyeColor color) {
        this.dataTracker.set(COLOR, (byte)color.getId());
    }

    @Overwrite
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isEmpty()) {
            if (player.isSneaking() && itemStack.getItem() instanceof DyeItem) {
                if (!player.getAbilities().creativeMode && this.getColor() != ((DyeItem)itemStack.getItem()).getColor()) {
                    itemStack.decrement(1);
                }
                this.setColor(((DyeItem)itemStack.getItem()).getColor());
                this.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());
                return ActionResult.SUCCESS;
            } else if (itemStack2.isEmpty()) {
                ItemStack itemStack3 = itemStack.copy();
                itemStack3.setCount(1);
                this.setStackInHand(Hand.MAIN_HAND, itemStack3);
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                this.world.playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                this.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());
                return ActionResult.SUCCESS;
            }
        } else if (!itemStack2.isEmpty() && hand == Hand.MAIN_HAND) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.world.playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.swingHand(Hand.MAIN_HAND);
            Iterator var5 = this.inventory.clearToList().iterator();

            while(var5.hasNext()) {
                ItemStack itemStack4 = (ItemStack)var5.next();
                LookTargetUtil.give(this, itemStack4, this.getPos());
            }

            this.getBrain().forget(MemoryModuleType.LIKED_PLAYER);
            player.giveItemStack(itemStack2);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void addWriteCustomData(NbtCompound nbt, CallbackInfo cbi) {
        if (this.getColor() != null) {
            nbt.putByte("Color", (byte)this.getColor().getId());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void addReadCustomData(NbtCompound nbt, CallbackInfo cbi) {
        if (nbt.contains("Color")) {
            this.setColor(DyeColor.byId(nbt.getByte("Color")));
        }
    }

    private DyeColor defaultColor(Random random) {
        int i = random.nextInt(100);
        if (i < 22) {
            return DyeColor.GREEN;
        } else if (i < 28) {
            return DyeColor.PURPLE;
        } else if (i < 40) {
            return DyeColor.YELLOW;
        } else if (i < 52) {
            return DyeColor.RED;
        } else if (i < 64) {
            return DyeColor.ORANGE;
        } else if (i < 72) {
            return DyeColor.BROWN;
        } else {
            return random.nextInt(200) == 0 ? DyeColor.WHITE : DyeColor.LIGHT_BLUE;
        }
    }

    static {
        COLOR = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
