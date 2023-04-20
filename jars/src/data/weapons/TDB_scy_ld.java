package data.weapons;

import com.fs.starfarer.api.combat.*;

import java.util.HashMap;
import java.util.Map;

public class TDB_scy_ld implements EveryFrameWeaponEffectPlugin {

    private float angle = 0f;

    private static final Map<String, Float> ROTATE_SPEED = new HashMap<>();

    static {
        ROTATE_SPEED.put("TDB_scy_ld", -70f);
        //ROTATE_SPEED.put("xxx", 256f);
        //ROTATE_SPEED.put("xxx", 20f);
        //ROTATE_SPEED.put("武器id2", 转速f);
        //ROTATE_SPEED.put("武器id3", 转速f);
        //ROTATE_SPEED.put("武器id4", 转速f);
    }


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();

        if (ship == null || !ship.isAlive()) {
            return;
        }

        if (ship.getSystem() != null && !engine.isPaused())
        {
            if (ship.getSystem().isOn()) {
                if (weapon.getAnimation().getFrame() < weapon.getAnimation().getNumFrames() - 1) {
                    weapon.getAnimation().setFrameRate(7f);
                } else {
                    weapon.getAnimation().setFrameRate(0f);
                    weapon.getAnimation().setFrame(weapon.getAnimation().getNumFrames() - 1);
                }
            } else
            {
                if (weapon.getAnimation().getFrame() > 0) {
                    weapon.getAnimation().setFrameRate(-7f);
                } else {
                    weapon.getAnimation().setFrameRate(0f);
                    weapon.getAnimation().setFrame(0);
                }
            }
        }

        if (!engine.isPaused() && !weapon.isDisabled()) {

            angle += ROTATE_SPEED.get(weapon.getSpec().getWeaponId()) * amount;
            angle += ship.getAngularVelocity() * amount;
            if (angle > 360f) angle -= 360f;
            weapon.setCurrAngle(angle);
        }
    }
}