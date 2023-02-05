package data.characters.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class TDB_HF {

    public static final float ARMOR_DAMAGE_REDUCTION = 10f;
    public static final float ARMOR_KINETIC_REDUCTION = 20f;

    public static final float DAMAGE_TO_MODULES_REDUCTION = 25;

    public static final float SHIELD_DAMAGE_REDUCTION = 5f;
    public static final float SHIELD_HE_REDUCTION = 10f;

    public static String txt(String id) {
        return Global.getSettings().getString("skills", id);
    }

    public static class Level1 implements ShipSkillEffect {
        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_DAMAGE_REDUCTION / 100f);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getShieldDamageTakenMult().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (SHIELD_DAMAGE_REDUCTION) + txt("SKILL_HF1");
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }

    public static class Level2 implements ShipSkillEffect {

        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getArmorDamageTakenMult().modifyMult(id, 1f - ARMOR_DAMAGE_REDUCTION / 100f);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getArmorDamageTakenMult().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (ARMOR_DAMAGE_REDUCTION) + txt("SKILL_HF1");
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }

    public static class Level3 implements ShipSkillEffect {

        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getKineticArmorDamageTakenMult().modifyMult(id, 1f - ARMOR_KINETIC_REDUCTION / 100f);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getKineticArmorDamageTakenMult().unmodify(id);
            stats.getHardFluxDissipationFraction().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (ARMOR_KINETIC_REDUCTION) + txt("SKILL_HF1");
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }

    public static class Level4 implements ShipSkillEffect {

        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getEngineDamageTakenMult().modifyMult(id, 1f - DAMAGE_TO_MODULES_REDUCTION / 100f);
            stats.getWeaponDamageTakenMult().modifyMult(id, 1f - DAMAGE_TO_MODULES_REDUCTION / 100f);
            stats.getHighExplosiveShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_HE_REDUCTION / 100f);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getEngineDamageTakenMult().unmodify(id);
            stats.getWeaponDamageTakenMult().unmodify(id);
            stats.getHighExplosiveShieldDamageTakenMult().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (DAMAGE_TO_MODULES_REDUCTION) + txt("SKILL_HF2") + (int) (SHIELD_HE_REDUCTION) + txt("SKILL_HF3");
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
}
