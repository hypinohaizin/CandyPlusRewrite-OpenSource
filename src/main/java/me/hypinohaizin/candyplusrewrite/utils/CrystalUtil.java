package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.CombatRules;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.util.NonNullList;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

public class CrystalUtil implements Util {
    public static Boolean getArmourFucker(final EntityPlayer player, final float percent) {
        for (final ItemStack stack : player.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR) {
                return true;
            }
            final float n = (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100.0f;
        }
        return false;
    }

    public static boolean canPlaceCrystalForPistonAura(final BlockPos pos) {
        return BlockUtil.getBlock(pos.add(0, 1, 0)) == Blocks.AIR && BlockUtil.getBlock(pos.add(0, 2, 0)) == Blocks.AIR;
    }

    public static boolean canSeePos(final BlockPos pos) {
        return CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) == null;
    }

    public static boolean canPlaceCrystal(final BlockPos pos) {
        return BlockUtil.getBlock(pos.add(0, 1, 0)) == Blocks.AIR && BlockUtil.getBlock(pos.add(0, 2, 0)) == Blocks.AIR;
    }

    public static List<BlockPos> possiblePlacePositions(final float placeRange, final boolean specialEntityCheck, final boolean oneDot15) {
        final NonNullList<BlockPos> positions = NonNullList.create();
        List<BlockPos> filtered = getSphere(PlayerUtil.getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream()
                .filter(pos -> CrystalUtil.mc.world.getBlockState(pos).getBlock() != Blocks.AIR)
                .filter(pos -> canPlaceCrystal(pos, specialEntityCheck, oneDot15))
                .collect(Collectors.toList());
        positions.addAll(filtered);
        return positions;
    }


    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int) r) : cy;
                while (true) {
                    final float f = (float) y;
                    final float f2 = sphere ? (cy + r) : ((float) (cy + h));
                    if (f >= f2) {
                        break;
                    }
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
            }
        }
        return circleblocks;
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean specialEntityCheck, final boolean onepointThirteen) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (!onepointThirteen) {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || CrystalUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            } else {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (final Exception ex) {
        }
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(CrystalUtil.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    private static float getDamageMultiplied(final float damage) {
        final int diff = CrystalUtil.mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (final Exception ex) {
            }
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static boolean hasCrystal(final BlockPos pos) {
        final List<Entity> crystals = CrystalUtil.mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityEnderCrystal)
                .collect(Collectors.toList());
        for (final Entity crystal : crystals) {
            final BlockPos crystalPos = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
            if (pos.equals(crystalPos.add(0, -1, 0))) {
                return true;
            }
        }
        return false;
    }

    public static Entity getCrystal(final BlockPos pos) {
        final List<Entity> crystals = CrystalUtil.mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityEnderCrystal)
                .collect(Collectors.toList());
        for (final Entity crystal : crystals) {
            final BlockPos crystalPos = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
            if (pos.equals(crystalPos.add(0, -1, 0))) {
                return crystal;
            }
        }
        return null;
    }
}