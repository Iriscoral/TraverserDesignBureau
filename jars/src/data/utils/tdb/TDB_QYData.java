package data.utils.tdb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import static data.utils.tdb.I18nUtil.getString;

////////////////////////
///这是一个群友军官数据库///
////////////////////////

public class TDB_QYData {
    //群友：鸽子
    public static PersonAPI createGuGu() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("", "Liteve", FullName.Gender.FEMALE));
        person.setFaction("TDB");
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_GuGu"));
        person.setPersonality(Personalities.RECKLESS);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId(null);
        person.setId("TDB_GuGu");

        person.getStats().setSkipRefresh(true);

        person.getStats().setLevel(11);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);

        person.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
        person.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
        person.getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 1);
        person.getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
        person.getStats().setSkillLevel(Skills.CARRIER_GROUP, 1);
        person.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);

        person.getStats().setSkipRefresh(false);

        Global.getSector().getImportantPeople().addPerson(person);

        return person;
    }

    //群友：落雪
    public static PersonAPI createLuoXue() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("", getString("utils", "QYData_1"), FullName.Gender.FEMALE));
        person.setFaction("TDB");
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_03"));
        person.setPersonality(Personalities.STEADY);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId("TDB_LuoXue");
        person.setId("TDB_LuoXue");

        person.getStats().setSkipRefresh(true);

        person.getStats().setLevel(6);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);

        person.getStats().setSkipRefresh(false);

        Global.getSector().getImportantPeople().addPerson(person);

        return person;
    }

    //群友：肃正子模块-伊尔乌斯
    public static PersonAPI createYiRe() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("", getString("utils", "QYData_2"), FullName.Gender.ANY));
        person.setFaction("TDB");
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_01"));
        person.setPersonality(Personalities.CAUTIOUS);
        person.setRankId(getString("utils", "QYData_3"));
        person.setPostId("TDB_YiRe");
        person.setId("TDB_YiRe");

        person.getStats().setSkipRefresh(true);

        person.getStats().setLevel(6);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);

        person.getStats().setSkipRefresh(false);

        Global.getSector().getImportantPeople().addPerson(person);

        return person;
    }

    //群友：伊芙·阿斯托罗艾尔
    public static PersonAPI createYiFu() {
        PersonAPI person = Global.getFactory().createPerson();
            person.setName(new FullName(getString("utils", "QYData_4"), getString("utils", "QYData_5"), FullName.Gender.FEMALE));
        person.setFaction("TDB");
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_YiFu"));
        person.setPersonality(Personalities.STEADY);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId("TDB_YiFu");
        person.setId("TDB_YiFu");

        person.getStats().setSkipRefresh(true);

        person.getStats().setLevel(12);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
        person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
        person.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
        person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);

        person.getStats().setSkipRefresh(false);

        Global.getSector().getImportantPeople().addPerson(person);


        return person;
    }

    //群友：星空流尘
    public static PersonAPI createXingKong() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName(getString("utils", "QYData_6"), getString("utils", "QYData_7"), FullName.Gender.FEMALE));
        person.setFaction("TDB");
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_02"));
        person.setPersonality(Personalities.STEADY);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId("TDB_XingKong");
        person.setId("TDB_XingKong");

        person.getStats().setSkipRefresh(true);

        person.getStats().setLevel(12);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
        person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
        person.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
        person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);

        person.getStats().setSkipRefresh(false);

        Global.getSector().getImportantPeople().addPerson(person);


        return person;
    }
}