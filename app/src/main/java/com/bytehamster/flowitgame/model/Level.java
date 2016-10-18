package com.bytehamster.flowitgame.model;

import android.content.Context;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Level {
    private final Field[][] map = new Field[5][6];

    public Level(int number, Context context) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(context.getAssets().open("levels_compressed.xml"));
            doc.getDocumentElement().normalize();

            NodeList levelList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < levelList.getLength(); i++) {
                if(levelList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element level = (Element) levelList.item(i);
                if (level.getAttribute("number").equals("" + number)) {
                    String color = level.getAttribute("color");
                    color = color.replaceAll("\\s", "");
                    String modifier = level.getAttribute("modifier");
                    modifier = modifier.replaceAll("\\s", "");

                    for(int col = 0; col < 5; col++) {
                        for(int row = 0; row < 6; row++) {
                            int index = col + row*5;
                            map[col][row] = new Field(color.charAt(index), modifier.charAt(index));
                        }
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        for(int col = 0; col < 5; col++) {
            for(int row = 0; row < 6; row++) {
                map[col][row] = new Field('0', 'X');
            }
        }
    }

    public Field fieldAt(int x, int y) {
        return map[x][y];
    }

    public void unvisitAll() {
        for(int col = 0; col < getWidth(); col++) {
            for(int row = 0; row < getHeight(); row++) {
                map[col][row].setVisited(false);
            }
        }
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }
}
