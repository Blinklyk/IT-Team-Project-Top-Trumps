package structures.utils;

import structures.basic.BetterUnit;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.HashSet;
import java.util.Set;

public class ComodoChargerUnit extends Unit {

    private int defaultAttack = 1;
    private int defaultHealth = 3;
    private int curAttack;
    private int curHealth;

    public ComodoChargerUnit() {
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
