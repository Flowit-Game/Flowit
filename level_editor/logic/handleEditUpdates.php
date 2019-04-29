<?php
$_SESSION["solved"] = false;

if (@$_GET["action"] == "save_color") {
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
            $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"] = COLOR_RED;
        else
            $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["dest_color"] = COLOR_BLACK;
    $_GET["action"] = "edit";
} else if (begins_with(@$_GET["action"], "save_")) {
    if(isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["action"]))
        $_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["action"] = str_replace("save_", "", $_GET["action"]);
    $_GET["action"] = "edit";
}

