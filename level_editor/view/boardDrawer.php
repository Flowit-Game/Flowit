<?php

function drawBoard($LEVEL_DATA, $action, $highlight_col, $highlight_row, $highlight) {

    echo "<table id=\"board\" class=\"mx-auto\">";
    for ($i = 0; $i < $_SESSION["rows"]; $i++) {
        echo "<tr>";
        for ($y = 0; $y < $_SESSION["cols"]; $y++) {
            echo "<td>";
            if ($LEVEL_DATA[$i][$y]["type"] == TYPE_EMPTY || $LEVEL_DATA[$i][$y]["type"] == TYPE_DISABLED) {
                echo "<a class=\"field\" href=\"./?action=$action&r=$i&c=$y\">";
            } else {
                echo "<a class=\"field fieldFull\" href=\"./?action=$action&r=$i&c=$y\">";
            }

            if ($LEVEL_DATA[$i][$y]["type"] == TYPE_EMPTY) {
                echo "<img src=\"./drawable/level_box_dest_color_" . COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" />";
                if ($LEVEL_DATA[$i][$y]["color"] != -1) {
                    echo "<img src=\"./drawable/level_box_stone_" . COLOR_MAPPING[$LEVEL_DATA[$i][$y]["color"]] . ".png\" />";
                }
            } else if ($LEVEL_DATA[$i][$y]["type"] == TYPE_FLOW) {
                echo "<img src=\"./drawable/level_box_color_" . COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" />";
                echo "<img src=\"./drawable/level_box_type_flow.png\" />";
            } else if ($LEVEL_DATA[$i][$y]["type"] == TYPE_BOMB) {
                echo "<img src=\"./drawable/level_box_color_" . COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" />";
                echo "<img src=\"./drawable/level_box_type_bomb.png\" />";
            } else if ($LEVEL_DATA[$i][$y]["type"] == TYPE_LIMIT) {
                echo "<img src=\"./drawable/level_box_color_" . COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" />";
                if (isset($LEVEL_DATA[$i][$y]["action"])) {
                    echo "<img src=\"./drawable/level_box_type_limit_".$LEVEL_DATA[$i][$y]["action"].".png\" />";
                }
            } else if ($LEVEL_DATA[$i][$y]["type"] == TYPE_DISABLED) {
                echo "<img src=\"./drawable/level_nothing.png\" />";
            }

            if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight) {
                echo "<img src=\"./drawable/highlight.png\" />";
            }

            echo "</a>";
            echo "</td>";
        }
        echo "</tr>";
    }
    echo "</table>";
}
