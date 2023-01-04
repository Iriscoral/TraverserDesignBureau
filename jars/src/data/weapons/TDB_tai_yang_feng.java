package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class TDB_tai_yang_feng implements OnFireEffectPlugin, OnHitEffectPlugin {

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship = weapon.getShip();

        Vector2f weaponLocation = weapon.getLocation();
        Vector2f shipVelocity = ship.getVelocity();

        engine.spawnExplosion(weaponLocation, shipVelocity, TDB_ColorData.TDBblue3, 50f, 0.15f);
        engine.addSmoothParticle(weaponLocation, shipVelocity, 50f * 3f, 1f, 0.15f * 2f, TDB_ColorData.TDBblue3);


    }

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        for (int a = 0; a < 6; a++) {
            //电弧特效前置设置
            float angle;
            float radiusMult;
            angle = 360f * (float) Math.random();
            radiusMult = MathUtils.getRandomNumberInRange(2.7f, 3.5f);
            point = MathUtils.getPointOnCircumference(projectile.getLocation(), 105f * radiusMult * 0.5f, angle);
            Vector2f Point1 = MathUtils.getPointOnCircumference(projectile.getLocation(), 105f * radiusMult * 0.5f, MathUtils.clampAngle(angle + MathUtils.getRandomNumberInRange(35f, 70f)));
            //生成电弧
            engine.spawnEmpArc(projectile.getSource(), point, null, new SimpleEntity(Point1), DamageType.ENERGY, 0f, 0f, 10000f, null, 0.2f, TDB_ColorData.TDBblue3, TDB_ColorData.TDBblue3);
        }
    }

}
