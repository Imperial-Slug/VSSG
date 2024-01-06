package com.game.vssg;

import com.badlogic.gdx.utils.ObjectSet;

public class Wave {

   private int waveNumber;
   private int numberOfCorvettes;
   private int numberOfFighters;
  private ObjectSet<CpuShip> enemies;


    Wave( ObjectSet<CpuShip> enemies, int waveNumber, int numberOfFighters, int numberOfCorvettes){

        this.waveNumber = waveNumber;
        this.enemies = enemies;
        this.numberOfCorvettes = 0;
        this.numberOfFighters = 0;

    }

    public void setNumberOfFighters(int numberOfFighters) {
        this.numberOfFighters = numberOfFighters;
    }

    public ObjectSet<CpuShip> getEnemies() {
        return enemies;
    }

    public void setEnemies(ObjectSet<CpuShip> enemies) {
        this.enemies = enemies;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public void setNumberOfCorvettes(int numberOfCorvettes) {
        this.numberOfCorvettes = numberOfCorvettes;
    }

    public void setNumberOfFighters(int numberOfFighters, int waveNumber) {
        this.numberOfFighters = numberOfFighters;
        this.waveNumber = waveNumber;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getNumberOfCorvettes() {
        return numberOfCorvettes;
    }

    public int getNumberOfFighters() {
        return numberOfFighters;
    }
}
