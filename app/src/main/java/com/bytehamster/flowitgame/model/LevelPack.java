package com.bytehamster.flowitgame.model;

import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;

public class LevelPack {
    private ArrayList<Level> levels = new ArrayList<>();
    public static LevelPack EASY;
    public static LevelPack MEDIUM;
    public static LevelPack HARD;
    public static LevelPack COMMUNITY;
    private int id;

    public static void parsePacks(Context context) {
        EASY = new LevelPack(1, "levelsEasy.xml", context);
        MEDIUM = new LevelPack(2, "levelsMedium.xml", context);
        HARD = new LevelPack(3, "levelsHard.xml", context);
        COMMUNITY = new LevelPack(4, "levelsCommunity.xml", context);
    }

    private LevelPack(int id, String fileName, Context context) {
        this.id = id;
        Document doc;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(context.getAssets().open(fileName + ".compressed"));
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            throw new RuntimeException("Error loading level pack " + fileName, e);
        }

        NodeList levelList = doc.getDocumentElement().getChildNodes();
        int indexInPack = 0;
        for (int i = 0; i < levelList.getLength(); i++) {
            if(levelList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element levelEl = (Element) levelList.item(i);
            int number = Integer.parseInt(levelEl.getAttribute("number"));
            String colors = levelEl.getAttribute("color");
            String modifiers = levelEl.getAttribute("modifier");
            int optimalSteps = 0;
            if (levelEl.hasAttribute("solution")) {
                String solution = levelEl.getAttribute("solution");
                optimalSteps = solution.split(",").length;
            }

            levels.add(new Level(indexInPack, number, this, colors, modifiers, optimalSteps));
            indexInPack++;
        }
    }

    public Level getLevel(int indexInPack) {
        return levels.get(indexInPack);
    }

    public int size() {
        return levels.size();
    }

    public int id() {
        return id;
    }
}
