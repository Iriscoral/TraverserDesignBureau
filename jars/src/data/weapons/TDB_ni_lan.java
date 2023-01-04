package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.scripts.util.MagicLensFlare;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;


public class TDB_ni_lan implements OnHitEffectPlugin , OnFireEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 200, 0, TDB_ColorData.TDBpink, TDB_ColorData.TDBpurplish_red);

    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship = weapon.getShip();
        Vector2f weaponLocation = weapon.getLocation();
        Vector2f shipVelocity = ship.getVelocity();
        engine.spawnExplosion(weaponLocation, shipVelocity, TDB_ColorData.TDBblue3, 50f, 0.15f);
    }
}

