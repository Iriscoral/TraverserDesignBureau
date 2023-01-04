package data.shipsystems.scripts;

//import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import java.util.Map;
import java.util.WeakHashMap;

public class TDB_pao_ji extends BaseShipSystemScript {

    private static final Map<String, targetingMode> MODE = new WeakHashMap<>();
    private final String ZC = ("ZC");

    public enum targetingMode {
        ZC,
        PJ,
    }

    //模式切换相关
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {
            return;
        }

        if (effectLevel == 1) {
            targetingMode QH;
            if (MODE.containsKey(ship.getId())) {
                QH = MODE.get(ship.getId());
            } else {
                QH = targetingMode.ZC;
            }

            //模式关系
            switch (QH) {
                case ZC:
                    QH = targetingMode.PJ;
                    applyPJ(stats);
                    break;
                case PJ:
                    QH = targetingMode.ZC;
                    applyZC(stats);
                    break;
            }
            MODE.put(ship.getId(), QH);
        }
    }

    //当模式为ZC时
    private void applyZC(MutableShipStatsAPI stats) {
        stats.getMaxSpeed().modifyFlat(ZC, 1f);

        stats.getBallisticWeaponRangeBonus().unmodify(ZC);
        stats.getEnergyWeaponRangeBonus().unmodify(ZC);
    }

    //当模式为PJ时
    private void applyPJ(MutableShipStatsAPI stats) {
        stats.getBallisticWeaponRangeBonus().modifyPercent(ZC, 100f);
        stats.getEnergyWeaponRangeBonus().modifyPercent(ZC, 100f);

        stats.getMaxSpeed().unmodify(ZC);
    }

    //取消（重置）对应加成
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().unmodify(ZC);
        stats.getEnergyWeaponRangeBonus().unmodify(ZC);
        stats.getMaxSpeed().unmodify(id);

        String PJ = ("PJ");
        stats.getBallisticWeaponRangeBonus().unmodify(PJ);
        stats.getEnergyWeaponRangeBonus().unmodify(PJ);
    }
}
