<?php
include("logic/levelCode.php");

if (@$_POST["save"] == "true" && isset($_SESSION["level_data"])) {
    $success = true;
    $sendResult = "";
    if (@$_POST["name"] == "") {
        $success = false;
        $sendResult .= "<div class=\"alert alert-danger\">Please enter your name.</div>";
    }
    if (@$_POST["checkAgree"] != "on") {
        $success = false;
        $sendResult .= "<div class=\"alert alert-danger\">Please accept the conditions.</div>";
    }

    if ($success) {
        $receiver = "info@bytehamster.com";
        $subject = "Flowit level proposal";
        $header = "From: ByteHamster system <system@bytehamster.com>";
        $text = "Name: " . $_POST["name"] . "\n \n" . getLevelCode();

        $mailSuccess = mail($receiver, $subject, $text, $header);

        if ($mailSuccess) {
            $sendResult .= "<div class=\"alert alert-success\">Level was sent successfully. Thanks a lot!</div>";
            resetLevel();
        } else {
            $sendResult .= "<div class=\"alert alert-danger\">Internal error.</div>";
        }
    }
}