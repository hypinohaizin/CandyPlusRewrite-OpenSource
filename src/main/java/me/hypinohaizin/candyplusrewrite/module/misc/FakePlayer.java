package me.hypinohaizin.candyplusrewrite.module.misc;

import net.minecraft.client.multiplayer.WorldClient;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class FakePlayer extends Module
{
    public Setting<Boolean> copyInv;
    private EntityOtherPlayerMP _fakePlayer;

    public FakePlayer() {
        super("FakePlayer", Categories.MISC, false, false);
        copyInv = register(new Setting<>("CopyInv", true));
    }
    
    @Override
    public void onEnable() {
        if (FakePlayer.mc.player == null) {
            disable();
            return;
        }
        _fakePlayer = null;
        if (FakePlayer.mc.player != null) {
            final WorldClient world = FakePlayer.mc.world;
            final UUID fromString = UUID.fromString("53f654cb-e42a-4a67-8072-4bb70ed76230");
            getClass();
            (_fakePlayer = new EntityOtherPlayerMP(world, new GameProfile(fromString, "8x0f"))).copyLocationAndAnglesFrom(FakePlayer.mc.player);
            _fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
            if (copyInv.getValue()) {
                _fakePlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
            }
            FakePlayer.mc.world.addEntityToWorld(-100, _fakePlayer);
        }
    }
    
    @Override
    public void onDisable() {
        if (FakePlayer.mc.world != null && FakePlayer.mc.player != null) {
            super.onDisable();
            try {
                FakePlayer.mc.world.removeEntity(_fakePlayer);
            }
            catch (final Exception ex) {}
        }
    }
}
