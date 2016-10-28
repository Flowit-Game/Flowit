<?php
header('Content-Type: text/html; charset=UTF-8');
session_start();

function begins_with($haystack, $needle) {
    return strpos($haystack, $needle) === 0;
}
function contains($haystack, $needle) {
    return strpos($haystack, $needle) !== false;
}

$size = 70;
$sizepx = $size . "px";

$TYPE_EMPTY = 0;
$TYPE_FLOW = 1;
$TYPE_LIMIT = 2;
$TYPE_DISABLED = 3;
$TYPE_BOMB = 4;

$COLOR_EMPTY = -1;
$COLOR_RED = 1;
$COLOR_GREEN = 2;
$COLOR_BLUE = 3;
$COLOR_YELLOW = 4;
$COLOR_BLACK = 5;

$COLOR_MAPPING = array(
    $COLOR_EMPTY => "",
    $COLOR_RED => "red",
    $COLOR_GREEN => "green",
    $COLOR_BLUE => "blue",
    $COLOR_YELLOW => "yellow",
    $COLOR_BLACK => "black",
);
$COLOR_MAPPING2 = array(
    $COLOR_EMPTY => "X",
    $COLOR_RED => "r",
    $COLOR_GREEN => "g",
    $COLOR_BLUE => "b",
    $COLOR_YELLOW => "o",
    $COLOR_BLACK => "d",
);

if (@$_GET["action"] == "restart") {
    unset($_SESSION["level_data"]);
} else if (@$_GET["action"] == "save_color") {
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"]))
        $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"] = $_GET["color"];
    $_GET["action"] = "edit";
} else if (@$_GET["action"] == "save_preset") {
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["color"]))
        $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["color"] = $_GET["color"];
    $_GET["action"] = "edit";
} else if (@$_GET["action"] == "save_type") {
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"]))
        $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] = $_GET["type"];
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"]))
        if($_GET["type"] == 0)
            $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"] = $COLOR_RED;
        else
            $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"] = $COLOR_BLACK;
    $_GET["action"] = "edit";
} else if (begins_with(@$_GET["action"], "save_")) {
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["action"]))
        $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["action"] = str_replace("save_", "", $_GET["action"]);
    $_GET["action"] = "edit";
}


