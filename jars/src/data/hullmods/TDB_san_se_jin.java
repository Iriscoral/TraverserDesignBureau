package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.tdb.TDB_ColorData;


public class TDB_san_se_jin extends BaseHullMod {
    //使用你的监听器
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new TDB_Listener_SystemUse(ship, id));
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading("备注", Alignment.TMID, 4f);
        tooltip.addPara("三色堇专属插件", TDB_ColorData.TDBcolor1, 4f);
    }

}
