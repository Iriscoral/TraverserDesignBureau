package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicAnim;
import org.lazywizard.lazylib.MathUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDB_wan_shuang implements EveryFrameWeaponEffectPlugin {

    private static final Map<Integer, String> LEFT_SELECTOR = new HashMap<>();

    static {
        LEFT_SELECTOR.put(0, "TDB_wan_shuang_zuo");
    }

    private static final Map<Integer, String> RIGHT_SELECTOR = new HashMap<>();

    static {
        RIGHT_SELECTOR.put(0, "TDB_wan_shuang_you");
    }

    private WeaponAPI rpanel;
    private WeaponAPI lpanel;

    private ShipAPI ship;
    private ShipSystemAPI system;
    private ShipEngineControllerAPI engines;

    public final String lID = "WS0008";
    public final String rID = "WS0009";


    private boolean runOnce = false;

    private float panelWidth, panelHeight;

    private float rate = 1;
    private boolean travelDrive = false;


    private float currentRotateL = 0;
    private float currentRotateR = 0;


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (!runOnce || ship == null || system == null) {
            ship = weapon.getShip();

            system = ship.getSystem();
            engines = ship.getEngineController();
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons) {
                switch (w.getSlot().getId()) {

                    case lID:
                        lpanel = w;
                        panelHeight = w.getSprite().getHeight();
                        panelWidth = w.getSprite().getWidth();
                        break;
                    case rID:
                        rpanel = w;
                        break;
                }
            }


            runOnce = true;
            return;
        }


        float ltarget = 0;
        float rtarget = 0;

        float maxRotate = 22.5f;
        if (engines.isAccelerating()) {
            ltarget -= maxRotate / 2;
            rtarget += maxRotate / 2;
        } else if (engines.isDecelerating() || engines.isAcceleratingBackwards()) {
            ltarget += maxRotate;
            rtarget -= maxRotate;
        }
        if (engines.isStrafingLeft()) {
            ltarget += maxRotate / 3;
            rtarget += maxRotate / 1.5f;
        } else if (engines.isStrafingRight()) {
            ltarget -= maxRotate / 1.5f;
            rtarget -= maxRotate / 3;
        }
        if (engines.isTurningLeft()) {
            ltarget -= maxRotate / 2;
            rtarget -= maxRotate / 2;
        } else if (engines.isTurningRight()) {
            ltarget += maxRotate / 2;
            rtarget += maxRotate / 2;
        }

        float rtl = MathUtils.getShortestRotation(currentRotateL, ltarget);
        if (Math.abs(rtl) < 0.5f) {
            currentRotateL = ltarget;
        } else if (rtl > 0) {
            currentRotateL += 0.5f;
        } else {
            currentRotateL -= 0.5f;
        }

        float rtr = MathUtils.getShortestRotation(currentRotateR, rtarget);
        if (Math.abs(rtr) < 0.5f) {
            currentRotateR = rtarget;
        } else if (rtr > 0) {
            currentRotateR += 0.5f;
        } else {
            currentRotateR -= 0.5f;
        }


        if (ship.getTravelDrive().isActive() || ship.getFluxTracker().isVenting()) {
            rate = Math.min(1, rate + amount);
            travelDrive = true;
        } else if (travelDrive) {
            rate = Math.max(0, rate - amount);
            if (rate == 0) {
                travelDrive = false;
            }
        } else {
            rate = system.getEffectLevel();
        }


        if (system.isActive() || rate > 0) {


            float rotateDoors = MagicAnim.smoothNormalizeRange(rate, 0.25f, 0.75f);
            float recessDoors = MagicAnim.smoothNormalizeRange(rate, 0.5f, 1f);


            float panelOffsetX = 5;
            float lpX = panelWidth / 2 + panelOffsetX * recessDoors;
            float rpX = panelWidth / 2 - panelOffsetX * recessDoors;

            float panelOffsetY = 5;
            float pY = panelHeight / 2 + panelOffsetY * rotateDoors;

            lpanel.getSprite().setCenter(lpX, pY);
            rpanel.getSprite().setCenter(rpX, pY);


        }
    }
}