if (!isset($_SESSION["level_data"]) && isset($_GET["cols"]) && isset($_GET["rows"])) {
    $_SESSION["rows"] = $_GET["rows"];
    $_SESSION["cols"] = $_GET["cols"];

    $_SESSION["level_data"] = array();
    for ($i = 0; $i < $_SESSION["rows"]; $i++) {
        $_SESSION["level_data"][$i] = array();
        for ($y = 0; $y < $_SESSION["cols"]; $y++) {
            $_SESSION["level_data"][$i][$y] = array("type" => $TYPE_EMPTY, "color" => $COLOR_EMPTY, "dest_color" => $COLOR_RED, "action" => "up");
        }
    }
}
?>
<!DOCTYPE html>
<html>
    <head>
        <title>FlowIt-LevelEditor</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <style>
            *{
                margin:0px;
                padding:0px;
                border: none;
                text-align:center;
                font-family: Arial;
                font-size: 16px;
            }
            img{
                background:transparent;
            }
            table{
                border-spacing: 0px;
                line-height: 0px;
            }
            body{
                padding: 20px;
                overflow-y:scroll;
                width: 80%;
                margin: auto;
                background: #ddd;
            }
            body > *{
                margin:auto;
            }
            h1 {
                margin-bottom: 20px;
                font-size: 30px;
                display: block;
            }
            a{
                display:block;
                text-align:center;
                text-decoration: none;
                color: #22d;
            }
            textarea {
                width:300px;
                font-family:monospace;
                height:500px;
                font-size:16px;
                padding:10px;
                text-align:left;
                background: #eee;
                border: 1px solid #bbb;
            }
            a.button {
                background: #ccc;
                padding: 8px;
                display: inline-block;
            }
            a.button:hover {
                background: #bbb;
            }
            #board {
                width: 55%;
            }
            #editor {
                width: 40%;
                min-width: 450px;
                background: #eee;
                border: 1px solid #bbb;
                padding: 20px;
            }
            #editor, #board {
                display: inline-block;
                float: left;
                margin-top: 20px;
            }
            #editor>*, #board>* {
                margin: auto;
            }
        </style>
        <script>
            function findGetParameter(parameterName) {
                var result = null,
                    tmp = [];
                var items = location.search.substr(1).split("&");
                for (var index = 0; index < items.length; index++) {
                    tmp = items[index].split("=");
                    if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
                }
                return result;
            }
            function param(paramName, offset) {
                return Number(findGetParameter(paramName)) + offset;
            }
            
            document.onkeydown = checkKey;
            function checkKey(e) {
                e = e || window.event;
                if (e.keyCode == '38') {
                    // up arrow
                    window.location.href="./?action=edit&c="+param("c",  0)+"&r="+param("r", -1);
                    e.preventDefault();
                } else if (e.keyCode == '40') {
                    // down arrow
                    window.location.href="./?action=edit&c="+param("c",  0)+"&r="+param("r", +1);
                    e.preventDefault();
                } else if (e.keyCode == '37') {
                   // left arrow
                    window.location.href="./?action=edit&c="+param("c", -1)+"&r="+param("r",  0);
                    e.preventDefault();
                } else if (e.keyCode == '39') {
                   // right arrow
                    window.location.href="./?action=edit&c="+param("c", +1)+"&r="+param("r",  0);
                    e.preventDefault();
                }

            }
        </script>
    </head>
    <body>
        <h1>FlowIt! Level editor</h1>
        <div>
            <a href="./?action=restart" class="button">Neustart</a> &nbsp;
            &nbsp; <a href="./?action=source"  class="button">Quellcode anzeigen</a> &nbsp;
            &nbsp; <a href="./?action=edit&r=0&c=0" class="button">Bearbeitungsmodus</a>
        </div><br />
        <?php

        function b($b) {
            if ($b)
                return "true";
            else
                return "false";
        }
        
        
        if (!isset($_SESSION["level_data"])) {
            echo "<br /><br /><a href=\"?cols=5&rows=6&r=0&c=0&action=edit\" class=\"button\">5*6 Feld erstellen</a><br /><br />";
            echo "<a href=\"?cols=6&rows=8&r=0&c=0&action=edit\" class=\"button\">6*8 Feld erstellen</a>";
        } else if (@$_GET["action"] == "source") {
            echo "<br /><br /><textarea spellcheck=\"false\">";

            echo "    <level number=\"\"\n        color=\"";
            $isFirst = true;
            foreach ($_SESSION["level_data"] as $row) {
                if($isFirst) {
                    $isFirst = false;
                } else {
                    echo "\n";
                    echo "               ";
                }
                foreach ($row as $col) {
                    if ($col["type"] == 3) {
                        echo "0";
                    } else {
                        echo $COLOR_MAPPING2[$col["dest_color"]];
                    }
                }
            }

            echo "\"\n        modifier=\"";

            foreach ($_SESSION["level_data"] as $row) {
                echo "\n               ";
                foreach ($row as $col) {
                    if ($col["type"] == $TYPE_LIMIT) {
                        if ($col["action"] == "left") {
                            echo "L";
                        } else if ($col["action"] == "right") {
                            echo "R";
                        } else if ($col["action"] == "up") {
                            echo "U";
                        } else if ($col["action"] == "down") {
                            echo "D";
                        } else if ($col["action"] == "rotate_left") {
                            echo "a";
                        } else if ($col["action"] == "rotate_right") {
                            echo "x";
                        } else if ($col["action"] == "rotate_up") {
                            echo "w";
                        } else if ($col["action"] == "rotate_down") {
                            echo "s";
                        }
                    } else if ($col["type"] == $TYPE_FLOW) {
                        echo "F";
                    } else if ($col["type"] == $TYPE_BOMB) {
                        echo "B";
                    } else if ($col["type"] == $TYPE_DISABLED) {
                        echo "X";
                    } else if ($col["color"] != $COLOR_EMPTY) {
                        echo $COLOR_MAPPING2[$col["color"]];
                    } else {
                        echo "0";
                    }
                }
            }
            echo "\" />";

            echo "</textarea>";
        } else {
            echo "<div id=\"board\"><table>";
            for ($i = 0; $i < $_SESSION["rows"]; $i++) {
                echo "<tr>";
                for ($y = 0; $y < $_SESSION["cols"]; $y++) {
                    echo "<td width=\"$size\" height=\"$size\">";
                    echo "<a href=\"./?action=edit&r=$i&c=$y\" style=\"height:$sizepx;\">";
                    echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";

                    if ($_SESSION["level_data"][$i][$y]["type"] == $TYPE_EMPTY) {
                        echo "<img src=\"./drawable/level_box_dest_color_" . $COLOR_MAPPING[$_SESSION["level_data"][$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";

                        if ($_SESSION["level_data"][$i][$y]["color"] != -1) {
                            echo "<img src=\"./drawable/level_box_stone_" . $COLOR_MAPPING[$_SESSION["level_data"][$i][$y]["color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                            if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                                echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
                        }
                        else if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                            echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                    }
                    else if ($_SESSION["level_data"][$i][$y]["type"] == $TYPE_FLOW) {
                        echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$_SESSION["level_data"][$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                        echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";

                        if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                            echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
                    }
                    else if ($_SESSION["level_data"][$i][$y]["type"] == $TYPE_BOMB) {
                        echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$_SESSION["level_data"][$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                        echo "<img src=\"./drawable/level_box_type_bomb.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";

                        if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                            echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(3*$size)."px;\" />";
                    }
                    else if ($_SESSION["level_data"][$i][$y]["type"] == $TYPE_LIMIT) {
                        echo "<img src=\"./drawable/level_box_color_" . $COLOR_MAPPING[$_SESSION["level_data"][$i][$y]["dest_color"]] . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                        $delta = -2*$size;

                        if(isset($_SESSION["level_data"][$i][$y]["action"])) {
                            echo "<img src=\"./drawable/level_box_type_limit_".$_SESSION["level_data"][$i][$y]["action"].".png\" style=\"width:$sizepx;position:relative;top:$delta" . "px;\" />";
                            $delta -= $size;
                        }

                        if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                            echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:$delta" . "px;\" />";

                        //echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                    }
                    if ($_SESSION["level_data"][$i][$y]["type"] == $TYPE_DISABLED) {
                        echo "<img src=\"./drawable/level_nothing.jpg\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";

                        
                        if ($i == @(Integer) $_GET["r"] && $y == @(Integer) $_GET["c"] && @$_GET["action"] == "edit")
                            echo "<img src=\"./highlight.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                    }
                    echo "</a>";
                    echo "</td>";
                    //$_SESSION["level_data"][$i][$y] = array("type" => $TYPE_EMPTY,"color" => $COLOR_EMPTY, "dest_color" => $COLOR_RED);
                }
                echo "</tr>";
            }
            echo "</table></div>";

            if (@$_GET["action"] == "edit") {
                echo "<div id=\"editor\"><b>Bearbeiten:</b><br /><br /><table>";
                echo "<tr>";
                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=0\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                echo "</a>";
                echo "</td>";

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=1\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                echo "</a>";
                echo "</td>";
                
                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=4\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_bomb.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                echo "</a>";
                echo "</td>";

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=2\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_limit_up.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                echo "</a>";
                echo "</td>";

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=3\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_nothing.jpg\" style=\"width:$sizepx;\" />";
                echo "</a>";
                echo "</td>";

                echo "</tr>";
                echo "</table>";

                echo "<br />";

                if (!isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"])) {
                    echo "<br /><br />Out of board.";
                } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == $TYPE_EMPTY) {
                    echo "<table>";
                    echo "<tr>";
                    foreach ($COLOR_MAPPING as $a => $b) {
                        if ($b != "") {
                            echo "<td width=\"$size\" height=\"$size\">";
                            echo "<a href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_dest_color_" . $b . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                            echo "</a>";
                            echo "</td>";
                        }
                    }
                    echo "</tr>";
                    echo "</table>";

                    echo "<br />";

                    echo "<table>";
                    echo "<tr>";
                    foreach ($COLOR_MAPPING as $a => $b) {
                        echo "<td width=\"$size\" height=\"$size\">";
                        echo "<a href=\"./?action=save_preset&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                        echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                        if ($b != "")
                            echo "<img src=\"./drawable/level_box_stone_" . $b . ".png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                        echo "</a>";
                        echo "</td>";
                    }
                    echo "</tr>";
                    echo "</table>";
                }
                else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == $TYPE_FLOW) {
                    echo "<table>";
                    echo "<tr>";
                    foreach ($COLOR_MAPPING as $a => $b) {
                        if ($b != "") {
                            echo "<td width=\"$size\" height=\"$size\">";
                            echo "<a href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_color_$b.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                            echo "</a>";
                            echo "</td>";
                        }
                    }
                    echo "</tr>";
                    echo "</table>";
                }
                else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == $TYPE_BOMB) {
                    echo "<table>";
                    echo "<tr>";
                    foreach ($COLOR_MAPPING as $a => $b) {
                        if ($b != "") {
                            echo "<td width=\"$size\" height=\"$size\">";
                            echo "<a href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_color_$b.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_type_bomb.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                            echo "</a>";
                            echo "</td>";
                        }
                    }
                    echo "</tr>";
                    echo "</table>";
                } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == $TYPE_LIMIT) {
                    echo "<table>";
                    echo "<tr>";
                    foreach ($COLOR_MAPPING as $a => $b) {
                        if ($b != "") {
                            echo "<td width=\"$size\" height=\"$size\">";
                            echo "<a href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_color_$b.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                            echo "<img src=\"./drawable/level_box_type_limit_up.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                            echo "</a>";
                            echo "</td>";
                        }
                    }
                    echo "</tr>";
                    echo "</table>";

                    echo "<br />";

                    echo "<table>";
                    echo "<tr>";
                    
                    function direction($d) {
                        global $size, $sizepx;
                        echo "<td width=\"$size\" height=\"$size\">";
                        echo "<a href=\"./?action=save_$d&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "\" style=\"height:$sizepx;\">";
                        echo "<img src=\"./drawable/level_box_hole.jpg\" style=\"width:$sizepx;\" />";
                        echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                        echo "<img src=\"./drawable/level_box_type_limit_$d.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                        echo "</a>";
                        echo "</td>";
                    }

                    direction("up");
                    direction("down");
                    direction("left");
                    direction("right");
                    echo "</tr><tr>";
                    direction("rotate_up");
                    direction("rotate_down");
                    direction("rotate_left");
                    direction("rotate_right");

                    echo "</tr>";
                    echo "</table>";
                }
                echo "</div>";
            }
        }
        ?>

    </body>
</html>


