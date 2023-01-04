package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.utils.tdb.TDB_ColorData;

import java.util.HashSet;
import java.util.Set;

public class TDB_na_mi extends BaseHullMod {

    public static final float ZY = 200;


    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //电子战
        stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, 5f);
        //人员归0
        stats.getMinCrewMod().modifyMult(id, 0);
        //地面支援
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, ZY);

    }


    //让文本用%能检测到数值
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int) ZY;
        if (index == 1) return "5" + "%";
        return null;
    }

    //检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("ecm");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_na_mi");
            }
        }
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (ship.getVariant().getHullMods().contains("ecm")) {
            return "灰风的电子战系统非常复杂我们无法对其进行改造";
        }
        return "状态正常";
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading("备注", Alignment.TMID, 4f);
        tooltip.addPara("灰风的专属插件", TDB_ColorData.TDBgreen2, 4f);
        tooltip.addPara("与[%s]冲突", 4f, Misc.getHighlightColor(), TDB_ColorData.TDBred, "电子对抗组件");
    }

}
