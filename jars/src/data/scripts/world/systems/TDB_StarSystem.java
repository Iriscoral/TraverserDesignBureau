package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicCampaign;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_QYData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import static com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;

public class TDB_StarSystem {

    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    public void generate(SectorAPI sector) {
        //create a star system 创建一个新的星系（名字）
        String systemName = "Indulge";
        StarSystemAPI system = sector.createStarSystem(systemName);
        //set its location 星系位置
        system.getLocation().set(13800f, -1000f);
        //set background image 星系背景图片
        system.setBackgroundTextureFilename("graphics/backgrounds/TDB_xing_xi.png");

        //the star 恒星大小（半径）（日冕大小）
        PlanetAPI star = system.initStar(systemName, "star_blue_giant", 800f, 350f);
        //background light color 背景光颜色
        system.setLightColor(new Color(142, 180, 234));

        //make asteroid belt surround it 让小行星带环绕它
        system.addAsteroidBelt(star, 100, 6200f, 150f, 180, 360, Terrain.ASTEROID_BELT, "");

        //a new planet for people 一个新的星球（给势力
        PlanetAPI planet1 = system.addPlanet("TDB_planet1", star, I18nUtil.getStarSystemsString("TDB_planet1_name"), "terran", 215, 180f, 4000f, 365f);

        //a new market for planet 设置星球市场
        MarketAPI planet1Market = addMarketplace(planet1, planet1.getName(),  // this number is size 设置殖民地规模
                new ArrayList<>(Arrays.asList(Conditions.POPULATION_7, // population, should be equal to size
                        Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.RUINS_WIDESPREAD)),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN,
                        "TDB_Market",
                        Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(Arrays.asList(Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.STARFORTRESS_HIGH,
                        Industries.HEAVYBATTERIES,
                        Industries.REFINING,
                        Industries.ORBITALWORKS,
                        Industries.WAYSTATION,
                        Industries.HIGHCOMMAND,
                        Industries.FUELPROD,
                        "TDB_wu_ren",
                        "TDB_SJJ",
                        "TDB_GroundDefenses")));
        //make a custom description which is specified in descriptions.csv    引用星球介绍位置
        planet1.setCustomDescriptionId("TDB_planet1_description");

        //give the orbital works a gamma core   给轨道工程一个伽马核心
        planet1Market.getIndustry(Industries.ORBITALWORKS).setAICoreId(Commodities.GAMMA_CORE);
        //and give it a nanoforge  给它一个纳米锻造炉
        planet1Market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));

        //then give designed command a blue core 给最高指挥部一个蓝色（阿尔法）ai核心
        planet1Market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);

        // generates hyperspace destinations for in-system jump points  为星系生成指定跳跃点
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("TDB_jump_point", txt("starsystem_1"));
        jumpPoint.setOrbit(Global.getFactory().createCircularOrbit(planet1, 100f, 700f, 30f));
        jumpPoint.setRelatedPlanet(planet1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        //扫描本星系所有跳跃点并为之配置数据
        system.autogenerateHyperspaceJumpPoints(true, false);
        //勘探母舰生成
        SectorEntityToken TDBSurvey_ship = system.addCustomEntity("TDB_Survey_ship", txt("starsystem_2"), "TDBSurvey_ship", "neutral");
        TDBSurvey_ship.setCircularOrbitPointingDown(star, 45 + 10, 1600, 250);
        TDBSurvey_ship.setCustomDescriptionId("TDB_Survey_ship");
        Misc.setAbandonedStationMarket("TDB_abandoned_station_market", TDBSurvey_ship);

        //母星空间站生成
        SectorEntityToken TDBStation = system.addCustomEntity("TDB_Station", txt("starsystem_3"), "station_TDB_type", "TDB");
        TDBStation.setCircularOrbitPointingDown(system.getEntityById("TDB_planet1"), 45 + 180, 360, 30);
        TDBStation.setCustomDescriptionId("TDB_station");
        TDBStation.setMarket(planet1Market);

        planet1Market.getConnectedEntities().add(TDBStation);

        //生成自家特殊舰队
        this.addFleet(planet1);

        //生成遗弃舰
        TTBlackSite.addDerelict(system, planet1, "TDB_gugu_variant", txt("starsystem_4"), "TDB_kite", ShipRecoverySpecial.ShipCondition.BATTERED, planet1.getRadius() * 2.0F, Math.random() < 0.1D);

        //生成自家的轨道防御系统
        //SectorEntityToken stationForA = system.addCustomEntity("TDB_jdgdfyA", (String)null, "TDB_jdgdfy", "TDB");
        //stationForA.setCircularOrbitPointingDown(star, 215, 4000f, 365f);

        //生成星门
        SectorEntityToken gate = system.addCustomEntity("TDB_gate", // unique id 设置星门id
                txt("starsystem_gate"), // name - if null, defaultName from custom_entities.json will be used 设置你星门的名字
                "inactive_gate", // type of object, defined in custom_entities.json 设置标签（让系统识别这是个星门）根据custom_entities.json设置
                "TDB"); // faction
        gate.setCircularOrbit(system.getEntityById("Indulge"), 0, 3180, 350);

        //设置你星系的永久稳定点建筑
        SectorEntityToken A = system.addCustomEntity("TDB_A", txt("starsystem_5"), "comm_relay", "TDB");
        A.setCircularOrbit(star, 180f, 3000f, 365f);
        SectorEntityToken B = system.addCustomEntity("TDB_B", txt("starsystem_6"), "nav_buoy", "TDB");
        B.setCircularOrbit(star, 220f, 3000f, 365f);
        SectorEntityToken C = system.addCustomEntity("TDB_C", txt("starsystem_7"), "sensor_array", "TDB");
        C.setCircularOrbit(star, 240f, 3000f, 365f);

        // Debris 生成残骸
        DebrisFieldParams params = new DebrisFieldParams(250f, // field radius - should not go above 1000 for performance reasons 残骸半径-出于性能原因，不应超过1000
                1f, // density, visual - affects number of debris pieces  密度，视觉-影响碎片数量
                10000000f, // duration in days 持续时间（天）
                10f); // days the field will keep generating glowing pieces
        params.source = DebrisFieldSource.MIXED;
        params.baseSalvageXP = 500; // base XP for scavenging in field 用于现场清理的基本XP
        SectorEntityToken debris = Misc.addDebrisField(system, params, StarSystemGenerator.random);
        SalvageSpecialAssigner.assignSpecialForDebrisField(debris);

        // makes the debris field always visible on map/sensors and not give any xp or notification on being discovered使碎片区域在地图/传感器上始终可见，并且不会在被发现时发出任何xp或通知
        debris.setSensorProfile(null);
        debris.setDiscoverable(null);

        // makes it discoverable and give 200 xp on being found 使其可被发现，并在被发现时提供200 xp
        // sets the range at which it can be detected (as a sensor contact) to 4000 units将可检测到的范围（传感器）设置为4000个单位
        // commented out.
        debris.setDiscoverable(true);
        debris.setDiscoveryXP(200f);
        debris.setSensorProfile(1f);
        debris.getDetectedRangeMod().modifyFlat("gen", 4000);
        debris.setCircularOrbit(star, 45 + 10, 1600, 250);

        //Finally cleans up hyperspace 清理超空间（？
        MagicCampaign.hyperspaceCleanup(system);
    }

    private static MarketAPI addMarketplace(SectorEntityToken primaryEntity, String name, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, 7);
        newMarket.setFactionId("TDB");
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", (float) 0.3);

        //Adds submarkets   添加子市场
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions  增加了市场条件
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(false);


        globalEconomy.addMarket(newMarket, true);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction("TDB");

        //Finally, return the newly-generated market
        return newMarket;
    }

    public void addFleet(SectorEntityToken rock) {
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("TDB", FleetTypes.TASK_FORCE, null);
        fleet.setName(txt("starsystem_8"));
        fleet.setNoFactionInName(true);
        fleet.setId("TDB_GuGu");
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true); // so it keeps transponder on
        fleet.getMemoryWithoutUpdate().set("$TDBGuGu",true);

        fleet.getFleetData().addFleetMember("TDB_gugu_variant");
        fleet.getFleetData().ensureHasFlagship();

        FleetMemberAPI xyship = fleet.getFleetData().addFleetMember("TDB_yuan_wei_variant");
        xyship.setShipName(txt("starsystem_9"));
        xyship.setCaptain(TDB_QYData.createXianYu());

        FleetMemberAPI lxship = fleet.getFleetData().addFleetMember("TDB_ceng_ji_yun_luo_xue");
        lxship.setShipName(txt("starsystem_10"));
        lxship.setCaptain(TDB_QYData.createLuoXue());

        FleetMemberAPI yrship = fleet.getFleetData().addFleetMember("TDB_tai_yang_yu_yi_er");
        yrship.setShipName("Rainvader");
        yrship.setCaptain(TDB_QYData.createYiRe());

        FleetMemberAPI yfship = fleet.getFleetData().addFleetMember("TDB_ji_liu_yi_fu");
        yfship.setShipName("Cheese Fox");
        yfship.setCaptain(TDB_QYData.createYiFu());

        FleetMemberAPI xkship = fleet.getFleetData().addFleetMember("TDB_ji_yu_yun_XK");
        xkship.setShipName(txt("starsystem_11"));
        xkship.setCaptain(TDB_QYData.createXingKong());

        FleetMemberAPI hmship = fleet.getFleetData().addFleetMember("TDB_gu_yu_HM");
        hmship.setShipName(txt("starsystem_12"));
        hmship.setCaptain(TDB_QYData.createHuoMao());

        FleetMemberAPI qbeship = fleet.getFleetData().addFleetMember("TDB_gu_yu_variant");
        qbeship.setShipName(txt("starsystem_13"));
        qbeship.setCaptain(TDB_QYData.create782());


        //fleet.addFloatingText("这是一个咕咕文本", TDB_ColorData.TDBred ,100);

//		fleet.addAbility(Abilities.TRANSPONDER);
        fleet.getAbility(Abilities.TRANSPONDER).activate();

        // so it never shows as "Unidentified Fleet" but isn't easier to spot due to using the actual transponder ability
        fleet.setTransponderOn(true);

        PersonAPI person = TDB_QYData.createGuGu();
        fleet.setCommander(person);

        FleetMemberAPI flagship = fleet.getFlagship();
        flagship.setCaptain(person);
        flagship.updateStats();
        flagship.getRepairTracker().setCR(flagship.getRepairTracker().getMaxCR());
        flagship.setShipName(txt("starsystem_14"));

        // to "perm" the variant so it gets saved and not recreated from the "ziggurat_Experimental" id
        flagship.setVariant(flagship.getVariant().clone(), false, false);
        flagship.getVariant().setSource(VariantSource.REFIT);

        rock.getContainingLocation().addEntity(fleet);

        fleet.addScript(new TDB_AssignmentAI(fleet, rock));

    }


}
