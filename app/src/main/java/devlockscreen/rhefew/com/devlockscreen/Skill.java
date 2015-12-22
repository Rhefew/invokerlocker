package devlockscreen.rhefew.com.devlockscreen;

/**
 * Created by rhefew on 22/12/15.
 */
public class Skill {

    private int quas;
    private int wex;
    private int exort;
    private Name name;

    private int skillPoints;

    public Skill createRandomSkill(){

        Name name = Name.getRandom();
        this.setName(name);

        switch (name){
            case Alacrity: {
                setQuas(0);
                setWex(2);
                setExort(1);

                break;
            }
            case Blast: {
                setQuas(1);
                setWex(1);
                setExort(1);
                break;
            }
            case Coldsnap: {
                setQuas(3);
                setWex(0);
                setExort(0);
                break;
            }
            case Emp: {
                setQuas(0);
                setWex(3);
                setExort(0);
                break;
            }
            case Meteor: {
                setQuas(0);
                setWex(1);
                setExort(2);
                break;
            }
            case Spirit: {
                setQuas(1);
                setWex(0);
                setExort(2);
                break;
            }
            case Sunstrike: {
                setQuas(0);
                setWex(0);
                setExort(3);
                break;
            }
            case Tornado: {
                setQuas(1);
                setWex(2);
                setExort(0);
                break;
            }
            case Walk: {
                setQuas(2);
                setWex(1);
                setExort(0);
                break;
            }
            case Wall: {
                setQuas(2);
                setWex(0);
                setExort(1);
                break;
            }
        }

        return this;
    }

    public int getQuas() {
        return quas;
    }

    public void setQuas(int quas) {
        this.quas = quas;
        setSkillPoints(getSkillPoints()+1);
    }

    public int getWex() {
        return wex;
    }

    public void setWex(int wex) {
        this.wex = wex;
        setSkillPoints(getSkillPoints()+1);
    }

    public int getExort() {
        return exort;
    }

    public void setExort(int exort) {
        this.exort = exort;
        setSkillPoints(getSkillPoints()+1);
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    enum Name{
        Alacrity,
        Blast,
        Coldsnap,
        Emp,
        Meteor,
        Spirit,
        Sunstrike,
        Tornado,
        Walk,
        Wall;

        public static Name getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }


    public boolean compareWith(Skill skill){
        boolean equals = false;
        if (this.getQuas() == skill.getQuas()
                && this.getWex() == skill.getWex()
                && this.getExort() == skill.getExort()) {
            equals = true;
        }

        return equals;
    }


}
