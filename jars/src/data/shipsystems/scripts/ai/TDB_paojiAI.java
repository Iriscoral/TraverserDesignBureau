package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;


public class TDB_paojiAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private ShipSystemAPI system;
    private float maxWeaponRange = 1f;
    private static final float weaponRangeMult = 0.5f;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            if (weapon.getSlot() != null && weapon.getSlot().getSlotSize() == WeaponAPI.WeaponSize.MEDIUM) {
                maxWeaponRange = Math.max(weapon.getRange() * weaponRangeMult, maxWeaponRange);
            }
        }
    }

    //ai行为设置
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (target != null) {
            float distance = MathUtils.getDistance(ship, target);
            if (distance > 1000f && distance < 2000f) {
                if (!system.isActive()) {
                    ship.useSystem();
                }
            } else if (distance < 900f) {
                if (system.isActive()) {
                    ship.useSystem();
                }
            }
        }
    }
}