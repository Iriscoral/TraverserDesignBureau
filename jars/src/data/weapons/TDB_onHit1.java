package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.util.Misc.ZERO;
import static data.utils.tdb.I18nUtil.nv;

public class TDB_onHit1 implements OnHitEffectPlugin {


    private final float A;

    public TDB_onHit1() {
        this.A = 360f;
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        engine.addSmoothParticle(point, ZERO, 200f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(point, ZERO, 150f, 0.5f, 0.25f, TDB_ColorData.TDBblue);

        float angle = projectile.getWeapon().getCurrAngle();

        engine.spawnExplosion(point, nv, TDB_ColorData.TDBblue, 100f, 1.5f);

        for (int x = 0; x < 5; ++x) {
            engine.addNebulaParticle(point, MathUtils.getPointOnCircumference(null, 50f, MathUtils.getRandomNumberInRange(angle + A, angle - A)), 50, 0.5f, 0.25f, 0f, 1.5F, TDB_ColorData.TDBblue3);
        }

        if ((float) Math.random() > 0.7f && !shieldHit && target instanceof ShipAPI) {
            //emp根据弹丸emp量获取
            float emp = projectile.getEmpAmount();
            //根据弹丸伤害获取伤害
            float dam = 100;
            //生成emp效果
            engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                    //伤害类型为能量
                    DamageType.ENERGY,
                    dam,
                    emp, // emp
                    //最大范围
                    100000f,
                    "tachyon_lance_emp_impact",
                    30f,
                    //颜色
                    new Color(25, 100, 155, 255),
                    TDB_ColorData.TDBwhite
            );

        }
    }
}

