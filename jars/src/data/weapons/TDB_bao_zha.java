package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;

import static data.utils.tdb.I18nUtil.nv;
import static data.utils.tdb.TDB_ColorData.TDBwhite;

public class TDB_bao_zha implements OnHitEffectPlugin {


    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        int Damage = 500;
        //    概率                              打盾上               目标舰船
        if ((float) Math.random() > 0f && !shieldHit && target instanceof ShipAPI) {
            engine.applyDamage(target, point, Damage, DamageType.FRAGMENTATION, 0, false, false, 1);

            //ship.getFluxTracker().showOverloadFloatyIfNeeded(Damage + "{", new Color(49, 135, 255,255),4,true);


            engine.spawnExplosion(point,
                    nv,
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
                engine.applyDamage(target, point, 500, DamageType.HIGH_EXPLOSIVE, 0, false, false, 1);
            }
        }


    }


}
