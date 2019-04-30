<?php
function setFieldColor($x, $y, $color) {
    if($x >= 0 && $x < $_SESSION["rows"]
          &&  $y >= 0 && $y < $_SESSION["cols"]
            && $_SESSION["play_data"][$x][$y]["type"] != TYPE_DISABLED) {
        $_SESSION["play_data"][$x][$y]["color"] = $color;
    }
}
function setFieldType($x, $y, $type) {
    if($x >= 0 && $x < $_SESSION["rows"]
          &&  $y >= 0 && $y < $_SESSION["cols"]
            && $_SESSION["play_data"][$x][$y]["type"] != TYPE_DISABLED) {
        $_SESSION["play_data"][$x][$y]["type"] = $type;
    }
}
function fill($x, $y, $dx, $dy, $from, $to) {
    global $wasFilled;
    while(true) {
        $x += $dx;
        $y += $dy;
        if($x >= 0 && $x < $_SESSION["rows"]
            &&  $y >= 0 && $y < $_SESSION["cols"]
            && $_SESSION["play_data"][$x][$y]["type"] == TYPE_EMPTY
            && $_SESSION["play_data"][$x][$y]["color"] == $from) {
            
            $wasFilled = true;
            $_SESSION["play_data"][$x][$y]["color"] = $to;
        } else {
            return;
        }
    }
}
function twofill($x, $y, $dx, $dy, $color) {
    global $wasFilled;
    fill($x, $y, $dx, $dy, COLOR_EMPTY, $color);
    if (!$wasFilled) {
        fill($x, $y, $dx, $dy, $color, COLOR_EMPTY);
    }
}
function recFill($x, $y, $from, $to, $isFirst = false) {
    global $wasFilled;
    if($x >= 0 && $x < $_SESSION["rows"]
        &&  $y >= 0 && $y < $_SESSION["cols"]
        && (($_SESSION["play_data"][$x][$y]["type"] == TYPE_EMPTY
                && $_SESSION["play_data"][$x][$y]["color"] == $from) || $isFirst)) {
        
        $wasFilled = !$isFirst;
        $_SESSION["play_data"][$x][$y]["color"] = $to;
        
        recFill($x-1, $y, $from, $to);
        recFill($x+1, $y, $from, $to);
        recFill($x, $y-1, $from, $to);
        recFill($x, $y+1, $from, $to);
    }
}
function isSolved() {
    for ($r=0; $r < sizeof($_SESSION["play_data"]); $r++) {
        for ($c=0; $c < sizeof($_SESSION["play_data"][0]); $c++) {
            if ($_SESSION["play_data"][$r][$c]["type"] == TYPE_EMPTY
                && ($_SESSION["play_data"][$r][$c]["color"]
                    != $_SESSION["level_data"][$r][$c]["dest_color"])) {
                return false;
            }
        }
    }
    return true;
}

if(isset($_GET["c"])) {
    $c = $_GET["c"];
    $r = $_GET["r"];
    if($_SESSION["play_data"][$r][$c]["type"] == TYPE_BOMB) {
        $color = $_SESSION["play_data"][$r][$c]["dest_color"];
        setFieldColor($r-1, $c-1, $color);setFieldType($r-1, $c-1, TYPE_EMPTY);
        setFieldColor($r-1, $c, $color);setFieldType($r-1, $c, TYPE_EMPTY);
        setFieldColor($r-1, $c+1, $color);setFieldType($r-1, $c+1, TYPE_EMPTY);
        setFieldColor($r, $c-1, $color);setFieldType($r, $c-1, TYPE_EMPTY);
        setFieldColor($r, $c, $color);setFieldType($r, $c, TYPE_EMPTY);
        setFieldColor($r, $c+1, $color);setFieldType($r, $c+1, TYPE_EMPTY);
        setFieldColor($r+1, $c-1, $color);setFieldType($r+1, $c-1, TYPE_EMPTY);
        setFieldColor($r+1, $c, $color);setFieldType($r+1, $c, TYPE_EMPTY);
        setFieldColor($r+1, $c+1, $color);setFieldType($r+1, $c+1, TYPE_EMPTY);
    } else if($_SESSION["play_data"][$r][$c]["type"] == TYPE_FLOW) {
        $wasFilled = false;
        $color = $_SESSION["play_data"][$r][$c]["dest_color"];
        recFill($r, $c, COLOR_EMPTY, $color, true);
        if (!$wasFilled) {
            recFill($r, $c, $color, COLOR_EMPTY, true);
        }
    } else if($_SESSION["play_data"][$r][$c]["type"] == TYPE_LIMIT) {
        $wasFilled = false;
        
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

    $solved = isSolved();
    if ($solved) {
        $_SESSION["solved"] = true;
    }
}
