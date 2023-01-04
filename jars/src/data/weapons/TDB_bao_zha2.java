package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.utils.tdb.I18nUtil.nv;
import static data.utils.tdb.TDB_ColorData.TDBwhite;

public class TDB_bao_zha2 implements OnHitEffectPlugin , EveryFrameWeaponEffectPlugin{

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        DamagingProjectileAPI e = engine.spawnDamagingExplosion(createExplosionSpec(),projectile.getSource(), point);
        e.addDamagedAlready(target);

        int Damage = 800;
        //    概率                              打盾上               目标舰船
        if ((float) Math.random() > 0f && !shieldHit && target instanceof ShipAPI) {
            engine.applyDamage(target, point, Damage, DamageType.FRAGMENTATION, 0, false, false, 1);
            //ship.getFluxTracker().showOverloadFloatyIfNeeded(Damage + "{", new Color(49, 135, 255,255),4,true);


            engine.spawnExplosion(point,
                    new Vector2f(0, 0),
                    TDBwhite,
                    12.5f,
                    0.3f);

            engine.addSmoothParticle(
                    point,
                    nv,
                    100f,
                    2f,
                    0.15f,
                    TDB_ColorData.TDBcyan);


            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "TDB_na_mi"),
                    point,
                    I18nUtil.nv,
                    new Vector2f(88, 88),
                    new Vector2f(88, 88),
                    360 * (float) Math.random(),
                    0f,
                    TDB_ColorData.TDBwhite,
                    true,
                    0,
                    0.1f,
                    0.6f
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "TDB_na_mi"),
                    point,
                    I18nUtil.nv,
                    new Vector2f(48, 48),
                    new Vector2f(48, 48),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    TDB_ColorData.TDBblue2,
                    true,
                    0.2f,
                    0f,
                    0.3f
            );

            if (((ShipAPI) target).isHulk())
            {
                engine.applyDamage(target, point, 1000, DamageType.HIGH_EXPLOSIVE, 0, false, false, 1);
            }
        }
    }

    public DamagingExplosionSpec createExplosionSpec() {
        float damage = 300f;
        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.1f, // duration
                150f, // radius
                100f, // coreRadius
                damage, // maxDamage
                damage/2, // minDamage
                CollisionClass.PROJECTILE_FF, // collisionClass
                CollisionClass.PROJECTILE_NO_FF, // collisionClassByFighter
                5f, // particleSizeMin
                3f, // particleSizeRange
                0.5f, // particleDuration
                150, // particleCount
                new Color(255, 224,100, 255), // particleColor
                new Color(255, 224,100, 36)  // explosionColor
        );
        spec.setDamageType(DamageType.HIGH_EXPLOSIVE);
        spec.setUseDetailedExplosion(false);
        return spec;
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        float[] angles = new float[]{-90f, 90f};

        if (weapon.isFiring()) {
            for (float Angle : angles) {
                for (int i = 0; i < 5; ++i) {
                    float spawnangle = weapon.getCurrAngle() + Angle;
                    float Point = MathUtils.getRandomNumberInRange(spawnangle - 20f, spawnangle + 20f);

                    Vector2f vel = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(5f, 50f), Point);
                    Vector2f spawnLocation = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(16f, 20f), Point);
                    engine.addSwirlyNebulaParticle(spawnLocation, vel, MathUtils.getRandomNumberInRange(10f, 15f), MathUtils.getRandomNumberInRange(0.1f, 0.5f), 0f, 0f, 1f, TDB_ColorData.TDByellow2, true);
                }
            }
        }
    }
}
