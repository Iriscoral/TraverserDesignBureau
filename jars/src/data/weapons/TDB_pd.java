package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.utils.tdb.I18nUtil.nv;
import static data.utils.tdb.TDB_ColorData.TDBwhite;

public class TDB_pd implements EveryFrameWeaponEffectPlugin{
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (weapon.isFiring()) {
            for (MissileAPI missile : CombatUtils.getMissilesWithinRange(weapon.getLocation(), 500f))
            {
                if (missile==null||missile.getOwner() == weapon.getShip().getOwner())
                {
                    break;
                }

                missile.setOwner(weapon.getShip().getOwner());
                missile.setJitter(missile.getSource(),TDB_ColorData.TDBred,2,3,5);
            }
        }
    }
}
