package me.noryea.colorfulallays.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AllayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AllayEntityRenderer.class)
@Environment(EnvType.CLIENT) public class AllayEntityRendererMixin extends MobEntityRenderer<AllayEntity, AllayEntityModel> {

    public AllayEntityRendererMixin(EntityRendererFactory.Context context, AllayEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Overwrite
    public Identifier getTexture(AllayEntity allayEntity) {
        return MODIFIER_TEXTURES[MathHelper.clamp(allayEntity.writeNbt(new NbtCompound()).getByte("Color"), 0, 15)];
    }

    private static final Identifier[] MODIFIER_TEXTURES = new Identifier[] {
            new Identifier("textures/entity/allay/white.png"),
            new Identifier("textures/entity/allay/orange.png"),
            new Identifier("textures/entity/allay/magenta.png"),
            new Identifier("textures/entity/allay/light_blue.png"),
            new Identifier("textures/entity/allay/yellow.png"),
            new Identifier("textures/entity/allay/lime.png"),
            new Identifier("textures/entity/allay/pink.png"),
            new Identifier("textures/entity/allay/gray.png"),
            new Identifier("textures/entity/allay/light_gray.png"),
            new Identifier("textures/entity/allay/cyan.png"),
            new Identifier("textures/entity/allay/purple.png"),
            new Identifier("textures/entity/allay/blue.png"),
            new Identifier("textures/entity/allay/brown.png"),
            new Identifier("textures/entity/allay/green.png"),
            new Identifier("textures/entity/allay/red.png"),
            new Identifier("textures/entity/allay/black.png"),
    };

}