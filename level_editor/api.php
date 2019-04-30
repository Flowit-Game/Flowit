<?php
header('Content-Type: text/html; charset=UTF-8');
session_start();

include("view/boardDrawer.php");
include("logic/utils.php");
include("logic/handleUserInput.php");

if(@$_GET["action"] == "play") {
    drawBoard($_SESSION["play_data"], "play", 0, 0, false, true);
} else {
    drawBoard($_SESSION["level_data"], "edit", $_GET["c"], $_GET["r"], true);
}
