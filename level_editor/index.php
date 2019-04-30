<?php
header('Content-Type: text/html; charset=UTF-8');
session_start();

include("view/boardDrawer.php");
include("logic/utils.php");
include("logic/handleUserInput.php");

?>
<!DOCTYPE html>
<html>
    <head>
        <title>Flowit Level Editor</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" type="image/png" href="drawable/icon_web.png" sizes="256x256">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <script src="res/jquery-3.1.1.min.js"></script>
        <link rel="stylesheet" href="res/bootstrap-4.3.1.min.css">
        <link rel="stylesheet" type="text/css" href="res/style.css">
        <script src="res/script.js"></script>
    </head>
    <body>
        <div class="navbar navbar-light bg-light">
            <h1 class="navbar-brand">
                <img src="drawable/icon_web.png" width="30" height="30" class="d-inline-block align-top" alt="Flowit!">
                Flowit! Level Editor
            </h1>
            <div class="navbar-expand navbar-left mr-auto mt-2 mt-lg-0">
                <ul class="navbar-nav">
                    <li class="nav-item <?php echo @$_GET["action"] == "edit"?"active":""; ?>">
                        <a class="nav-link" href="./?action=edit&r=-1&c=0">Edit</a>
                    </li>
                    <li class="nav-item <?php echo @$_GET["action"] == "play"?"active":""; ?>">
                        <a class="nav-link" href="./?action=play&play=restart">Play</a>
                    </li>
                </ul>
            </div>
            <div class="navbar-expand navbar-right mt-2 mt-lg-0">
                <a class="btn btn-secondary" onclick="return confirm('Delete the level you designed?')" href="./?action=restart">Delete</a>
                <?php if (@$_SESSION["solved"]) { ?>
                    <a class="btn btn-success" id="saveButton" href="./?action=source">Submit</a>
                <?php } else { ?>
                    <a class="btn btn-secondary" id="saveButton" href="./?action=source">Submit</a>
                <?php } ?>
            </div>
        </div>
        <?php
            if (!isset($_SESSION["level_data"])) {
                include("view/createLevel.php");
            } else if (@$_GET["action"] == "source") { 
                include("view/save.php");
            } else if(@$_GET["action"] == "play") {
                include("view/play.php");
            } else {
                include("view/edit.php");
            }
        ?>
    </body>
</html>
