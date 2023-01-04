package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.utils.tdb.TDB_ColorData;

import java.util.HashSet;
import java.util.Set;

public class TDB_Automated extends BaseHullMod {
    public static final float CREW_MOD = 60f;
    public static final float REPAIR_BONUS = 20f;
    public static final float MAX_CREW_MOD = 50f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //设定舰船的最少/最多船员数量
        stats.getMinCrewMod().modifyMult(id, 0.4f);
        stats.getMaxCrewMod().modifyMult(id, 0.5f);
        //设定引擎/武器维修速度
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) return (int) CREW_MOD + "%";
        if (index == 1) return (int) MAX_CREW_MOD + "%";
        if (index == 2) return (int) REPAIR_BONUS + "%";
        return null;
    }

    //检测前置插件/检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("autorepair");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_Automated");
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng) && !ship.getVariant().getHullMods().contains("autorepair");
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)) {
            return "只能安装在穿越者协会的船体上";
        }
        if (ship.getVariant().getHullMods().contains("autorepair")) {
            return "与自动修复模块[AutomatedRepairUnit]冲突";
        }
        return "状态正常";
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading("备注", Alignment.TMID, 4f);
        tooltip.addPara("只能安装在穿越者协会的船体上", TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara("与[%s]冲突", 4f, Misc.getHighlightColor(), TDB_ColorData.TDBred, "自动修复模块");
    }
}
