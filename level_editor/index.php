<?php
header('Content-Type: text/html; charset=UTF-8');
session_start();
include("boardDrawer.php");

function begins_with($haystack, $needle) {
    return strpos($haystack, $needle) === 0;
}
function contains($haystack, $needle) {
    return strpos($haystack, $needle) !== false;
}

define("ROTATING", true);
define("BOMBS", true);

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
} else if (@$_GET["action"] == "play" && @$_GET["play"] == "restart") {
    $_SESSION["play_data"] = $_SESSION["level_data"];
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
        <script src="jquery-3.1.1.min.js"></script>
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
                background: #aaa;
            }
            a.active {
                background: #aaa;
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
            function createCookie(name,value,days) {
                if (days) {
                    var date = new Date();
                    date.setTime(date.getTime()+(days*24*60*60*1000));
                    var expires = "; expires="+date.toGMTString();
                }
                else {var expires = ""; }
                document.cookie = name+"="+value+expires+"; path=/";
            }

            function readCookie(name) {
                var nameEQ = name + "=";
                var ca = document.cookie.split(';');
                for(var i=0;i < ca.length;i++) {
                    var c = ca[i];
                    while (c.charAt(0)==' ') c = c.substring(1,c.length);
                    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
                }
                return null;
            }
            
            function getQueryVariable(variable) {
                var query = window.location.search.substring(1);
                var vars = query.split("&");
                for (var i=0;i<vars.length;i++) {
                  var pair = vars[i].split("=");
                  if (pair[0] == variable) {
                    return pair[1];
                  }
                }
              }

            function clickRandom(cssClass) {
                var links = $("a."+cssClass);
                if(links.length == 0) {
                    autostart();
                    return;
                }
                var randomNumber = Math.floor(Math.random()*links.length)  
                links.get(randomNumber).click();
            }
            function startRandom() {
                createCookie("randClicks", 60, 1);
                clickRandom();
            }
            function autostart() {
                if(readCookie("randClicks") > 0) {
                    createCookie("randClicks", readCookie("randClicks") - 1, 1);
                    var click = "field";
                    if(getQueryVariable("action") == "edit" ||
                            getQueryVariable("action") == "save_type" ||
                            getQueryVariable("action") == "save_color" ||
                            getQueryVariable("action") == "save_preset") {
                        if(readCookie("randClicks")%3==0) {
                            click = "field";
                        } else if(readCookie("randClicks")%3==1) {
                            click = "action";
                        } else if(readCookie("randClicks")%3==2) {
                            click = "action0";
                        }
                    } else {
                        click = "fieldFull";
                    }
                    setTimeout("clickRandom('"+ click +"')", 100);
                }
            }
            $(document).ready(function() {
                autostart();
            });
        </script>
    </head>
    <body>
        <h1>FlowIt! Level editor</h1>
        <div>
            <a href="./?action=restart" class="button">Neustart</a> &nbsp;
            &nbsp; <a href="./?action=edit&r=0&c=0" class="button <?php echo @$_GET["action"] == "edit"?"active":""; ?>">Bearbeitungsmodus</a> &nbsp;
            &nbsp; <a href="./?action=play&play=restart"  class="button <?php echo @$_GET["action"] == "play"?"active":""; ?>">Level spielen</a> &nbsp;
            &nbsp; <a href="javascript:startRandom();"  class="button">Random!</a> &nbsp;
            &nbsp; <a href="./?action=source"  class="button <?php echo @$_GET["action"] == "source"?"active":""; ?>">Quellcode anzeigen</a>
        </div><br />
        <?php
        
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
        } else if(@$_GET["action"] == "play") {
            
            function setFieldColor($x, $y, $color) {
                global $TYPE_DISABLED;
                if($x >= 0 && $x < $_SESSION["rows"]
                      &&  $y >= 0 && $y < $_SESSION["cols"]
                        && $_SESSION["play_data"][$x][$y]["type"] != $TYPE_DISABLED) {
                    $_SESSION["play_data"][$x][$y]["color"] = $color;
                }
            }
            function setFieldType($x, $y, $type) {
                global $TYPE_DISABLED;
                if($x >= 0 && $x < $_SESSION["rows"]
                      &&  $y >= 0 && $y < $_SESSION["cols"]
                        && $_SESSION["play_data"][$x][$y]["type"] != $TYPE_DISABLED) {
                    $_SESSION["play_data"][$x][$y]["type"] = $type;
                }
            }
            
            if(isset($_GET["c"])) {
                $c = $_GET["c"];
                $r = $_GET["r"];
                if($_SESSION["play_data"][$r][$c]["type"] == $TYPE_BOMB) {
                    $color = $_SESSION["play_data"][$r][$c]["dest_color"];
                    setFieldColor($r-1, $c-1, $color);setFieldType($r-1, $c-1, $TYPE_EMPTY);
                    setFieldColor($r-1, $c, $color);setFieldType($r-1, $c, $TYPE_EMPTY);
                    setFieldColor($r-1, $c+1, $color);setFieldType($r-1, $c+1, $TYPE_EMPTY);
                    setFieldColor($r, $c-1, $color);setFieldType($r, $c-1, $TYPE_EMPTY);
                    setFieldColor($r, $c, $color);setFieldType($r, $c, $TYPE_EMPTY);
                    setFieldColor($r, $c+1, $color);setFieldType($r, $c+1, $TYPE_EMPTY);
                    setFieldColor($r+1, $c-1, $color);setFieldType($r+1, $c-1, $TYPE_EMPTY);
                    setFieldColor($r+1, $c, $color);setFieldType($r+1, $c, $TYPE_EMPTY);
                    setFieldColor($r+1, $c+1, $color);setFieldType($r+1, $c+1, $TYPE_EMPTY);
                } else if($_SESSION["play_data"][$r][$c]["type"] == $TYPE_FLOW) {
                    $wasFilled = false;
                    function recFill($x, $y, $from, $to, $isFirst = false) {
                        global $wasFilled, $TYPE_EMPTY;
                        if($x >= 0 && $x < $_SESSION["rows"]
                            &&  $y >= 0 && $y < $_SESSION["cols"]
                            && ($_SESSION["play_data"][$x][$y]["type"] == $TYPE_EMPTY || $isFirst)
                            && $_SESSION["play_data"][$x][$y]["color"] == $from) {
                            
                            $wasFilled = !$isFirst;
                            $_SESSION["play_data"][$x][$y]["color"] = $to;
                            
                            recFill($x-1, $y, $from, $to);
                            recFill($x+1, $y, $from, $to);
                            recFill($x, $y-1, $from, $to);
                            recFill($x, $y+1, $from, $to);
                        }
                    }
                    $color = $_SESSION["play_data"][$r][$c]["dest_color"];
                    recFill($r, $c, $COLOR_EMPTY, $color, true);
                    if (!$wasFilled) {
                        recFill($r, $c, $color, $COLOR_EMPTY, true);
                    }
                } else if($_SESSION["play_data"][$r][$c]["type"] == $TYPE_LIMIT) {
                    $wasFilled = false;
                    
                    function fill($x, $y, $dx, $dy, $from, $to) {
                        global $wasFilled, $TYPE_EMPTY;
                        while(true) {
                            $x += $dx;
                            $y += $dy;
                            if($x >= 0 && $x < $_SESSION["rows"]
                                &&  $y >= 0 && $y < $_SESSION["cols"]
                                && $_SESSION["play_data"][$x][$y]["type"] == $TYPE_EMPTY
                                && $_SESSION["play_data"][$x][$y]["color"] == $from) {
                                
                                $wasFilled = true;
                                $_SESSION["play_data"][$x][$y]["color"] = $to;
                            } else {
                                return;
                            }
                        }
                    }
                    function twofill($x, $y, $dx, $dy, $color) {
                        global $COLOR_EMPTY, $wasFilled;
                        fill($x, $y, $dx, $dy, $COLOR_EMPTY, $color);
                        if (!$wasFilled) {
                            fill($x, $y, $dx, $dy, $color, $COLOR_EMPTY);
                        }
                    }
                    
                    $col = $_SESSION["play_data"][$r][$c];
                    $color = $_SESSION["play_data"][$r][$c]["dest_color"];
                    if ($col["action"] == "left") {
                        twofill($r, $c, 0, -1, $color);
                    } else if ($col["action"] == "right") {
                        twofill($r, $c, 0, +1, $color);
                    } else if ($col["action"] == "up") {
                        twofill($r, $c, -1, 0, $color);
                    } else if ($col["action"] == "down") {
                        twofill($r, $c, +1, 0, $color);
                    } else if ($col["action"] == "rotate_left") {
                        twofill($r, $c, 0, -1, $color);
                        $_SESSION["play_data"][$r][$c]["action"] = "rotate_up";
                    } else if ($col["action"] == "rotate_right") {
                        twofill($r, $c, 0, +1, $color);
                        $_SESSION["play_data"][$r][$c]["action"] = "rotate_down";
                    } else if ($col["action"] == "rotate_up") {
                        twofill($r, $c, -1, 0, $color);
                        $_SESSION["play_data"][$r][$c]["action"] = "rotate_right";
                    } else if ($col["action"] == "rotate_down") {
                        twofill($r, $c, +1, 0, $color);
                        $_SESSION["play_data"][$r][$c]["action"] = "rotate_left";
                    }
                }
            }
            
            drawBoard($_SESSION["play_data"], "play", 0, 0, false, "float:none;");
        } else {
            drawBoard($_SESSION["level_data"], "edit", $_GET["c"], $_GET["r"], @$_GET["action"] == "edit");

            if (@$_GET["action"] == "edit") {
                echo "<div id=\"editor\"><b>Bearbeiten:</b><br /><br /><table>";
                echo "<tr>";
                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a class=\"action0\" href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=0\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
                echo "</a>";
                echo "</td>";

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a class=\"action0\" href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=1\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_flow.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                echo "</a>";
                echo "</td>";
                
                if(BOMBS) {
                    echo "<td width=\"$size\" height=\"$size\">";
                    echo "<a class=\"action0\" href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=4\" style=\"height:$sizepx;\">";
                    echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
                    echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                    echo "<img src=\"./drawable/level_box_type_bomb.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                    echo "</a>";
                    echo "</td>";
                }

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a class=\"action0\" href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=2\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_color_black.png\" style=\"width:$sizepx;position:relative;top:-$sizepx;\" />";
                echo "<img src=\"./drawable/level_box_type_limit_up.png\" style=\"width:$sizepx;position:relative;top:-".(2*$size)."px;\" />";
                echo "</a>";
                echo "</td>";

                echo "<td width=\"$size\" height=\"$size\">";
                echo "<a class=\"action0\" href=\"./?action=save_type&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&type=3\" style=\"height:$sizepx;\">";
                echo "<img src=\"./drawable/level_nothing.png\" style=\"width:$sizepx;\" />";
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
                            echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                        echo "<a class=\"action\" href=\"./?action=save_preset&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                        echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                            echo "<a class=\"action\" href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                            echo "<a class=\"action\" href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                            echo "<a class=\"action\" href=\"./?action=save_color&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "&color=$a\" style=\"height:$sizepx;\">";
                            echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                        echo "<a class=\"action\" href=\"./?action=save_$d&c=" . $_GET["c"] . "&r=" . $_GET["r"] . "\" style=\"height:$sizepx;\">";
                        echo "<img src=\"./drawable/level_box_hole.png\" style=\"width:$sizepx;\" />";
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
                    if(ROTATING) {
                        direction("rotate_up");
                        direction("rotate_down");
                        direction("rotate_left");
                        direction("rotate_right");
                    }

                    echo "</tr>";
                    echo "</table>";
                }
                echo "</div>";
            }
        }
        ?>

    </body>
</html>


