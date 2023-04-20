package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import data.scripts.util.MagicRender;
import java.util.List;

import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.combat.CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER;

public class TDBCombatEveryFram extends BaseEveryFrameCombatPlugin {
    private static CombatEngineAPI engine;

    public void advance(float amount, List<InputEventAPI> events)
    {
        if ((this.engine == null) || (this.engine.isPaused())) return;
        for (MissileAPI missile : this.engine.getMissiles())
        {
            if (missile.getProjectileSpecId().equals("TDB_duan_hen2") || missile.getProjectileSpecId().equals("TDB_foehn"))
            {
                Vector2f nv1 = new Vector2f(300,40);
                SpriteAPI l = Global.getSettings().getSprite("campaignEntities","fusion_lamp_glow");
                MagicRender.singleframe(l,missile.getLocation(),nv1,0, TDB_ColorData.TDBblue2,true,ABOVE_SHIPS_AND_MISSILES_LAYER);
            }
        }

    }

    public void renderInUICoords(ViewportAPI viewport) {
    }

    public void init(CombatEngineAPI engine)
    {
        this.engine = engine;
    }

    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {

    }

}
