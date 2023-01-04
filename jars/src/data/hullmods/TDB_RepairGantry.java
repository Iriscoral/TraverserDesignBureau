package data.hullmods;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class TDB_RepairGantry extends BaseHullMod {

    //货船的集成起重机（仅仅修改了部分参数其余和原版一致

    private static final Map<HullSize, Float> mag = new HashMap<>();

    static {
        mag.put(HullSize.FRIGATE, 25f);
        mag.put(HullSize.DESTROYER, 25f);
        mag.put(HullSize.CRUISER, 25f);
        mag.put(HullSize.CAPITAL_SHIP, 25f);
    }

    public static final float BATTLE_SALVAGE_MULT = .2f;
    public static final float MIN_CR = 0.1f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //stats.getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD).modifyFlat(id, SALVAGE_MODIFIER);
        stats.getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD).modifyFlat(id, mag.get(hullSize) * 0.01f);
        //stats.getDynamic().getMod(Stats.BATTLE_SALVAGE_VALUE_MULT_MOD).modifyFlat(id, (Float) mag.get(hullSize) * 0.01f * (Float) mag.get(hullSize) * 0.01f);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        //if (index == 0) return "" + (int) (SALVAGE_MODIFIER * 100f);
        //if (index == 1) return "" + (int) (BATTLE_SALVAGE_MODIFIER * 100f);

        if (index == 0) return (mag.get(HullSize.FRIGATE)).intValue() + "%";
        if (index == 1) return (mag.get(HullSize.DESTROYER)).intValue() + "%";
        if (index == 2) return (mag.get(HullSize.CRUISER)).intValue() + "%";
        if (index == 3) return (mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
        if (index == 4) return Math.round(BATTLE_SALVAGE_MULT * 100f) + "%";

        return null;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

    }


    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return true;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

    }

    public static float getAdjustedGantryModifier(CampaignFleetAPI fleet, String skipId, float add) {
        //List<Pair<FleetMemberAPI, Float>> values = new ArrayList<Pair<FleetMemberAPI,Float>>();

        float max = 0f;
        float total = 0f;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (member.isMothballed()) continue;
            if (member.getRepairTracker().getCR() < MIN_CR) continue;

            if (member.getId().equals(skipId)) {
                continue;
            }
            float v = member.getStats().getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD).computeEffective(0f);
            if (v <= 0) continue;

//			Pair<FleetMemberAPI, Float> p = new Pair<FleetMemberAPI, Float>(member, v);
//			values.add(p);
            if (v > max) max = v;
            total += v;
        }
        if (add > max) max = add;
        total += add;

        if (max <= 0) return 0f;
        float units = total / max;
        if (units <= 1) return max;
        float mult = Misc.logOfBase(2.5f, units) + 1f;
        float result = total * mult / units;
        if (result <= 0) {
            result = 0;
        } else {
            result = Math.round(result * 100f) / 100f;
            result = Math.max(result, 0.01f);
        }
        return result;
    }

}









