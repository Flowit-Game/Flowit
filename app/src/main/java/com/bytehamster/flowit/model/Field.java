package com.bytehamster.flowit.model;

public class Field {
    private Color color;
    private Modifier modifier;
    private boolean isVisited = false;

    public Field(char color, char modifier) {
        switch (color) {
            case 'r':
                this.color = Color.RED;
                break;
            case 'g':
                this.color = Color.GREEN;
                break;
            case 'b':
                this.color = Color.BLUE;
                break;
            case 'o':
                this.color = Color.ORANGE;
                break;
            case 'd':
                this.color = Color.DARK;
                break;
            default:
                this.color = Color.EMPTY;
                break;
        }
        switch (modifier) {
            case 'r':
                this.modifier = Modifier.RED;
                break;
            case 'g':
                this.modifier = Modifier.GREEN;
                break;
            case 'b':
                this.modifier = Modifier.BLUE;
                break;
            case 'o':
                this.modifier = Modifier.ORANGE;
                break;
            case 'd':
                this.modifier = Modifier.DARK;
                break;
            case 'F':
                this.modifier = Modifier.FLOOD;
                break;
            case 'U':
                this.modifier = Modifier.UP;
                break;
            case 'R':
                this.modifier = Modifier.RIGHT;
                break;
            case 'L':
                this.modifier = Modifier.LEFT;
                break;
            case 'D':
                this.modifier = Modifier.DOWN;
                break;
            case 'w':
                this.modifier = Modifier.ROTATE_UP;
                break;
            case 'x':
                this.modifier = Modifier.ROTATE_RIGHT;
                break;
            case 'a':
                this.modifier = Modifier.ROTATE_LEFT;
                break;
            case 's':
                this.modifier = Modifier.ROTATE_DOWN;
                break;
            case 'B':
                this.modifier = Modifier.BOMB;
                break;
            case '0':
                this.modifier = Modifier.EMPTY;
                break;
            default:
                this.modifier = Modifier.TRANSPARENT;
                break;
        }
    }

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}
