package data.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.scripts.util.MagicLensFlare;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;


public class TDB_onHit2 implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 200, 0, TDB_ColorData.TDBpink, TDB_ColorData.TDBpurplish_red);

    }
}

