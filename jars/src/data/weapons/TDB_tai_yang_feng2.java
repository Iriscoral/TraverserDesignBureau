package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;


public class TDB_tai_yang_feng2 implements EveryFrameWeaponEffectPlugin {


    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (weapon.getChargeLevel() > 0f) {
            Vector2f weaponLocation = weapon.getLocation();
            ShipAPI ship = weapon.getShip();
            float weaponca = weapon.getCurrAngle();

            float size = MathUtils.getRandomNumberInRange(3f, 6f);
            float angle = MathUtils.getRandomNumberInRange(-90f, 90f);

            Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
            Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);

            engine.addHitParticle(loc, lvel, size, 2f * weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.1f, 0.3f), TDB_ColorData.TDBblue);
        }

        if (!engine.isPaused() && weapon.getShip().getOriginalOwner() != -1) {
            if (MagicRender.screenCheck(0.25f, weapon.getLocation()) && weapon.getChargeLevel() == 1f) {

                for (DamagingProjectileAPI poj : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 100f)) {
                    if (poj == null) {
                        break;
                    }

                        Vector2f vel = MathUtils.getRandomPointInCone(I18nUtil.nv, (float) 5, poj.getFacing() - 2f, poj.getFacing() + 10f);
                        Vector2f pos = new Vector2f(poj.getLocation());
                        Vector2f.add(pos, vel, pos);
                        Vector2f.add(vel, poj.getSource().getVelocity(), vel);
                        //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));

                        MagicRender.battlespace(
                                Global.getSettings().getSprite("fx", "TDB_shuang_hen"),
                                pos,
                                vel,
                                new Vector2f(88, 88),
                                new Vector2f(88, 88),
                                //angle,
                                weapon.getCurrAngle() + 90,
                                0,
                                TDB_ColorData.TDBwhite,
                                true,
                                0,
                                0.1f,
                                0.6f
                        );
                }

                //生成炮弹用于假装抛弹壳
				/*float angle = weapon.getCurrAngle();
				ShipAPI ship = weapon.getShip();
				Global.getCombatEngine().spawnProjectile(poj.getSource(), poj.getWeapon(), "TDB_han_chao", weapon.getLocation(), angle-A, ship.getVelocity());

				 */
            }
        }
    }
}