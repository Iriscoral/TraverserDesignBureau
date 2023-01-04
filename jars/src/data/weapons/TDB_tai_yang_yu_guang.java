package data.weapons;

import com.fs.starfarer.api.combat.*;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class TDB_tai_yang_yu_guang implements EveryFrameWeaponEffectPlugin {


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        ShipSystemAPI system = ship.getSystem();

        if (system != null)
        {
            if (ship.getSystem().isActive() && !engine.isPaused()) {
                weapon.getSprite().setColor(TDB_ColorData.TDBpurplish);

                //电弧特效前置设置
                float angle;
                float radius;
                angle = 360f * (float) Math.random();
                radius = MathUtils.getRandomNumberInRange(2.7f, 3.5f);
                Vector2f point = weapon.getLocation();
                Vector2f Point1 = MathUtils.getPointOnCircumference(weapon.getLocation(), radius * 0.5f, MathUtils.clampAngle(angle + MathUtils.getRandomNumberInRange(35f, 70f)));

                float emp =(float) Math.random();

                if (emp>0.8f)
                {
                    //生成电弧
                    engine.spawnEmpArc(ship, point, null, new SimpleEntity(Point1), DamageType.ENERGY, 0f, 0f, 10000f, null, 0, TDB_ColorData.TDBpurplish, TDB_ColorData.TDBpurplish2);
                }
                engine.addSwirlyNebulaParticle(weapon.getLocation(), I18nUtil.nv, (float) MathUtils.getRandomNumberInRange(0, 35), MathUtils.getRandomNumberInRange(1f, 3f), 0,0,1, TDB_ColorData.TDBpurplish,true);
            }
        }
    }
}