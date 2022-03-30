package structures.utils;

import structures.basic.Unit;

public class AIAvatar extends Unit {
    private int defaultAttack = 2;
    private int defaultHealth = 20;
    private int curAttack;
    private int curHealth;

    public AIAvatar() {
        super();
        curAttack = defaultAttack;
        curHealth = defaultHealth;
    }

    public void setCurAttack(int curAttack) {
        this.curAttack = curAttack;
    }

    public void setCurHealth(int curHealth) {
        this.curHealth = curHealth;
    }

    public int getCurAttack() {
        return curAttack;
    }

    public int getCurHealth() {
        return curHealth;
    }
}
