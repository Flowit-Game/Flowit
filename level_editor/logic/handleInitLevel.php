<?php

$_SESSION["rows"] = $_GET["rows"];
$_SESSION["cols"] = $_GET["cols"];

$_SESSION["level_data"] = array();
for ($i = 0; $i < $_SESSION["rows"]; $i++) {
    $_SESSION["level_data"][$i] = array();
    for ($y = 0; $y < $_SESSION["cols"]; $y++) {
        $_SESSION["level_data"][$i][$y] = array("type" => TYPE_EMPTY, "color" => COLOR_EMPTY, "dest_color" => COLOR_RED, "action" => "up");
    }
}