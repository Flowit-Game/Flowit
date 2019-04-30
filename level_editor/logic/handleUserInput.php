<?php

if (@$_GET["action"] == "restart") {
    resetLevel();
} else if (@$_GET["action"] == "source") {
    include("logic/send.php");
} else if (begins_with(@$_GET["action"], "save_")) {
    include("logic/handleEditUpdates.php");
} else if (@$_GET["action"] == "play" && @$_GET["play"] == "restart") {
    $_SESSION["play_data"] = @$_SESSION["level_data"];
} else if (@$_GET["action"] == "play") {
    include("logic/handlePlayUpdates.php");
} else if (!isset($_SESSION["level_data"]) && isset($_GET["cols"]) && isset($_GET["rows"])) {
    include("logic/handleInitLevel.php");
}
