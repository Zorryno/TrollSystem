package de.zorryno.trollsystem.trolls;

import org.bukkit.inventory.ItemStack;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.function.Function;

public class TrollHolder {

    private static TrollHolder instance = new TrollHolder();

    private List<Troll> trolls = new ArrayList<>();
    private Random random;

    private TrollHolder() {
        this.random = new Random();
    }

    public void registerTroll(Troll troll) {
        trolls.add(troll);
    }

    public List<Troll> getTrolls() {
        return new ArrayList<>(trolls);
    }

    public Troll getTroll(String name) {
        for (Troll troll : getTrolls()) {
            if (troll.getName().equals(name))
                return troll;
        }
        return null;
    }

    public Troll getTroll(ItemStack item) {
        for (Troll troll : getTrolls()) {
            if (troll.getItem().isSimilar(item))
                return troll;
        }
        return null;
    }

    public List<Troll> getRandomTrolls(int amount) {
        List<Troll> trollList = new ArrayList<>();

        for (int i = amount; i > 0; i--) {
            trollList.add(trolls.get(random.nextInt(trolls.size())));
        }

        return trollList;
    }

    public void unregisterAll() {
        trolls.clear();
    }

    public static TrollHolder getInstance() {
        return instance;
    }
}
