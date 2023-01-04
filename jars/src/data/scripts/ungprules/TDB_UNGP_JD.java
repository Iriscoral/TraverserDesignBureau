package data.scripts.ungprules;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.campaign.specialist.UNGP_SpecialistSettings.Difficulty;
import data.scripts.ungprules.impl.UNGP_BaseRuleEffect;
import data.scripts.ungprules.tags.UNGP_CombatTag;

public class TDB_UNGP_JD extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    public static final float JD = 40f;

    @Override
    public float getValueByDifficulty(int index, Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(10f, 30f);
        return 0f;
    }

    @Override
    public String getDescriptionParams(int index, Difficulty difficulty) {
        if (index == 0) return "" + (int) JD + "%";
        return super.getDescriptionParams(index, difficulty);
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        ship.getMutableStats().getAcceleration().modifyPercent(buffID, 1 * JD);
        ship.getMutableStats().getTurnAcceleration().modifyPercent(buffID, 1 * JD);
        ship.getMutableStats().getDeceleration().modifyPercent(buffID, 1 * JD);
        ship.getMutableStats().getMaxTurnRate().modifyPercent(buffID, 1 * JD);
    }
}