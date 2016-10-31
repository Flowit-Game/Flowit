<?php

function drawBoard($LEVEL_DATA, $action, $highlight_col, $highlight_row, $highlight, $style="") {
    global $COLOR_MAPPING, $size, $sizepx,
           $TYPE_EMPTY,$TYPE_FLOW,$TYPE_LIMIT,$TYPE_DISABLED,$TYPE_BOMB;
    
    echo "<div id=\"board\" style=\"$style\"><table>";
    for ($i = 0; $i < $_SESSION["rows"]; $i++) {
        echo "<tr>";
        for ($y = 0; $y < $_SESSION["cols"]; $y++) {
            echo "<td width=\"$size\" height=\"$size\">";
            echo "<a href=\"./?action=$action&r=$i&c=$y\" style=\"height:$sizepx;\">";
            echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";

            if ($LEVEL_DATA[$i][$y]["type"] == $TYPE_EMPTY) {
                echo "<img src=\"./drawable/level_box_dest_color_" . $COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";

                if ($LEVEL_DATA[$i][$y]["color"] != -1) {
                    echo "<img src=\"./drawable/level_box_stone_" . $COLOR_MAPPING[$LEVEL_DATA[$i][$y]["color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                    if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                        echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
                }
                else if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                    echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
            }
            else if ($LEVEL_DATA[$i][$y]["type"] == $TYPE_FLOW) {
                echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";

                if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                    echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
            }
            else if ($LEVEL_DATA[$i][$y]["type"] == $TYPE_BOMB) {
                echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_bomb.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";

                if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                    echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
            }
            else if ($LEVEL_DATA[$i][$y]["type"] == $TYPE_LIMIT) {
                echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$LEVEL_DATA[$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                $delta = -2*$size;

                if(isset($LEVEL_DATA[$i][$y]["action"])) {
                    echo "<img src=\"./drawable/level_box_type_limit_".$LEVEL_DATA[$i][$y]["action"].".png\" style=\"width:$sizepx;position:relative;top:$delta" . "px;\" />";
                    $delta -= $size;
                }

                if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                    echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:$delta" . "px;\" />";

                //echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
            }
            if ($LEVEL_DATA[$i][$y]["type"] == $TYPE_DISABLED) {
                echo "<img src=\"./drawable/level_nothing.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";


                if ($i == @(Integer) $highlight_row && $y == @(Integer) $highlight_col && $highlight)
                    echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
            }
            echo "</a>";
            echo "</td>";
            //$LEVEL_DATA[$i][$y] = array("type" => $TYPE_EMPTY,"color" => $COLOR_EMPTY, "dest_color" => $COLOR_RED);
        }
        echo "</tr>";
    }
    echo "</table></div>";
}
