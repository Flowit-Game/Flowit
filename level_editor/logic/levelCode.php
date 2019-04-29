<?php

function getLevelCode() {
    $COLOR_MAPPING2 = array(
        COLOR_EMPTY => "X",
        COLOR_RED => "r",
        COLOR_GREEN => "g",
        COLOR_BLUE => "b",
        COLOR_YELLOW => "o",
        COLOR_BLACK => "d",
    );

    $code = "    <level number=\"\"\n        color=\"";

    $isFirst = true;
    foreach ($_SESSION["level_data"] as $row) {
        if($isFirst) {
            $isFirst = false;
        } else {
            $code .= "\n";
            $code .= "               ";
        }
        foreach ($row as $col) {
            if ($col["type"] == 3) {
                $code .= "0";
            } else {
                $code .= $COLOR_MAPPING2[$col["dest_color"]];
            }
        }
    }

    $code .= "\"\n        modifier=\"";

    foreach ($_SESSION["level_data"] as $row) {
        $code .= "\n               ";
        foreach ($row as $col) {
            if ($col["type"] == TYPE_LIMIT) {
                if ($col["action"] == "left") {
                    $code .= "L";
                } else if ($col["action"] == "right") {
                    $code .= "R";
                } else if ($col["action"] == "up") {
                    $code .= "U";
                } else if ($col["action"] == "down") {
                    $code .= "D";
                } else if ($col["action"] == "rotate_left") {
                    $code .= "a";
                } else if ($col["action"] == "rotate_right") {
                    $code .= "x";
                } else if ($col["action"] == "rotate_up") {
                    $code .= "w";
                } else if ($col["action"] == "rotate_down") {
                    $code .= "s";
                }
            } else if ($col["type"] == TYPE_FLOW) {
                $code .= "F";
            } else if ($col["type"] == TYPE_BOMB) {
                $code .= "B";
            } else if ($col["type"] == TYPE_DISABLED) {
                $code .= "X";
            } else if ($col["color"] != COLOR_EMPTY) {
                $code .= $COLOR_MAPPING2[$col["color"]];
            } else {
                $code .= "0";
            }
        }
    }
    $code .= "\" />";
    return $code;
}
