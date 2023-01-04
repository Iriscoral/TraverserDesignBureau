package data.hullmods;

//import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import data.scripts.util.MagicIncompatibleHullmods;
//import com.fs.starfarer.api.combat.ShipHullSpecAPI;
//import com.fs.starfarer.api.ui.Alignment;
//import com.fs.starfarer.api.ui.TooltipMakerAPI;
//import com.fs.starfarer.api.util.Misc;
//import data.utils.tdb.TDB_ColorData;

import java.util.HashSet;
import java.util.Set;

import static data.utils.tdb.TDB_ColorData.TDBcolor1;

///////////////////////////////////
//此插件原为配合_G切换舰船技能，现已废弃//
///////////////////////////////////

public class TDB_qiong_ding extends BaseHullMod {

//    public static final String TDB_qiong_ding = "TDB_qiong_ding";

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        /*if (stats.getEntity() != null && ((ShipAPI) stats.getEntity()).getHullSpec() != null) {
            //如果没有改变舰船“皮肤”的插件，则吧舰船的皮肤设定为XXX
            if (!((ShipAPI) stats.getEntity()).getVariant().hasHullMod("TDB_qiong_ding_G")) {

                ShipHullSpecAPI ship = Global.getSettings().getHullSpec("TDB_qiong_ding_T");
                ((ShipAPI) stats.getEntity()).getVariant().setHullSpecAPI(ship);

            }
        }*/
    }

    //检测前置插件/检测冲突插件
    private final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    {
        BLOCKED_HULLMODS.add("neural_interface");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "neural_interface");
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return (ship.getHullSpec().getHullId().startsWith("TDB_")) &&!ship.getVariant().getHullMods().contains("neural_interface");
    }

   /* public static String getNonDHullId(ShipHullSpecAPI spec) {
        if (spec == null) {
            return null;
        } else {
            return spec.getDParentHullId() != null && !spec.getDParentHullId().isEmpty() ? spec.getDParentHullId() : spec.getHullId();
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;
        if (ship == null) return;
        TooltipMakerAPI text;
        tooltip.addPara("#根据不同协议改变部署炮台的类型", TDBcolor1, 4f);
        tooltip.addSectionHeading("特殊", Alignment.TMID, 4f);
        if (!ship.getVariant().hasHullMod("TDB_qiong_ding_G")) {
            text = tooltip.beginImageWithText("graphics/hullmods/TDB_tian_qiong.png", 32);
            text.addPara("[%s]", 0, Misc.getHighlightColor(), TDB_ColorData.TDBblue, "防御模式");
            text.addPara("舰船将部署防御炮台", 4f);
        } else {
            text = tooltip.beginImageWithText("graphics/hullmods/TDB_g.png", 32);
            text.addPara("[%s]", 0, Misc.getHighlightColor(), TDB_ColorData.TDBred, "反击模式");
            text.addPara("舰船将部署攻击炮台", 4f);
        }
        tooltip.addImageWithText(pad);
    }*/
}
